package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.reservations.types.Reservation
import com.fsa_profgroep_4.reservations.types.ReservationUpdate

interface ReservationRepository {
    fun findById(id: Int) : Reservation?
    fun findByRenterId(renterId: Int) : List<Reservation>
    fun findByVehicleId(vehicleId: Int) : List<Reservation>
    fun saveReservation(reservation: Reservation) : Reservation?
    fun deleteReservation(reservationId: Int): Reservation
    fun updateReservation(reservation: ReservationUpdate): Reservation?
}