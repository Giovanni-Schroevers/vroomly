package com.fsa_profgroep_4.vehicles.types
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.time.LocalDate

@GraphQLDescription("Vehicles owned by owner (verhuurder)")
data class Vehicle (
    @param:GraphQLDescription("Vehicle unique id")
    val id: String,
    @param:GraphQLDescription("Vehicle owner's unique id")
    val ownerId: String,
    // TODO add id and owner id to UML
    @param:GraphQLDescription("Vehicle's brand")
    val brand: String,
    @param:GraphQLDescription("Vehicle's model")
    val model: String,
    @param:GraphQLDescription("Vehicle's license plate")
    val licensePlate: String,
    @param:GraphQLDescription("Vehicle's vin")
    val vin: String,
    @param:GraphQLDescription("Vehicle's MOT Valid till certain date")
    val motValidTill: String,
    @param:GraphQLDescription("Vehicle's odometer in km")
    val odometerKm: Double,
    @param:GraphQLDescription("Amount of seats in the vehicle")
    val seats: Int,
    @param:GraphQLDescription("Vehicle's color")
    val color: String,
    @param:GraphQLDescription("Vehicle current status")
    val status: VehicleStatus,
    @param:GraphQLDescription("Vehicle category/type")
    val category: VehicleCategory,
    @param:GraphQLDescription("Vehicle's cost per day to rent")
    val costPerDay: Double,
    @param:GraphQLDescription("Vehicle's engine type")
    val engineType: EngineType,
    // TODO: Add reviewStars to UML
    @param:GraphQLDescription("Average review stars for this vehicle")
    val reviewStars: Double
)
@GraphQLDescription("Types of vehicle status")
enum class VehicleStatus {
    @GraphQLDescription("Vehicle is available for renting")
    ACTIVE,
    @GraphQLDescription("Vehicle is currently unavailable and assigned to service operations")
    IN_SERVICE,
    @GraphQLDescription("Vehicle is undergoing maintenance or inspection and is not available for the time being")
    MAINTENANCE,
    @GraphQLDescription("Vehicle is permanently removed from operation")
    DECOMMISSIONED
}

// TODO: Change the UML to match this VehicleCategory enum
enum class VehicleCategory {
    SEDAN,
    SUV,
    HATCHBACK,
    CONVERTIBLE,
    COUPE,
    WAGON,
    VAN,
    TRUCK
}

// TODO: Change the UML to match this EngineType enum
enum class EngineType {
    PETROL,
    DIESEL,
    ELECTRIC,
    HYBRID
}

