package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.repository.*
import com.fsa_profgroep_4.vehicles.types.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PostgresVehicleRepository(jdbc: String, user: String, password: String): VehicleRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    /** ========================================================
     *                     CREATE FUNCTIONS
     *  ======================================================== */

    @OptIn(ExperimentalTime::class)
    override fun saveVehicle(vehicle: Vehicle): Vehicle? {
        return try {
            transaction {
                try {
                    // --- ENGINE TYPE ---
                    val engineTypeId = EngineTypeTable
                        .select(EngineTypeTable.Id)
                        .where { EngineTypeTable.Code eq vehicle.engineType.name }
                        .firstOrNull()?.get(EngineTypeTable.Id)
                        ?: run {
                            val insertStmt = EngineTypeTable.insert {
                                it[Code] = vehicle.engineType.name
                                it[Description] = vehicle.engineType.name
                            }
                            val returned = insertStmt.resultedValues?.firstOrNull()
                            returned?.get(EngineTypeTable.Id)
                                ?: throw IllegalStateException("Failed to insert or retrieve EngineType '${vehicle.engineType.name}'")
                        }

                    // --- VEHICLE MODEL ---
                    val vehicleModelId = VehicleModelTable
                        .selectAll()
                        .where {
                            (VehicleModelTable.Brand eq vehicle.brand) and
                                    (VehicleModelTable.Model eq vehicle.model) and
                                    (VehicleModelTable.Year eq vehicle.year)
                        }
                        .firstOrNull()?.get(VehicleModelTable.Id)
                        ?: run {
                            val insertStmt = VehicleModelTable.insert {
                                it[Brand] = vehicle.brand
                                it[Model] = vehicle.model
                                it[Color] = vehicle.color
                                it[Year] = vehicle.year
                                it[Category] = vehicle.category.name
                                it[Seats] = vehicle.seats
                                it[EngineTypeId] = engineTypeId
                            }
                            val returned = insertStmt.resultedValues?.firstOrNull()
                            returned?.get(VehicleModelTable.Id)
                                ?: throw IllegalStateException("Failed to insert or retrieve VehicleModel '${vehicle.brand} ${vehicle.model} ${vehicle.year}'")
                        }

                    // --- VEHICLE ---
                    val vehicleId = run {
                        val insertStmt = VehicleTable.insert {
                            it[LicensePlate] = vehicle.licensePlate
                            it[Status] = vehicle.status.name
                            it[Vin] = vehicle.vin
                            it[ReviewStars] = vehicle.reviewStars
                            it[VehicleModelId] = vehicleModelId
                            it[OwnerId] = vehicle.ownerId
                        }

                        val returned = insertStmt.resultedValues?.firstOrNull()
                        returned?.get(VehicleTable.Id)
                            ?: throw IllegalStateException("Failed to insert or retrieve VehicleModel '${vehicle.brand} ${vehicle.model} ${vehicle.year}'")
                    }

//                    // --- OWNERSHIP ---
//                    OwnershipTable.insert {
//                        it[UserId] = vehicle.ownerId
//                        it[VehicleId] = vehicleId
//                    }

                    // --- ODOMETER ---
                    if (vehicle.odometerKm > 0) {
                        OdometerTable.insert {
                            it[VehicleId] = vehicleId
                            it[Mileage] = vehicle.odometerKm.toBigDecimal()
                            it[Date] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        }
                    }

                    // Return the inserted vehicle
                    findById(vehicleId)

                } catch (e: Exception) {
                    // Catch any exception inside the transaction and throw a descriptive one
                    throw IllegalStateException("Failed to save vehicle '${vehicle.licensePlate}': ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            // Outer catch to log and rethrow for GraphQL
            println("saveVehicle() error: ${e.message}")
            throw IllegalStateException("Error saving vehicle '${vehicle.licensePlate}': ${e.message}", e)
        }
    }

    override fun addImageToVehicle(vehicleId: Int, imageUrl: String, number: Int?): Vehicle? {
        return transaction {
            try {
                // Determine the image number to use (append if null)
                val targetNumber = number ?: VehicleImageTable
                    .selectAll()
                    .where { VehicleImageTable.VehicleId eq vehicleId }
                    .count()
                    .toInt()

                // Fetch all affected images with number >= targetNumber, ordered DESC so we update highest first
                val affected = VehicleImageTable
                    .selectAll()
                    .where {
                        (VehicleImageTable.VehicleId eq vehicleId) and
                                (VehicleImageTable.Number greaterEq targetNumber)
                    }
                    .orderBy(VehicleImageTable.Number, SortOrder.DESC)
                    .map { row -> row[VehicleImageTable.Id] to row[VehicleImageTable.Number] }

                // Update each row individually (highest first) to avoid unique-key conflicts
                affected.forEach { (id, currentNumber) ->
                    VehicleImageTable.update({ VehicleImageTable.Id eq id }) {
                        it[Number] = currentNumber + 1
                    }
                }

                // Insert the new image at targetNumber
                VehicleImageTable.insert {
                    it[VehicleId] = vehicleId
                    it[Number] = targetNumber
                    it[Url] = imageUrl
                }

                // Return updated vehicle
                findById(vehicleId)
            } catch (e: Exception) {
                throw IllegalStateException("Failed to add image to vehicle ID $vehicleId: ${e.message}", e)
            }
        }
    }




    /** ========================================================
     *                      READ FUNCTIONS
     *  ======================================================== */

    override fun findById(id: Int): Vehicle? = transaction {
        val query = (VehicleTable
            .leftJoin(VehicleModelTable, { VehicleTable.VehicleModelId }, { VehicleModelTable.Id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.EngineTypeId }, { EngineTypeTable.Id }))
            .selectAll()
            .where { VehicleTable.Id eq id }
            .limit(1)
            .firstOrNull()

        query?.let { mapResultRowToVehicle(it) }
    }

    override fun findByOwnerId(ownerId: Int): List<Vehicle> = transaction {
        (VehicleTable
            .leftJoin(VehicleModelTable, { VehicleTable.VehicleModelId }, { VehicleModelTable.Id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.EngineTypeId }, { EngineTypeTable.Id }))
            .selectAll()
            .where { VehicleTable.OwnerId eq ownerId }
            .map { row -> mapResultRowToVehicle(row) }
    }

    override fun getAllVehicles(): List<Vehicle> = transaction {
        (VehicleTable
            .leftJoin(VehicleModelTable, { VehicleTable.VehicleModelId }, { VehicleModelTable.Id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.EngineTypeId }, { EngineTypeTable.Id }))
            .selectAll()
            .map { row -> mapResultRowToVehicle(row) }
    }

    /** ========================================================
     *                      UPDATE FUNCTIONS
     *  ======================================================== */

    @OptIn(ExperimentalTime::class)
    override fun updateVehicle(vehicle: VehicleUpdate): Vehicle? {
        return try {
            transaction {
                try {
                    if (vehicle.id == 0)
                        throw IllegalArgumentException("Vehicle ID must be present for this update operation.")

                    // --- Fetch current vehicle (for merging) ---
                    val existingVehicle = findById(vehicle.id)
                        ?: throw IllegalStateException("Vehicle with ID ${vehicle.id} not found.")

                    // --- ENGINE TYPE ---
                    val engineTypeId = vehicle.engineType?.name?.let { engineName ->
                        EngineTypeTable
                            .select(EngineTypeTable.Id)
                            .where { EngineTypeTable.Code eq engineName }
                            .firstOrNull()?.get(EngineTypeTable.Id)
                            ?: run {
                                val insertStmt = EngineTypeTable.insert {
                                    it[Code] = engineName
                                    it[Description] = engineName
                                }
                                insertStmt.resultedValues?.firstOrNull()?.get(EngineTypeTable.Id)
                                    ?: throw IllegalStateException("Failed to insert or retrieve EngineType '$engineName'")
                            }
                    } ?: run {
                        // Use existing engine type
                        VehicleModelTable
                            .selectAll()
                            .where { VehicleModelTable.Id eq vehicle.id }
                            .firstOrNull()?.get(VehicleModelTable.EngineTypeId)
                            ?: throw IllegalStateException("Failed to retrieve existing EngineType for VehicleModel ID ${existingVehicle.vehicleModelId}")
                    }

                    // --- VEHICLE MODEL ---
                    val vehicleModelId = existingVehicle.vehicleModelId ?: run {
                        VehicleModelTable
                            .selectAll()
                            .where {
                                (VehicleModelTable.Brand eq (vehicle.brand ?: existingVehicle.brand)) and
                                        (VehicleModelTable.Model eq (vehicle.model ?: existingVehicle.model)) and
                                        (VehicleModelTable.Year eq (vehicle.year ?: existingVehicle.year))
                            }
                            .firstOrNull()?.get(VehicleModelTable.Id)
                            ?: run {
                                val insertStmt = VehicleModelTable.insert {
                                    it[Brand] = vehicle.brand ?: existingVehicle.brand
                                    it[Model] = vehicle.model ?: existingVehicle.model
                                    it[Year] = vehicle.year ?: existingVehicle.year
                                    it[Category] = (vehicle.category ?: existingVehicle.category).name
                                    it[Seats] = vehicle.seats ?: existingVehicle.seats
                                    it[EngineTypeId] = engineTypeId
                                }
                                insertStmt.resultedValues?.firstOrNull()?.get(VehicleModelTable.Id)
                                    ?: throw IllegalStateException("Failed to insert or retrieve VehicleModel.")
                            }
                    }

                    // Update existing VehicleModel if it matches
                    if(vehicleModelId == existingVehicle.vehicleModelId){
                        VehicleModelTable.update(where = { VehicleModelTable.Id eq vehicleModelId }) {
                            it[Brand] = vehicle.brand ?: existingVehicle.brand
                            it[Model] = vehicle.model ?: existingVehicle.model
                            it[Year] = vehicle.year ?: existingVehicle.year
                            it[Category] = (vehicle.category ?: existingVehicle.category).name
                            it[Seats] = vehicle.seats ?: existingVehicle.seats
                            it[EngineTypeId] = engineTypeId
                        }
                    }

                    // --- VEHICLE ---
                    VehicleTable.update({ VehicleTable.Id eq vehicle.id }) {
                        vehicle.licensePlate?.let { lp -> it[LicensePlate] = lp }
                        vehicle.status?.let { st -> it[Status] = st.name }
                        vehicle.vin?.let { vin -> it[Vin] = vin }
                        vehicle.ownerId?.let { owner -> it[OwnerId] = owner }
                        it[VehicleModelId] = vehicleModelId
                    }

//                    // --- OWNERSHIP ---
//                    vehicle.ownerId?.let { ownerId ->
//                        OwnershipTable.update({ OwnershipTable.VehicleId eq vehicle.id }) {
//                            it[UserId] = ownerId
//                        }
//                    }

                    // --- ODOMETER ---
                    vehicle.odometerKm?.let { odometer ->
                        if (odometer > 0) {
                            val updated = OdometerTable.update({ OdometerTable.VehicleId eq vehicle.id }) {
                                it[Mileage] = odometer.toBigDecimal()
                                it[Date] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            }
                            if (updated == 0) {
                                // no existing odometer entry? insert one
                                OdometerTable.insert {
                                    it[VehicleId] = vehicle.id
                                    it[Mileage] = odometer.toBigDecimal()
                                    it[Date] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                                }
                            }
                        }
                    }
                    // Return the updated vehicle
                    findById(vehicle.id)
                } catch (e: Exception) {
                    throw IllegalStateException("Failed to update vehicle '${vehicle.licensePlate ?: vehicle.id}': ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            println("updateVehicle() error: ${e.message}")
            throw IllegalStateException("Error updating vehicle '${vehicle.licensePlate ?: vehicle.id}': ${e.message}", e)
        }
    }

    /** ========================================================
     *                      DELETE FUNCTIONS
     *  ======================================================== */

    override fun deleteVehicleById(vehicleId: Int): Vehicle {
        return transaction {
            val vehicle = findById(vehicleId)
                ?: throw IllegalStateException("Vehicle with ID $vehicleId not found.")

            OdometerTable.deleteWhere { OdometerTable.VehicleId eq vehicleId }
            LocationTable.deleteWhere { LocationTable.VehicleId eq vehicleId }
            MaintenanceTable.deleteWhere { MaintenanceTable.VehicleId eq vehicleId }
            ReservationTable.deleteWhere { ReservationTable.VehicleId eq vehicleId }
            VehicleImageTable.deleteWhere { VehicleImageTable.VehicleId eq vehicleId }

            val rowsDeleted = VehicleTable.deleteWhere { VehicleTable.Id eq vehicleId }

            if (rowsDeleted == 0) {
                throw IllegalStateException("Failed to delete vehicle with ID $vehicleId — not found or already deleted.")
            }

            VehicleModelTable.deleteWhere {
                (VehicleModelTable.Brand eq vehicle.brand) and
                        (VehicleModelTable.Model eq vehicle.model) and
                        (VehicleModelTable.Year eq vehicle.year) and
                        (VehicleModelTable.Seats eq vehicle.seats)
            }

            vehicle
        }
    }

    override fun removeImageFromVehicle(vehicleId: Int, imageId: Int): Vehicle? {
        return transaction {
            try {
                // Delete the image
                val rowsDeleted = VehicleImageTable.deleteWhere {
                    (VehicleImageTable.VehicleId eq vehicleId) and
                            (VehicleImageTable.Id eq imageId)
                }

                if (rowsDeleted == 0) {
                    throw IllegalStateException("No image with ID $imageId found for vehicle ID $vehicleId.")
                }

                // Renumber remaining images
                val remainingImages = VehicleImageTable
                    .selectAll()
                    .where { VehicleImageTable.VehicleId eq vehicleId }
                    .orderBy(VehicleImageTable.Number, SortOrder.ASC)
                    .map { it[VehicleImageTable.Id] }

                remainingImages.forEachIndexed { index, id ->
                    VehicleImageTable.update({ VehicleImageTable.Id eq id }) {
                        it[Number] = index
                    }
                }

                // Return the updated vehicle
                findById(vehicleId)
            } catch (e: Exception) {
                throw IllegalStateException("Failed to remove image ID $imageId from vehicle ID $vehicleId: ${e.message}", e)
            }
        }
    }


    /** ========================================================
     *                      OTHER FUNCTIONS
     *  ======================================================== */

    fun getLatestOdometer(vehicleId: Int): Double = transaction {
        OdometerTable
            .select(OdometerTable.Mileage)
            .where { OdometerTable.VehicleId eq vehicleId }
            .orderBy(OdometerTable.Date, SortOrder.DESC)
            .limit(1)
            .firstOrNull()?.get(OdometerTable.Mileage)?.toDouble() ?: 0.0
    }

    fun mapResultRowToVehicle(row: ResultRow): Vehicle = Vehicle(
        id = row[VehicleTable.Id],
        ownerId = row[VehicleTable.OwnerId],
        brand = row[VehicleModelTable.Brand],
        model = row[VehicleModelTable.Model],
        year = row[VehicleModelTable.Year],
        licensePlate = row[VehicleTable.LicensePlate],
        vin = row[VehicleTable.Vin],
        motValidTill = "",
        odometerKm = getLatestOdometer(row[VehicleTable.Id]),
        seats = row[VehicleModelTable.Seats],
        color = row[VehicleModelTable.Color],
        status = VehicleStatus.valueOf(row[VehicleTable.Status]),
        category = VehicleCategory.valueOf(row[VehicleModelTable.Category]),
        costPerDay = 0.0,
        engineType = EngineType.valueOf(row[EngineTypeTable.Code]),
        reviewStars = row[VehicleTable.ReviewStars],
        vehicleModelId = row[VehicleModelTable.Id],
        zeroToHundred = row[VehicleModelTable.ZeroToHundred],
        images = row[VehicleTable.Id].let { vehicleId ->
            VehicleImageTable
                .selectAll()
                .where{ VehicleImageTable.VehicleId eq vehicleId }
                .orderBy(VehicleImageTable.Number, SortOrder.ASC)
                .map { imageRow ->
                    VehicleImage(
                        id = imageRow[VehicleImageTable.Id],
                        vehicleId = imageRow[VehicleImageTable.VehicleId],
                        url = imageRow[VehicleImageTable.Url],
                        number = imageRow[VehicleImageTable.Number]
                    )
                }
        }
    )

    override fun getVehicleTcoData(vehicleId: Int): VehicleTcoData? = transaction {
        VehicleTcoDataTable
            .selectAll()
            .where { VehicleTcoDataTable.VehicleId eq vehicleId }
            .firstOrNull()
            ?.let { row ->
                VehicleTcoData(
                    vehicleId = row[VehicleTcoDataTable.VehicleId],
                    acquisitionCost = row[VehicleTcoDataTable.AcquisitionCost].toDouble(),
                    currentMarketValue = row[VehicleTcoDataTable.CurrentMarketValue].toDouble(),
                    maintenanceCosts = row[VehicleTcoDataTable.MaintenanceCosts].toDouble(),
                    fuelConsumptionPer100Km = row[VehicleTcoDataTable.FuelConsumptionPer100Km].toDouble(),
                    fuelPricePerLiter = row[VehicleTcoDataTable.FuelPricePerLiter].toDouble(),
                    insuranceCostsPerYear = row[VehicleTcoDataTable.InsuranceCostsPerYear].toDouble(),
                    taxAndRegistrationPerYear = row[VehicleTcoDataTable.TaxAndRegistrationPerYear].toDouble(),
                    yearsOwned = row[VehicleTcoDataTable.YearsOwned]
                )
            }
    }

    override fun saveVehicleTcoData(data: VehicleTcoData): VehicleTcoData? = transaction {
        try {
            VehicleTcoDataTable.insert {
                it[VehicleId] = data.vehicleId
                it[AcquisitionCost] = data.acquisitionCost.toBigDecimal()
                it[CurrentMarketValue] = data.currentMarketValue.toBigDecimal()
                it[MaintenanceCosts] = data.maintenanceCosts.toBigDecimal()
                it[FuelConsumptionPer100Km] = data.fuelConsumptionPer100Km.toBigDecimal()
                it[FuelPricePerLiter] = data.fuelPricePerLiter.toBigDecimal()
                it[InsuranceCostsPerYear] = data.insuranceCostsPerYear.toBigDecimal()
                it[TaxAndRegistrationPerYear] = data.taxAndRegistrationPerYear.toBigDecimal()
                it[YearsOwned] = data.yearsOwned
            }
            getVehicleTcoData(data.vehicleId)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to save TCO data for vehicle ${data.vehicleId}: ${e.message}", e)
        }
    }


    override fun updateVehicleTcoData(data: VehicleTcoDataInput): VehicleTcoData? = transaction {
        try {
            val rowsUpdated = VehicleTcoDataTable.update(
                where = { VehicleTcoDataTable.VehicleId eq data.vehicleId }
            ) {
                data.acquisitionCost?.let { v -> it[AcquisitionCost] = v.toBigDecimal() }
                data.currentMarketValue?.let { v -> it[CurrentMarketValue] = v.toBigDecimal() }
                data.maintenanceCosts?.let { v -> it[MaintenanceCosts] = v.toBigDecimal() }
                data.fuelConsumptionPer100Km?.let { v -> it[FuelConsumptionPer100Km] = v.toBigDecimal() }
                data.fuelPricePerLiter?.let { v -> it[FuelPricePerLiter] = v.toBigDecimal() }
                data.insuranceCostsPerYear?.let { v -> it[InsuranceCostsPerYear] = v.toBigDecimal() }
                data.taxAndRegistrationPerYear?.let { v -> it[TaxAndRegistrationPerYear] = v.toBigDecimal() }
                data.yearsOwned?.let { v -> it[YearsOwned] = v }
            }

            if (rowsUpdated == 0) {
                throw IllegalStateException("No TCO data found for vehicle ${data.vehicleId}")
            }
            getVehicleTcoData(data.vehicleId)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to update TCO data for vehicle ${data.vehicleId}: ${e.message}", e)
        }
    }
}