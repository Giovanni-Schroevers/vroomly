package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.repository.VehicleRepository
import org.jetbrains.exposed.v1.jdbc.Database

class PostgresVehicleRepository(jdbc: String, user: String, password: String): VehicleRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )
}