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

    override fun findById(id: Int): Vehicle? = transaction {
        val query = (VehicleTable
            .leftJoin(VehicleModelTable, { VehicleTable.VehicleModelId }, { VehicleModelTable.Id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.EngineTypeId }, { EngineTypeTable.Id })
            .innerJoin(OwnershipTable, { VehicleTable.Id }, { OwnershipTable.VehicleId }))
            .selectAll()
            .where { VehicleTable.Id eq id }
            .limit(1)
            .firstOrNull()

        query?.let { mapResultRowToVehicle(it) }
    }

    override fun findByOwnerId(ownerId: Int): List<Vehicle> = transaction {
        (VehicleTable
            .leftJoin(VehicleModelTable, { VehicleTable.VehicleModelId }, { VehicleModelTable.Id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.EngineTypeId }, { EngineTypeTable.Id })
            .innerJoin(OwnershipTable, { VehicleTable.Id }, { OwnershipTable.VehicleId }))
            .selectAll()
            .where { OwnershipTable.UserId eq ownerId }
            .map { row -> mapResultRowToVehicle(row) }
    }


    override fun getAllVehicles(): List<Vehicle> = transaction {
        (VehicleTable
            .leftJoin(VehicleModelTable, { VehicleTable.VehicleModelId }, { VehicleModelTable.Id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.EngineTypeId }, { EngineTypeTable.Id })
            .innerJoin(OwnershipTable, { VehicleTable.Id }, { OwnershipTable.VehicleId }))
            .selectAll()
            .map { row -> mapResultRowToVehicle(row) }
    }

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
                            it[VehicleModelId] = vehicleModelId
                        }

                        val returned = insertStmt.resultedValues?.firstOrNull()
                        returned?.get(VehicleTable.Id)
                            ?: throw IllegalStateException("Failed to insert or retrieve VehicleModel '${vehicle.brand} ${vehicle.model} ${vehicle.year}'")
                    }

                    // --- OWNERSHIP ---
                    OwnershipTable.insert {
                        it[UserId] = vehicle.ownerId
                        it[VehicleId] = vehicleId
                    }

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


    override fun deleteVehicle(vehicle: Vehicle): Vehicle {
        TODO("Not yet implemented")
    }

    override fun updateVehicle(vehicle: Vehicle): Unit {
        TODO("Not yet implemented")
    }

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
        ownerId = row[OwnershipTable.UserId],
        brand = row[VehicleModelTable.Brand],
        model = row[VehicleModelTable.Model],
        year = row[VehicleModelTable.Year],
        licensePlate = row[VehicleTable.LicensePlate],
        vin = row[VehicleTable.Vin],
        motValidTill = "", // Placeholder
        odometerKm = getLatestOdometer(row[VehicleTable.Id]),
        seats = row[VehicleModelTable.Seats],
        color = "Unknown", // Placeholder
        status = VehicleStatus.valueOf(row[VehicleTable.Status]),
        category = VehicleCategory.valueOf(row[VehicleModelTable.Category]),
        costPerDay = 0.0, // Placeholder
        engineType = EngineType.valueOf(row[EngineTypeTable.Code]),
        reviewStars = 0.0 // Placeholder
    )
}