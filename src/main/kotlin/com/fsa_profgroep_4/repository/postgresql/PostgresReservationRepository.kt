package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.repository.*
import com.fsa_profgroep_4.reservations.*
import com.fsa_profgroep_4.reservations.types.*
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.LocalDateTime

class PostgresReservationRepository(jdbc: String, user: String, password: String) : ReservationRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    override fun findById(id: Int): Reservation? = transaction {
        ReservationTable
            .selectAll()
            .where { ReservationTable.Id eq id }
            .limit(1)
            .firstOrNull()
            ?.let { mapResultRowToReservation(it) }
    }

    override fun findByRenterId(renterId: Int): List<Reservation> = transaction {
        ReservationTable
            .selectAll()
            .where { ReservationTable.UserId eq renterId }
            .map { row -> mapResultRowToReservation(row) }
    }

    override fun findByVehicleId(vehicleId: Int): List<Reservation> = transaction {
        ReservationTable
            .selectAll()
            .where { ReservationTable.VehicleId eq vehicleId }
            .map { row -> mapResultRowToReservation(row) }
    }

    override fun saveReservation(reservation: Reservation): Reservation? {
        return try {
            transaction {
                val reservationId = run {
                    val insertStmt = ReservationTable.insert {
                        it[UserId] = reservation.renterId
                        it[VehicleId] = reservation.vehicleId
                        it[StartDate] = convertJavaDatetoKotlinDate(reservation.startDate)
                        it[EndDate] = convertJavaDatetoKotlinDate(reservation.endDate)
                        it[Status] = reservation.status.name
                        it[TotalCost] = reservation.totalCost
                        it[CreationDate] = convertJavaDatetoKotlinDate(reservation.createdAt)
                    }

                    val returned = insertStmt.resultedValues?.firstOrNull()
                    returned?.get(ReservationTable.Id)
                        ?: throw IllegalStateException("Failed to insert or retrieve reservation for vehicle '${reservation.vehicleId}'")
                }

                findById(reservationId)
            }
        } catch (e: Exception) {
            println("saveReservation() error: ${e.message}")
            throw IllegalStateException("Error saving reservation for vehicle '${reservation.vehicleId}': ${e.message}", e)
        }
    }

    override fun deleteReservation(reservationId: Int): Reservation = transaction {
        val reservation = findById(reservationId)
            ?: throw IllegalStateException("Reservation with ID $reservationId not found.")

        ReservationTable.deleteWhere { ReservationTable.Id eq reservationId }

        reservation
    }

    override fun updateReservation(reservation: ReservationUpdate): Reservation? = transaction {
        findById(reservation.id)
            ?: throw IllegalStateException("Reservation with ID ${reservation.id} not found.")

        ReservationTable
            .update({ ReservationTable.Id eq reservation.id }) {
                reservation.vehicleId?.let { v -> it[VehicleId] = v }
                reservation.renterId?.let { r -> it[UserId] = r }
                reservation.startDate?.let { s -> it[StartDate] = convertJavaDatetoKotlinDate(s) }
                reservation.endDate?.let { e -> it[EndDate] = convertJavaDatetoKotlinDate(e) }
                reservation.totalCost?.let { c -> it[TotalCost] = c }
                reservation.paid?.let { p -> it[Paid] = p }
                reservation.status?.let { s -> it[Status] = s.name }
            }

        findById(reservation.id)
    }

    fun mapResultRowToReservation(row: ResultRow): Reservation = Reservation(
        row[ReservationTable.Id],
        convertKotlinDateToJavaDate(row[ReservationTable.StartDate]),
        convertKotlinDateToJavaDate(row[ReservationTable.EndDate]),
        ReservationStatus.valueOf(row[ReservationTable.Status]),
        row[ReservationTable.TotalCost],
        row[ReservationTable.Paid],
        convertKotlinDateToJavaDate(row[ReservationTable.CreationDate]),
        row[ReservationTable.VehicleId],
        row[ReservationTable.UserId]
    )
}