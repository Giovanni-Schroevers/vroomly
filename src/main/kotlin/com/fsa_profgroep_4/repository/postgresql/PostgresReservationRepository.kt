package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.repository.ReservationRepository
import com.fsa_profgroep_4.reservations.Reservation
import org.jetbrains.exposed.v1.jdbc.Database

class PostgresReservationRepository(jdbc: String, user: String, password: String) : ReservationRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    override fun findById(id: Int): Reservation {
        TODO("Not yet implemented")
    }

    override fun findByOwnerId(ownerId: Int): List<Reservation> {
        TODO("Not yet implemented")
    }

    override fun saveReservation(reservation: Reservation): Reservation {
        TODO("Not yet implemented")
    }

    override fun deleteReservation(reservation: Reservation) {
        TODO("Not yet implemented")
    }

    override fun updateReservation(reservation: Reservation) {
        TODO("Not yet implemented")
    }
}