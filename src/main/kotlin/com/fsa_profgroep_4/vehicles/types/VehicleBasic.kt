package com.fsa_profgroep_4.vehicles.types
import com.expediagroup.graphql.generator.annotations.GraphQLDescription


@GraphQLDescription("Basic information for vehicles owned by owner (verhuurder)")
data class VehicleBasic (
    @param:GraphQLDescription("Vehicle unique id")
    val id: Int,
    @param:GraphQLDescription("Vehicle owner's unique id")
    val ownerId: Int,
    @param:GraphQLDescription("Vehicle's brand")
    val brand: String,
    @param:GraphQLDescription("Vehicle's cost per day to rent")
    val costPerDay: Double,
    @param:GraphQLDescription("Vehicle's engine type")
    val engineType: EngineType,
    @param:GraphQLDescription("Average review stars for this vehicle")
    val reviewStars: Double,
)

// This type combines a list of basic vehicle info along with their images, because GraphQL doesn't support returning Pairs or Tuples
data class BasicVehicleInfo(
    val basics: List<VehicleBasic>,
    val images: List<VehicleImage>
)

