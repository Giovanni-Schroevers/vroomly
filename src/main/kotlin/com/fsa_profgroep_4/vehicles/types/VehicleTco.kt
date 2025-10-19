package com.fsa_profgroep_4.vehicles.types
import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Vehicles owned by owner (verhuurder)")
data class VehicleTco (
    @param:GraphQLDescription("Vehicle unique id")
    val id: Int,
    @param:GraphQLDescription("Purchase price of the vehicle")
    val purchasePrice: Double,
    @param:GraphQLDescription("Current value value of the vehicle")
    val currentValue: Double,
    @param:GraphQLDescription("Total depreciation")
    val depreciation: Double,
    @param:GraphQLDescription("Total maintenance cost")
    val maintenanceCost: Double,
    @param:GraphQLDescription("Total fuel cost")
    val fuelCost: Double,
    @param:GraphQLDescription("Insurance costs per year")
    val insuranceCostPerYear: Double,
    @param:GraphQLDescription("Vehicle tax and registration costs per year")
    val taxAndRegistrationPerYear: Double,
    @param:GraphQLDescription("TCO per kilometer")
    val costPerKilometer: Double,
    @param:GraphQLDescription("Number of years of ownership")
    val yearOwned: Int,
)

@GraphQLDescription("Input for TCO calculation")
data class TcoInput(
    @param:GraphQLDescription("Vehicle unique id")
    val vehicleId: String,
    @param:GraphQLDescription("Original purchase price")
    val purchasePrice: Double,
    @param:GraphQLDescription("Current value value of the vehicle")
    val currentValue: Double,
    @param:GraphQLDescription("Total maintenance cost")
    val maintenanceCosts: Double,
    @param:GraphQLDescription("Average fuel consumption in liters per 100km")
    val fuelConsumptionPer100Km: Double,
    @param:GraphQLDescription("Average fuel price per liter")
    val fuelPricePerLiter: Double,
    @param:GraphQLDescription("Insurance costs per year")
    val insuranceCostsPerYear: Double,
    @param:GraphQLDescription("Vehicle tax and registration costs per year")
    val taxAndRegistrationPerYear: Double,
    @param:GraphQLDescription("Number of years of ownership")
    val yearsOwned: Double
)