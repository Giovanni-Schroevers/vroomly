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
        ownerId: Int
    ): List<Vehicle> {
        return vehicleService.getAllVehiclesByOwner(ownerId)
    }

    @GraphQLDescription("Get detailed info about a specific vehicle")
    suspend fun vehicle(
        @GraphQLDescription("Vehicle Id")
        id: Int
    ): Vehicle {
        return vehicleService.getVehicleById(id)
    }

    @GraphQLDescription("Get vehicles with optional filters and pagination")
    suspend fun searchVehicles(
        @GraphQLDescription("Filters to apply to the vehicles")
        filters: VehicleFilter? = null,

        @GraphQLDescription("The amount of vehicles to return")
        paginationAmount: Int,

        @GraphQLDescription("The page of vehicles to return")
        paginationPage: Int
    ): BasicVehicleInfo {
        var vehicles = vehicleService.getAllVehicles() // returns List<Vehicle>

        // Apply filters if provided
        filters?.let {
            vehicles = vehicles.filter { vehicle ->
                (it.brand == null || vehicle.brand.equals(it.brand, ignoreCase = true)) &&
                        (it.engineType == null || vehicle.engineType == it.engineType) &&
                        (it.minCostPerDay == null || vehicle.costPerDay >= it.minCostPerDay) &&
                        (it.maxCostPerDay == null || vehicle.costPerDay <= it.maxCostPerDay)
            }
        }

        // Apply pagination
        val startIndex = if (vehicles.size < paginationAmount) {
            paginationAmount // start at first page if fewer vehicles
        } else {
            (paginationPage - 1) * paginationAmount
        }

        val pagedVehicles = vehicles.drop(startIndex).take(paginationAmount)
        val ids = pagedVehicles.map { it.id }

        // Return basic info and images
        return vehicleService.getBasicVehicleInfoById(ids)
    }

}