package com.fsa_profgroep_4.vehicles

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.fsa_profgroep_4.repository.RepositoryFactory
import com.fsa_profgroep_4.repository.VehicleRepository
import com.fsa_profgroep_4.vehicles.types.VehicleTcoData
import com.fsa_profgroep_4.vehicles.types.VehicleTcoDataInput
import io.ktor.server.application.ApplicationEnvironment

class VehiclesMutation(private val vehicleRepository: VehicleRepository): Mutation {
    constructor(environment: ApplicationEnvironment): this(
        RepositoryFactory(environment).createVehicleRepository()
    )
    private val vehicleService: VehicleService = VehicleService()

    @GraphQLDescription("Save TCO data for a vehicle")
    suspend fun saveVehicleTcoData(
        @GraphQLDescription("TCO data input")
        input: VehicleTcoDataInput
    ): VehicleTcoData? {
        return vehicleRepository.saveVehicleTcoData(
            VehicleTcoData(
                vehicleId = input.vehicleId,
                acquisitionCost = input.acquisitionCost ?: 0.0,
                currentMarketValue = input.currentMarketValue ?: 0.0,
                maintenanceCosts = input.maintenanceCosts ?: 0.0,
                fuelConsumptionPer100Km = input.fuelConsumptionPer100Km ?: 0.0,
                fuelPricePerLiter = input.fuelPricePerLiter ?: 0.0,
                insuranceCostsPerYear = input.insuranceCostsPerYear ?: 0.0,
                taxAndRegistrationPerYear = input.taxAndRegistrationPerYear ?: 0.0,
                yearsOwned = input.yearsOwned ?: 0
            )
        )
    }

    @GraphQLDescription("Update TCO data for a vehicle")
    suspend fun updateVehicleTcoData(
        @GraphQLDescription("TCO data input")
        input: VehicleTcoDataInput
    ): VehicleTcoData? {
        return vehicleRepository.updateVehicleTcoData(input)
    }

}