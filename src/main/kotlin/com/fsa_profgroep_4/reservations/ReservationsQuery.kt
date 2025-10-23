package com.fsa_profgroep_4.reservations

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.repository.*
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.datetime.LocalDate

class ReservationsQuery(
    val reservationRepository: ReservationRepository
): Query {
    constructor(environment: ApplicationEnvironment) : this(
        RepositoryFactory(environment).createReservationRepository(),
    )

    @GraphQLDescription("create a new reservation for a vehicle")
    fun createReservation(
        @GraphQLDescription("Vehicle Id")
        vehicleId: Int,
        @GraphQLDescription("Renter Id")
        renterId: Int,
        @GraphQLDescription("Start date of reservation")
        startDate: LocalDate,
        @GraphQLDescription("End date of reservation")
        endDate: LocalDate,
        ): Reservation {
        TODO("Reservation creation")
    }

    @GraphQLDescription("Get specific reservation by id")
    fun getReservation(
        @GraphQLDescription("Reservation Id")
        reservationId: Int
    ): Reservation? {
        TODO("Get specific reservation by id")
    }

    @GraphQLDescription("Get all reservation for a vehicle by vehicleId")
    fun getReservationsByVehicleId(
        @GraphQLDescription("Vehicle Id")
        vehicleId: Int
    ): List<Reservation> {
        TODO("Get all reservation for a vehicle by vehicleId")
    }

    @GraphQLDescription("Get all reservation for a renter by renterId")
    fun getReservationsByRenterId(
        @GraphQLDescription("Renter Id")
        renterId: Int
    ): List<Reservation> {
        TODO("Get all reservation for a renter by renterId")
    }

    @GraphQLDescription("Update a reservation")
    fun updateReservation(
        @GraphQLDescription("Reservation to update")
        reservationUpdate: ReservationUpdate
    ){
        TODO("Reservation update")
    }
    @GraphQLDescription("Delete a reservation")
    fun deleteReservation(
        @GraphQLDescription("Id of the to be deleted reservation")
        reservationId: Int
    ){
        TODO("Reservation delete")
    }
}