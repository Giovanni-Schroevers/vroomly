package com.fsa_profgroep_4.vehicles

import com.fsa_profgroep_4.vehicles.types.*
import java.time.LocalDate
import kotlin.String

class VehicleService {
    private val mockVehicles: List<Vehicle> = VehicleHelper.generateMockVehicles(100)
    private val mockImages: List<VehicleImage> = VehicleHelper.generateMockVehicleImages(mockVehicles)

    suspend fun getAllVehiclesByOwner(ownerId: Int): List<Vehicle> {
        return mockVehicles.filter { it.ownerId == ownerId }
    }

    suspend fun getVehicleById(id: Int): Vehicle {
        return mockVehicles.first { it.id == id }
    }

    suspend fun getBasicVehicleInfoById(ids: List<Int>): BasicVehicleInfo {
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

    suspend fun calculateTco(input: TcoInput, vehicle: Vehicle): VehicleTco {
        val depreciation = input.AcquisitionCost - input.currentMarketValue

        val totalKmDriven = vehicle.odometerKm
        val fuelConsumed = (totalKmDriven / 100) * input.fuelConsumptionPer100Km
        val fuelCosts = fuelConsumed * input.fuelPricePerLiter

        val totalInsuranceCosts = input.insuranceCostsPerYear * input.yearsOwned

        val totalTaxAndRegistration = input.taxAndRegistrationPerYear * input.yearsOwned

        val totalCost = depreciation + input.maintenanceCosts + fuelCosts + totalInsuranceCosts + totalTaxAndRegistration
        val costPerKm = if (totalKmDriven == 0.0) 0.0 else totalCost / totalKmDriven

        return VehicleTco(
            id = input.id,
            AcquisitionCost = input.AcquisitionCost,
            currentMarketValue = input.currentMarketValue,
            depreciation = depreciation,
            maintenanceCost = input.maintenanceCosts,
            fuelCost = fuelCosts,
            insuranceCostPerYear = input.insuranceCostsPerYear,
            taxAndRegistrationPerYear = input.taxAndRegistrationPerYear,
            costPerKilometer = costPerKm,
            yearsOwned = input.yearsOwned,
            tcoValue = totalCost
        )
    }
}