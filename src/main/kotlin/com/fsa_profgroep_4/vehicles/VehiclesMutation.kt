package com.fsa_profgroep_4.vehicles

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.fsa_profgroep_4.repository.RepositoryFactory
import com.fsa_profgroep_4.repository.VehicleRepository
import com.fsa_profgroep_4.vehicles.types.Vehicle
import com.fsa_profgroep_4.vehicles.types.VehicleTcoData
import com.fsa_profgroep_4.vehicles.types.VehicleTcoDataInput
import com.fsa_profgroep_4.vehicles.types.VehicleUpdate
import io.ktor.server.application.ApplicationEnvironment

class VehiclesMutation(private val vehicleRepository: VehicleRepository): Mutation {
    constructor(environment: ApplicationEnvironment): this(
        RepositoryFactory(environment).createVehicleRepository()
    )
    private val vehicleService: VehicleService = VehicleService()

    @GraphQLDescription("Save TCO data for a vehicle")
    suspend fun saveVehicleTcoData(
        @GraphQLDescription("TCO data input")
        input: VehicleTcoDataInput
    ): VehicleTcoData? {
        val repository = vehicleRepository
        return vehicleService.saveVehicleTcoData(repository, input)
    }

    @GraphQLDescription("Update TCO data for a vehicle")
    suspend fun updateVehicleTcoData(
        @GraphQLDescription("TCO data input")
        input: VehicleTcoDataInput
    ): VehicleTcoData? {
        val repository = vehicleRepository
        return vehicleService.updateVehicleTcoData(repository, input)
    }

    /**
     * Updates a vehicle from the database.
     *
     * @return the updated [Vehicle] object.
     */
    @GraphQLDescription("Update the Vehicle's data")
    suspend fun updateVehicle(
        @GraphQLDescription("VehicleUpdate to update")
        vehicle: VehicleUpdate
    ): Vehicle {
        val repository = vehicleRepository
        return vehicleService.updateVehicle(repository, vehicle)
    }
    /**
     * Deletes a vehicle from the database.
     *
     * @return the deleted [Vehicle] object.
     */
    @GraphQLDescription("Deletes a Vehicle")
    suspend fun deleteVehicle(
        @GraphQLDescription("Vehicle Id to delete")
        vehicleId: Int
    ): Vehicle {
        val repository = vehicleRepository
        return vehicleService.deleteVehicle(repository, vehicleId)
    }

    /**
     * Deletes an image from a vehicle in the database.
     *
     * @return the [Vehicle] object the image got deleted from.
     */
    @GraphQLDescription("Deletes an image from the Vehicle")
    suspend fun removeImageFromVehicle(
        @GraphQLDescription("Vehicle Id to remove image from")
        vehicleId: Int,

        @GraphQLDescription("Image Id to remove from vehicle")
        imageId: Int
    ): Vehicle {
        val repository = vehicleRepository
        return vehicleService.removeImageFromVehicle(repository, vehicleId, imageId)
    }

}