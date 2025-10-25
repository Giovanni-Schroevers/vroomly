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

    suspend fun createVehicle(repository: VehicleRepository, vehicle: Vehicle): Vehicle {
        semaphore.withPermit {
            val createdVehicle = repository.saveVehicle(vehicle)
            if (createdVehicle != null)
                return createdVehicle
        }
        return null!!
    }

    suspend fun addImageToVehicle(repository: VehicleRepository, vehicleId: Int, imageUrl: String, number: Int?): Vehicle {
        semaphore.withPermit {
            val updatedVehicle = repository.addImageToVehicle(vehicleId, imageUrl, number)
            if (updatedVehicle != null)
                return updatedVehicle
        }
        return null!!
    }
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

    suspend fun updateVehicle(repository: VehicleRepository, vehicleUpdate: VehicleUpdate): Vehicle {
        semaphore.withPermit {
            val updatedVehicle = repository.updateVehicle(vehicleUpdate)
            if (updatedVehicle != null)
                return updatedVehicle
        }
        return null!!
    }

    /** ========================================================
     *                     DELETE FUNCTIONS
     *  ======================================================== */

    suspend fun deleteVehicle(repository: VehicleRepository, vehicleId: Int): Vehicle {
        semaphore.withPermit {
            val deletedVehicle = repository.deleteVehicleById(vehicleId)
            if (deletedVehicle != null)
                return deletedVehicle
        }
        return null!!
    }

    suspend fun removeImageFromVehicle(repository: VehicleRepository, vehicleId: Int, imageId: Int): Vehicle {
        semaphore.withPermit {
            val updatedVehicle = repository.removeImageFromVehicle(vehicleId, imageId)
            if (updatedVehicle != null)
                return updatedVehicle
        }
        return null!!
    }
    /** ========================================================
     *                     OTHER FUNCTIONS
     *  ======================================================== */

    suspend fun calculateTcoByVehicleId(repository: VehicleRepository, vehicleId: Int): TCO {
        val vehicle = getVehicleById(repository, vehicleId)
            ?: throw IllegalArgumentException("Vehicle with ID $vehicleId not found")

        val tcoData = repository.getVehicleTcoData(vehicleId)
            ?: throw IllegalArgumentException("TCO data not found for vehicle $vehicleId. Please add TCO data first.")

        return getTcoValue(tcoData, vehicle)
    }

    private fun getTcoValue(data: VehicleTcoData, vehicle: Vehicle): TCO {
        val depreciation = data.acquisitionCost - data.currentMarketValue

        val totalKmDriven = vehicle.odometerKm.toDouble()
        val fuelConsumed = (totalKmDriven / 100.0) * data.fuelConsumptionPer100Km
        val fuelCosts = fuelConsumed * data.fuelPricePerLiter

        val totalInsuranceCosts = data.insuranceCostsPerYear * data.yearsOwned
        val totalTaxAndRegistration = data.taxAndRegistrationPerYear * data.yearsOwned

        val totalCost = depreciation + data.maintenanceCosts + fuelCosts + totalInsuranceCosts + totalTaxAndRegistration
        return TCO(tcoValue = totalCost)
    }

    fun computeConsumption(
        fuelConsumptionPer100Km: Double,
        fuelPricePerLiter: Double
    ): VehicleConsumption {
        val litersPerKm = fuelConsumptionPer100Km / 100.0
        val kmPerLiter =
            if (fuelConsumptionPer100Km > 0.0) 100.0 / fuelConsumptionPer100Km else 0.0
        val costPerKm = litersPerKm * fuelPricePerLiter

        return VehicleConsumption(
            litersPerKm = litersPerKm,
            kmPerLiter = kmPerLiter,
            costPerKm = costPerKm
        )
    }

    fun computeConsumptionForVehicle(
        repo: VehicleRepository,
        vehicleId: Int
    ): VehicleConsumption? {
        val data = repo.getVehicleTcoData(vehicleId) ?: return null
        return computeConsumption(
            fuelConsumptionPer100Km = data.fuelConsumptionPer100Km,
            fuelPricePerLiter = data.fuelPricePerLiter
        )
    }

    fun getBasicVehicleInfo(vehicles: List<Vehicle>): List<VehicleBasic> {
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
