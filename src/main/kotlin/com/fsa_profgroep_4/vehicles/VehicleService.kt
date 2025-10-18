package com.fsa_profgroep_4.vehicles

import com.fsa_profgroep_4.vehicles.types.*
import kotlin.String

class VehicleService {
   /*private var mockVehicles = listOf(
        Vehicle(
            id = "1",
            ownerId = "owner1",
            brand = "Toyota",
            model = "Corolla",
            licensePlate = "111-AA-11",
            vin = "L1K23JLK1SS09DF1L2K",
            motValidTill = LocalDate.of(2026, 12, 31).toString(),
            odometerKm = 20000.0,
            seats = 5,
            color = "Silver",
            status = VehicleStatus.ACTIVE,
            costPerDay = 45.0
        ),
        Vehicle(
            id = "2",
            ownerId = "owner1",
            brand = "Volkswagen",
            model = "Golf",
            licensePlate = "222-BB-22",
            vin = "4L234B24JK23454K",
            motValidTill = LocalDate.of(2025, 12, 31).toString(),
            odometerKm = 15000.0,
            seats = 5,
            color = "White",
            status = VehicleStatus.ACTIVE,
            costPerDay = 50.0
        ),
        Vehicle(
            id = "3",
            ownerId = "owner2",
            brand = "BMW",
            model = "3 Series",
            licensePlate = "333-CC-33",
            vin = "2L3J423B4JK23L4J6K",
            motValidTill = LocalDate.of(2026, 6, 20).toString(),
            odometerKm = 27000.0,
            seats = 5,
            color = "Black",
            status = VehicleStatus.MAINTENANCE,
            costPerDay = 75.0
        ),
    ) */

    private val mockVehicles: List<Vehicle> = VehicleHelper.generateMockVehicles(100)
    private val mockImages: List<VehicleImage> = VehicleHelper.generateMockVehicleImages(mockVehicles)

    suspend fun getAllVehiclesByOwner(ownerId: String): List<Vehicle> {
        return mockVehicles.filter { it.ownerId == ownerId }
    }

    suspend fun getVehicleById(id: String): Vehicle {
        return mockVehicles.first { it.id == id }
    }

    suspend fun getBasicVehicleInfoById(ids: List<String>): BasicVehicleInfo {
        val vehicles = mockVehicles.filter { it.id in ids }

        val basics = vehicles.map { vehicle ->
            VehicleBasic(
                id = vehicle.id,
                ownerId = vehicle.ownerId,
                brand = vehicle.brand,
                costPerDay = vehicle.costPerDay,
                engineType = vehicle.engineType,
                reviewStars = vehicle.reviewStars
            )
        }

        val images = vehicles.map { vehicle ->
            mockImages.first { it.vehicleId == vehicle.id }
        }

        return BasicVehicleInfo(basics = basics, images = images)
    }

    suspend fun getAllVehicles(): List<Vehicle> {
        return mockVehicles
    }
}