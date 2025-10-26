package com.fsa_profgroep_4.reservations.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.time.LocalDate
import kotlin.time.ExperimentalTime

@GraphQLDescription("Reservation for a vehicle")
data class Reservation @OptIn(ExperimentalTime::class) constructor(
    @param:GraphQLDescription("Reservation unique id")
    val id: Int? = null,
    @param:GraphQLDescription("Start date of Reservation")
    val startDate: LocalDate,
    @param:GraphQLDescription("End date of Reservation")
    val endDate: LocalDate,
    @param:GraphQLDescription("Reservation status")
    val status: ReservationStatus,
    @param:GraphQLDescription("Reservation totalCost")
    val totalCost: Double,
    @param:GraphQLDescription("Reservation paid or not")
    val paid: Boolean,
    @param:GraphQLDescription("Reservation creation date")
    val createdAt: LocalDate,
    @param:GraphQLDescription("Reservation vehicle id")
    val vehicleId: Int,
    @param:GraphQLDescription("Reservation id of vehicle renter")
    val renterId: Int,
    @param:GraphQLDescription("Driving reports of the reservation")
    val drivingReports: List<DrivingReport>? = null
)

@GraphQLDescription("Driving report data")
data class DrivingReport(
    @param:GraphQLDescription("Driving report unique id")
    val id: Int,
    @param:GraphQLDescription("Driving report safety score")
    val safetyScore: Double,
    @param:GraphQLDescription("Driving report date")
    val date: LocalDate,
    @param:GraphQLDescription("Violations during the drive")
    val violations: List<Violation>?
)

data class Violation(
    @param:GraphQLDescription("Violation unique id")
    val id: Int,
    @param:GraphQLDescription("Violation description")
    val description: String,
    @param:GraphQLDescription("Violation score deduction")
    val score: Double
)

@GraphQLDescription("Reservation data used for updating existing reservation entries")
data class ReservationUpdate(
    @param:GraphQLDescription("Reservation unique id")
    val id: Int,
    @param:GraphQLDescription("Start date of Reservation")
    val startDate: LocalDate?,
    @param:GraphQLDescription("End date of Reservation")
    val endDate: LocalDate?,
    @param:GraphQLDescription("Reservation status")
    val status: ReservationStatus?,
    @param:GraphQLDescription("Reservation totalCost")
    val totalCost: Double?,
    @param:GraphQLDescription("Reservation paid or not")
    val paid: Boolean?,
    @param:GraphQLDescription("Reservation vehicle id")
    val vehicleId: Int?,
    @param:GraphQLDescription("Reservation id of vehicle renter")
    val renterId: Int?,
)

@GraphQLDescription("Reservation status")
enum class ReservationStatus{
    @GraphQLDescription("Reservation pending")
    PENDING,
    @GraphQLDescription("Reservation confirmed")
    CONFIRMED,
    @GraphQLDescription("Reservation active")
    ACTIVE,
    @GraphQLDescription("Reservation completed")
    COMPLETED,
    @GraphQLDescription("Reservation cancelled")
    CANCELLED,
}