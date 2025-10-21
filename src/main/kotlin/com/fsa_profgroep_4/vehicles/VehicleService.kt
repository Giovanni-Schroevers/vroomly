package com.fsa_profgroep_4.vehicles

import com.fsa_profgroep_4.repository.VehicleRepository
import com.fsa_profgroep_4.vehicles.types.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class VehicleService {
    val semaphoreBulk = Semaphore(10) // max 10 concurrent inserts for seeding
    val semaphore = Semaphore(3) // max 3 concurrent reads/writes for normal operations
    /** ========================================================
     *                     CREATE FUNCTIONS
     *  ======================================================== */

    /** ========================================================
     *                      READ FUNCTIONS
     *  ======================================================== */
    suspend fun getAllVehicles(repository: VehicleRepository): List<Vehicle> {
        semaphore.withPermit {
            val foundVehicles = repository.getAllVehicles()
            if (foundVehicles.isNotEmpty())
                return foundVehicles
        }
        return listOf()
    }

    suspend fun getVehiclesByOwnerId(repository: VehicleRepository, ownerId: Int): List<Vehicle> {
        semaphore.withPermit {
            val foundVehicle = repository.findByOwnerId(ownerId)
            if (foundVehicle.isNotEmpty())
                return foundVehicle
        }
        return listOf()
    }

    suspend fun getVehicleById(repository: VehicleRepository, vehicleId: Int): Vehicle? {
        semaphore.withPermit {
            val foundVehicle = repository.findById(vehicleId)
            if (foundVehicle != null)
                return foundVehicle
        }
        return null
    }
    /** ========================================================
     *                     UPDATE FUNCTIONS
     *  ======================================================== */


    /** ========================================================
     *                     DELETE FUNCTIONS
     *  ======================================================== */


    /** ========================================================
     *                     OTHER FUNCTIONS
     *  ======================================================== */
    suspend fun calculateTco(input: TcoInput, vehicle: Vehicle?): VehicleTco {
        val depreciation = input.AcquisitionCost - input.currentMarketValue

        // Make sure vehicle is not null
        if (vehicle == null)
            throw IllegalArgumentException("Vehicle not found for TCO calculation" )

        val totalKmDriven = vehicle.odometerKm
        val fuelConsumed = (totalKmDriven / 100) * input.fuelConsumptionPer100Km
        val fuelCosts = fuelConsumed * input.fuelPricePerLiter

        val totalInsuranceCosts = input.insuranceCostsPerYear * input.yearsOwned

        val totalTaxAndRegistration = input.taxAndRegistrationPerYear * input.yearsOwned

        val totalCost =
            depreciation + input.maintenanceCosts + fuelCosts + totalInsuranceCosts + totalTaxAndRegistration
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

    suspend fun getBasicVehicleInfo(vehicles: List<Vehicle>): List<VehicleBasic> {
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

    return basics
    }

    suspend fun vehicleDataSeeder(repository: VehicleRepository, amountToSeed: Int): List<Vehicle> {
        val addedVehicles = mutableListOf<Vehicle>()

        VehicleHelper.generateVehicles(amountToSeed).forEach { vehicle ->
            semaphoreBulk.withPermit {
                val savedVehicle = repository.saveVehicle(vehicle)
                if (savedVehicle != null)
                    addedVehicles.add(savedVehicle)
            }
        }

        return addedVehicles
    }
}
