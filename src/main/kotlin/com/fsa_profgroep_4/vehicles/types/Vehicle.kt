package com.fsa_profgroep_4.vehicles.types
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.time.LocalDate

@GraphQLDescription("Vehicles owned by owner (verhuurder)")
data class Vehicle (
    val id: String,
    val ownerId: String,
    // add id and owner id to uml?
    val brand: String,
    val model: String,
    val licensePlate: String,
    val vin: String,
    val motValidTill: String,
    val odometerKm: Double,
    val seats: Int,
    val color: String,
    val status: VehicleStatus,
//    val category: VehicleCategory,
    val costPerDay: Double,
)
@GraphQLDescription("Types of vehicle status")
enum class VehicleStatus {
    ACTIVE,
    IN_SERVICE,
    MAINTENANCE,
    DECOMMISSIONED
}

