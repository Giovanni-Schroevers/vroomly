package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.vehicles.types.TCO
import com.fsa_profgroep_4.vehicles.types.Vehicle
import com.fsa_profgroep_4.vehicles.types.VehicleTcoData
import com.fsa_profgroep_4.vehicles.types.VehicleTcoDataInput
import com.fsa_profgroep_4.vehicles.types.VehicleUpdate

interface VehicleRepository {
    fun findById(id: Int): Vehicle?
    fun findByOwnerId(ownerId: Int): List<Vehicle>
    fun getAllVehicles(): List<Vehicle>
    fun saveVehicle(vehicle: Vehicle): Vehicle?
    fun deleteVehicleById(vehicleId: Int): Vehicle?
    fun updateVehicle(vehicle: VehicleUpdate): Vehicle?
    fun addImageToVehicle(vehicleId: Int, imageUrl: String, number: Int?): Vehicle?
    fun removeImageFromVehicle(vehicleId: Int, imageId: Int): Vehicle?
    fun getVehicleTcoData(vehicleId: Int): VehicleTcoData?
    fun saveVehicleTcoData(data: VehicleTcoData): VehicleTcoData?
    fun updateVehicleTcoData(data: VehicleTcoDataInput): VehicleTcoData?
}
