package com.fsa_profgroep_4.driving_report

import com.expediagroup.graphql.server.operations.Mutation
import com.fsa_profgroep_4.auth.requirePrincipal
import com.fsa_profgroep_4.driving_report.types.DrivingReportInput
import com.fsa_profgroep_4.repository.DrivingReportRepository
import com.fsa_profgroep_4.repository.RepositoryFactory
import com.fsa_profgroep_4.repository.VehicleRepository
import graphql.GraphQLException
import graphql.schema.DataFetchingEnvironment
import io.ktor.server.application.ApplicationEnvironment
import kotlin.math.max

class DrivingReportMutation(val environment: ApplicationEnvironment, private val drivingReportRepository: DrivingReportRepository, private val vehicleRepository: VehicleRepository): Mutation {
    constructor(environment: ApplicationEnvironment): this(environment, RepositoryFactory(environment).createDrivingReportRepository(), RepositoryFactory(environment).createVehicleRepository())

    @Suppress("unused")
    suspend fun saveDrivingReport(input: DrivingReportInput, env: DataFetchingEnvironment): String {
        val token = requirePrincipal(env)

        try {
            val reservation = drivingReportRepository.getCurrentReservation(token.payload.getClaim("id").asInt())
                ?: throw GraphQLException("You don't have a current reservation")

            val incidents = mutableMapOf<String, Double>()
            var drivingScore = 100.0
            val zeroToHundredTime = vehicleRepository.findById(reservation.vehicleId)?.zeroToHundred ?: 10.0
            val safeDrivingService = SafeDrivingService(input.locationSnapshots)

            val unsafeAcceleration = safeDrivingService.checkForUnsafeAcceleration(zeroToHundredTime, input.maxAcceleration)

            if (unsafeAcceleration.first){
                incidents["unsafe acceleration"] = unsafeAcceleration.second
                drivingScore -= unsafeAcceleration.second
            }

            for (violation in safeDrivingService.checkForMaxSpeedViolations()) {
                incidents[violation.key] = violation.value
                drivingScore -= violation.value
            }

            drivingReportRepository.saveDrivingReport(reservation, max(0.0, drivingScore), incidents, input.date)
        } catch (e: Exception) {
            throw GraphQLException(e.message)
        }

        return "Drive has been saved successfully"
    }
}