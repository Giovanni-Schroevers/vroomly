package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.reservations.Reservation

interface ReservationRepository {
    fun findById(id: Int) : Reservation
    fun findByOwnerId(ownerId: Int) : List<Reservation>
    fun saveReservation(reservation: Reservation) : Reservation
    fun deleteReservation(reservation: Reservation)
    fun updateReservation(reservation: Reservation)
}