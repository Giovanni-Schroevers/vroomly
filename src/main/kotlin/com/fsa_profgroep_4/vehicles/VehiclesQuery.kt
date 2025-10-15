package com.fsa_profgroep_4.vehicles

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.vehicles.types.*
import graphql.GraphQLException

class VehiclesQuery: Query {
    private val vehicleService: VehicleService = VehicleService()

    @GraphQLDescription("Get all vehicles from a specific vehicle owner")
    suspend fun vehiclesByOwnerId(
        @GraphQLDescription("Vehicle owner (verhuurder) id")
        ownerId: String
    ): List<Vehicle> {
        return vehicleService.getAllVehiclesByOwner(ownerId)
    }

    @GraphQLDescription("Get detailed info about a specific vehicle")
    suspend fun vehicle(
        @GraphQLDescription("Vehicle Id")
        id: String
    ): Vehicle {
        return vehicleService.getVehicleById(id)
    }
}