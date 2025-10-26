package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.reservations.types.Reservation
import java.time.LocalDate

interface DrivingReportRepository {
    suspend fun getCurrentReservation(userId: Int): Reservation?
    suspend fun saveDrivingReport(reservation: Reservation, score: Double, violations: Map<String, Double>, date: LocalDate)
}