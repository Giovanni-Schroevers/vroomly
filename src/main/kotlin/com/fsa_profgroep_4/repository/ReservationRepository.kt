package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.reservations.types.Reservation

interface ReservationRepository {
    fun findById(id: Int) : Reservation?
    fun findByRenterId(renterId: Int) : List<Reservation>
    fun findByVehicleId(vehicleId: Int) : List<Reservation>
    fun saveReservation(reservation: Reservation) : Reservation
    fun deleteReservation(reservation: Reservation)
    fun updateReservation(reservation: Reservation)
}