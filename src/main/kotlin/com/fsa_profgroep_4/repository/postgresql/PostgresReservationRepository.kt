package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.repository.PaymentTable
import com.fsa_profgroep_4.repository.ReservationRepository
import com.fsa_profgroep_4.repository.ReservationTable
import com.fsa_profgroep_4.reservations.types.Reservation
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class PostgresReservationRepository(jdbc: String, user: String, password: String) : ReservationRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    override fun findById(id: Int): Reservation? = transaction {

    }

    override fun findByRenterId(renterId: Int): List<Reservation> {
        TODO("Not yet implemented")
    }

    override fun findByVehicleId(vehicleId: Int): List<Reservation> {
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

    fun mapResultRowToReservation(resultRow: ResultRow): Reservation = Reservation(
        resultRow[ReservationTable.Id],
        resultRow[ReservationTable.StartDate],
        resultRow[ReservationTable.EndDate],
        resultRow[ReservationTable.Status],
        resultRow[PaymentTable.Amount],
        resultRow

    )
}