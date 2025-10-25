package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.repository.postgresql.*
import io.ktor.server.application.ApplicationEnvironment

class RepositoryFactory(
    private val database: String,
    private val databaseUser: String,
    private val databasePassword: String,
    private val jdbc: String
) {
    constructor(environment: ApplicationEnvironment): this(
        database = environment.config.property("database.db").getString(),
        databaseUser = environment.config.property("database.user").getString(),
        databasePassword = environment.config.property("database.password").getString(),
        jdbc = environment.config.property("database.jdbc").getString(),
    )

    fun createUserRepository(): UserRepository {
        return when (database){
            "PostgreSQL" -> PostgresUserRepository(jdbc, databaseUser, databasePassword)
            else -> throw NotImplementedError("No UserRepository implementation available for database $database")
        }
    }

    fun createVehicleRepository(): VehicleRepository {
        return when (database){
            "PostgreSQL" -> PostgresVehicleRepository(jdbc, databaseUser, databasePassword)
            else -> throw NotImplementedError("No VehicleRepository implementation available for database $database")
        }
    }

    fun createPaymentRepository(): PaymentRepository {
        return when (database){
            "PostgreSQL" -> PostgresPaymentRepository(jdbc, databaseUser, databasePassword)
            else -> throw NotImplementedError("No PaymentRepository implementation available for database $database")
        }
    }

    fun createReservationRepository(): ReservationRepository {
        return when (database){
            "PostgreSQL" -> PostgresReservationRepository(jdbc, databaseUser, databasePassword)
            else -> throw NotImplementedError("No ReservationRepository implementation available for database $database")
        }
    }
}