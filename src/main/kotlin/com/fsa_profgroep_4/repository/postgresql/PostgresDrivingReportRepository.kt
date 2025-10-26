package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.auth.hash
import com.fsa_profgroep_4.repository.DrivingReportRepository
import com.fsa_profgroep_4.repository.DrivingReportTable
import com.fsa_profgroep_4.repository.ReservationTable
import com.fsa_profgroep_4.repository.UsersTable
import com.fsa_profgroep_4.repository.UsersTable.CreationDate
import com.fsa_profgroep_4.repository.UsersTable.DateOfBirth
import com.fsa_profgroep_4.repository.UsersTable.Email
import com.fsa_profgroep_4.repository.UsersTable.FirstName
import com.fsa_profgroep_4.repository.UsersTable.LastName
import com.fsa_profgroep_4.repository.UsersTable.MiddleName
import com.fsa_profgroep_4.repository.UsersTable.Password
import com.fsa_profgroep_4.repository.UsersTable.Username
import com.fsa_profgroep_4.repository.ViolationTable
import com.fsa_profgroep_4.reservations.convertKotlinDateToJavaDate
import com.fsa_profgroep_4.reservations.types.Reservation
import com.fsa_profgroep_4.reservations.types.ReservationStatus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.datetime.CurrentDate
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.LocalDate
import kotlin.time.ExperimentalTime

class PostgresDrivingReportRepository(jdbc: String, user: String, password: String) : DrivingReportRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    @OptIn(ExperimentalTime::class)
    override suspend fun getCurrentReservation(userId: Int): Reservation? {
        var reservation: Reservation? = null
        transaction(database) {
            val result: ResultRow? = ReservationTable
                .selectAll()
                .where { (ReservationTable.UserId eq userId) and (ReservationTable.StartDate lessEq CurrentDate) and (ReservationTable.EndDate greaterEq CurrentDate) }
                .limit(1)
                .singleOrNull()

            if (result != null) {
                reservation = Reservation(
                    result[ReservationTable.Id],
                    convertKotlinDateToJavaDate(result[ReservationTable.StartDate]),
                    convertKotlinDateToJavaDate(result[ReservationTable.EndDate]),
                    ReservationStatus.valueOf(result[ReservationTable.Status]),
                    result[ReservationTable.TotalCost],
                    result[ReservationTable.Paid],
                    convertKotlinDateToJavaDate(result[ReservationTable.CreationDate]),
                    result[ReservationTable.VehicleId],
                    result[ReservationTable.UserId]
                )
            }
        }
        return reservation
    }

    override suspend fun saveDrivingReport(reservation: Reservation, score: Double, violations: Map<String, Double>, date: LocalDate) {
        var drivingReportId: Int? = null

        if (reservation.id == null) {
            throw IllegalArgumentException("Reservation id is null")
        }

        transaction(database) {
            drivingReportId = DrivingReportTable.insert {
                it[SafetyScore] = score
                it[ReservationId] = reservation.id
                it[Date] = date.toKotlinLocalDate()
            } get DrivingReportTable.Id
        }

        for (violation in violations) {
            transaction(database) {
                ViolationTable.insert {
                    it[Description] = violation.key
                    it[Score] = violation.value
                    it[DrivingReportId] = drivingReportId!!
                }
            }
        }
    }
}
