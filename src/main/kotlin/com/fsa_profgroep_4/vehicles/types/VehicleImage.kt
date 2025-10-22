package com.fsa_profgroep_4.vehicles.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

// TODO: Add VehicleImage class in UML
@GraphQLDescription("Images associated with vehicles")
data class VehicleImage (
    @param:GraphQLDescription("Image's unique id")
    val id: Int? = null,
    @param:GraphQLDescription("Image's vehicle unique id")
    val vehicleId: Int,
    @param:GraphQLDescription("Url to the image resource")
    val url: String,
    @param:GraphQLDescription("What position the image has, 0 is main image")
    val number: Int
)
