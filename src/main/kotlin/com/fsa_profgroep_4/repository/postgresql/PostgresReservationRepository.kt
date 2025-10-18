package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.repository.ReservationRepository
import org.jetbrains.exposed.v1.jdbc.Database

class PostgresReservationRepository(jdbc: String, user: String, password: String) : ReservationRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )
}