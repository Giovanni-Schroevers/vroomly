package com.fsa_profgroep_4.vehicles.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

// TODO: Add VehicleImage class in UML
@GraphQLDescription("Images associated with vehicles")
data class VehicleImage (
    @param:GraphQLDescription("Image's unique id")
    val id: String,
    @param:GraphQLDescription("Image's vehicle unique id")
    val vehicleId: Int,
    @param:GraphQLDescription("Path to the image resource")
    val path: String,
    @param:GraphQLDescription("Type of image ")
    val type: VehicleImageType? = null
)

// TODO: Add VehicleImageType enum in UML
enum class VehicleImageType {
    @GraphQLDescription("Main image of the vehicle")
    MAIN,
    @GraphQLDescription("Additional image of the vehicle")
    ADDITIONAL
}