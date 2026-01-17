package com.fsa_profgroep_4.reservations

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.repository.*
import com.fsa_profgroep_4.reservations.types.*
import io.ktor.server.application.ApplicationEnvironment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ReservationsQuery(
    val reservationRepository: ReservationRepository,
    val vehicleRepository: VehicleRepository
): Query {
    constructor(environment: ApplicationEnvironment) : this(
        RepositoryFactory(environment).createReservationRepository(),
        RepositoryFactory(environment).createVehicleRepository()
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
        val vehicleRepository = vehicleRepository
        val reservationRepository = reservationRepository

        val vehicle = vehicleRepository.findById(vehicleId)
            ?: throw IllegalStateException("No vehicle found with ID '$vehicleId'")

        val reservation = Reservation(
            startDate = startDate,
            endDate = endDate,
            renterId = renterId,
            vehicleId = vehicleId,
            totalCost = (ChronoUnit.DAYS.between(startDate, endDate) + 1) * vehicle.costPerDay,
            status = ReservationStatus.PENDING,
            paid = false,
            createdAt = LocalDate.now(),
        )

        reservationRepository.saveReservation(reservation)
            ?.let { return it }

        throw IllegalStateException("Error while creating reservation")
    }

    @GraphQLDescription("Get specific reservation by id")
    fun getReservation(
        @GraphQLDescription("Reservation Id")
        reservationId: Int
    ): Reservation? {
        val repository = reservationRepository
        return repository.findById(reservationId)
    }

    @GraphQLDescription("Get all reservation for a vehicle by vehicleId")
    fun getReservationsByVehicleId(
        @GraphQLDescription("Vehicle Id")
        vehicleId: Int
    ): List<Reservation> {
        val repository = reservationRepository
        return repository.findByVehicleId(vehicleId)
    }

    @GraphQLDescription("Get all reservation for a renter by renterId")
    fun getReservationsByRenterId(
        @GraphQLDescription("Renter Id")
        renterId: Int
    ): List<Reservation> {
        val repository = reservationRepository
        return repository.findByRenterId(renterId)
    }

    @GraphQLDescription("Update a reservation")
    fun updateReservation(
        @GraphQLDescription("Reservation to update")
        input: ReservationUpdate
    ): Reservation {
        val repository = reservationRepository
        repository.updateReservation(input)
            ?.let { return it }

        throw IllegalStateException("Error while update reservation '${input.id}'")
    }
    @GraphQLDescription("Delete a reservation")
    fun deleteReservation(
        @GraphQLDescription("Id of the to be deleted reservation")
        reservationId: Int
    ): Reservation {
        val repository = reservationRepository
        return repository.deleteReservation(reservationId)
    }
}