package com.fsa_profgroep_4.vehicles.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Filters to search vehicles")
data class VehicleFilter(
    @param:GraphQLDescription("Filter by vehicle brand")
    val brand: String? = null,
    @param:GraphQLDescription("Filter by vehicle model")
    val model: String? = null,
    @param:GraphQLDescription("Filter by vehicle category/type")
    val category: VehicleCategory? = null,
    @param:GraphQLDescription("Filter by engine type")
    val engineType: EngineType? = null,
    @param:GraphQLDescription("Filter by vehicle status")
    val status: VehicleStatus? = null,
    @param:GraphQLDescription("Minimum cost per day to rent")
    val minCostPerDay: Double? = null,
    @param:GraphQLDescription("Maximum cost per day to rent")
    val maxCostPerDay: Double? = null,
    @param:GraphQLDescription("Minimum number of seats")
    val minSeats: Int? = null,
    @param:GraphQLDescription("Maximum number of seats")
    val maxSeats: Int? = null
)
