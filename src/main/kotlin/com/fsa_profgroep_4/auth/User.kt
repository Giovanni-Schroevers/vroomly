package com.fsa_profgroep_4.auth

import java.time.LocalDate
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class User (
    val username: String,
    val email: String,
    val password: String,
    val firstname: String,
    val middleName: String?,
    val lastname: String,
    val dateOfBirth: LocalDate,
    val id: UUID = UUID.randomUUID(),
    val createdAt: Instant = Clock.System.now(),
)
