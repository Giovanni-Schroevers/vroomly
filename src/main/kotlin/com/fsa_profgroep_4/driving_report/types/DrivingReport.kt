package com.fsa_profgroep_4.driving_report.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.time.LocalDate

@GraphQLDescription("Driving report input.")
data class DrivingReportInput(
    @param:GraphQLDescription("Snapshots of the vehicle's location.")
    val locationSnapshots: List<LocationSnapshot>,
    @param:GraphQLDescription("Date the report was taken.")
    val date: LocalDate,
    @param:GraphQLDescription("Maximum acceleration recorded.")
    val maxAcceleration: Double,
)

@GraphQLDescription("Driving snapshot of the vehicles location and time.")
data class LocationSnapshot(
    @param:GraphQLDescription("Latitude of the vehicle.")
    val latitude: Double,
    @param:GraphQLDescription("Longitude of the vehicle.")
    val longitude: Double,
    @param:GraphQLDescription("Time the snapshot was taken.")
    val timestamp: Long
)
