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
            .leftJoin(VehicleModelTable, { VehicleTable.vehicleModelId }, { VehicleModelTable.id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.engineTypeId }, { EngineTypeTable.id })
            .leftJoin(OwnershipTable, { VehicleTable.id }, { OwnershipTable.vehicleId }))
            .select(
                VehicleTable.id,
                VehicleTable.licensePlate,
                VehicleTable.status,
                VehicleTable.vin,
                VehicleModelTable.brand,
                VehicleModelTable.model,
                VehicleModelTable.category,
                VehicleModelTable.seats,
                EngineTypeTable.code,
                OwnershipTable.userId
            )
            .where { VehicleTable.id eq id }
            .limit(1)
            .firstOrNull()

        query?.let { mapResultRowToVehicle(it) }
    }

    override fun findByOwnerId(ownerId: Int): List<Vehicle> = transaction {
        (VehicleTable
            .leftJoin(VehicleModelTable, { VehicleTable.vehicleModelId }, { VehicleModelTable.id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.engineTypeId }, { EngineTypeTable.id })
            .innerJoin(OwnershipTable, { VehicleTable.id }, { OwnershipTable.vehicleId }))
            .select(
                VehicleTable.id,
                VehicleTable.licensePlate,
                VehicleTable.status,
                VehicleTable.vin,
                VehicleModelTable.brand,
                VehicleModelTable.model,
                VehicleModelTable.category,
                VehicleModelTable.seats,
                EngineTypeTable.code,
                OwnershipTable.userId
            )
            .where { OwnershipTable.userId eq ownerId }
            .map { row -> mapResultRowToVehicle(row) }
    }


    override fun getAllVehicles(): List<Vehicle> = transaction {
        (VehicleTable
            .leftJoin(VehicleModelTable, { VehicleTable.vehicleModelId }, { VehicleModelTable.id })
            .leftJoin(EngineTypeTable, { VehicleModelTable.engineTypeId }, { EngineTypeTable.id })
            .innerJoin(OwnershipTable, { VehicleTable.id }, { OwnershipTable.vehicleId }))
            .select(
                VehicleTable.id,
                VehicleTable.licensePlate,
                VehicleTable.status,
                VehicleTable.vin,
                VehicleModelTable.brand,
                VehicleModelTable.model,
                VehicleModelTable.category,
                VehicleModelTable.seats,
                EngineTypeTable.code,
                OwnershipTable.userId
            )
            .map { row -> mapResultRowToVehicle(row) }
    }

    @OptIn(ExperimentalTime::class)
    override fun saveVehicle(vehicle: Vehicle): Vehicle? = transaction {
        val engineTypeId = EngineTypeTable
            .select(EngineTypeTable.id)
            .where { EngineTypeTable.code eq vehicle.engineType.name }
            .firstOrNull()?.get(EngineTypeTable.id)
            ?: run {
                val insertStmt = EngineTypeTable.insert {
                    it[code] = vehicle.engineType.name
                    it[description] = vehicle.engineType.name
                }

                val returned = insertStmt.resultedValues?.firstOrNull()
                returned?.get(EngineTypeTable.id)
                    ?: error("Failed to retrieve engineTypeId")
            }

        val vehicleModelId = VehicleModelTable
            .selectAll()
            .where {
                (VehicleModelTable.brand eq vehicle.brand) and
                        (VehicleModelTable.model eq vehicle.model) and
                        (VehicleModelTable.year eq vehicle.year)
            }
            .firstOrNull()?.get(VehicleModelTable.id)
            ?: run {
                val insertStmt = VehicleModelTable.insert {
                    it[brand] = vehicle.brand
                    it[model] = vehicle.model
                    it[year] = vehicle.year
                    it[category] = vehicle.category.name
                    it[seats] = vehicle.seats
                    it[EngineTypeTable.id] = engineTypeId
                }
                val returned = insertStmt.resultedValues?.firstOrNull()
                returned?.get(VehicleModelTable.id)
                    ?: error("Failed to retrieve vehicleModelId")
            }

        val vehicleId = run {
            val insertStmt = VehicleTable.insert {
                it[licensePlate] = vehicle.licensePlate
                it[status] = vehicle.status.name
                it[vin] = vehicle.vin
                it[VehicleModelTable.id] = vehicleModelId
            }
            val returned = insertStmt.resultedValues?.firstOrNull()
            returned?.get(VehicleTable.id)
                ?: error("Failed to retrieve vehicleId")
        }

        OwnershipTable.insert {
            it[userId] = vehicle.ownerId
            it[VehicleTable.id] = vehicleId
        }

        if (vehicle.odometerKm > 0) {
            OdometerTable.insert {
                it[VehicleTable.id] = vehicleId
                it[mileage] = vehicle.odometerKm.toBigDecimal()
                it[date] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }
        }

        findById(vehicleId)
    }

    override fun deleteVehicle(vehicle: Vehicle): Vehicle {
        TODO("Not yet implemented")
    }

    override fun updateVehicle(vehicle: Vehicle): Unit {
        TODO("Not yet implemented")
    }

    fun getLatestOdometer(vehicleId: Int): Double = transaction {
        OdometerTable
            .select(OdometerTable.mileage)
            .where { OdometerTable.vehicleId eq vehicleId }
            .orderBy(OdometerTable.date, SortOrder.DESC)
            .limit(1)
            .firstOrNull()?.get(OdometerTable.mileage)?.toDouble() ?: 0.0
    }

    fun mapResultRowToVehicle(row: ResultRow): Vehicle = Vehicle(
        id = row[VehicleTable.id],
        ownerId = row[OwnershipTable.userId],
        brand = row[VehicleModelTable.brand],
        model = row[VehicleModelTable.model],
        year = row[VehicleModelTable.year],
        licensePlate = row[VehicleTable.licensePlate],
        vin = row[VehicleTable.vin],
        motValidTill = "", // Placeholder
        odometerKm = getLatestOdometer(row[VehicleTable.id]),
        seats = row[VehicleModelTable.seats],
        color = "Unknown", // Placeholder
        status = VehicleStatus.valueOf(row[VehicleTable.status]),
        category = VehicleCategory.valueOf(row[VehicleModelTable.category]),
        costPerDay = 0.0, // Placeholder
        engineType = EngineType.valueOf(row[EngineTypeTable.code]),
        reviewStars = 0.0 // Placeholder
    )
}