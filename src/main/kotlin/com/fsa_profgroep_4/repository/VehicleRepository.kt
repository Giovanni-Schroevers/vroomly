package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.vehicles.types.Vehicle

interface VehicleRepository {
    fun findById(id: Int): Vehicle?
    fun findByOwnerId(ownerId: Int): List<Vehicle>
    fun getAllVehicles(): List<Vehicle>
    fun saveVehicle(vehicle: Vehicle): Vehicle?
    fun deleteVehicle(vehicle: Vehicle): Vehicle
    fun updateVehicle(vehicle: Vehicle): Unit
}
