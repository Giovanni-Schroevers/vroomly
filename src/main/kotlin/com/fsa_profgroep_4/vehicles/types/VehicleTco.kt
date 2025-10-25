package com.fsa_profgroep_4.vehicles.types
import com.expediagroup.graphql.generator.annotations.GraphQLDescription


@GraphQLDescription("Total Cost Of Ownership")
data class TCO (
    @param:GraphQLDescription("Total Cost of Ownership value")
    val tcoValue: Double,
)


@GraphQLDescription("Vehicles owned by owner (verhuurder)")
data class VehicleTcoData (
    @param:GraphQLDescription("Vehicle unique id")
    val vehicleId: Int,
    @param:GraphQLDescription("Original purchase price")
    val acquisitionCost: Double,
    @param:GraphQLDescription("Current market value of the vehicle")
    val currentMarketValue: Double,
    @param:GraphQLDescription("Total maintenance costs")
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
    val yearsOwned: Int
    )

@GraphQLDescription("Input for TCO calculation")
data class VehicleTcoDataInput(
    @param:GraphQLDescription("Vehicle unique id")
    val vehicleId: Int,
    @param:GraphQLDescription("Original purchase price")
    val acquisitionCost: Double? = null,
    @param:GraphQLDescription("Current market value of the vehicle")
    val currentMarketValue: Double? = null,
    @param:GraphQLDescription("Total maintenance costs")
    val maintenanceCosts: Double? = null,
    @param:GraphQLDescription("Average fuel consumption in liters per 100km")
    val fuelConsumptionPer100Km: Double? = null,
    @param:GraphQLDescription("Average fuel price per liter")
    val fuelPricePerLiter: Double? = null,
    @param:GraphQLDescription("Insurance costs per year")
    val insuranceCostsPerYear: Double? = null,
    @param:GraphQLDescription("Vehicle tax and registration costs per year")
    val taxAndRegistrationPerYear: Double? = null,
    @param:GraphQLDescription("Number of years of ownership")
    val yearsOwned: Int? = null
)