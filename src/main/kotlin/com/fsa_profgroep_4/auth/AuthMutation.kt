package com.fsa_profgroep_4.auth

import com.expediagroup.graphql.server.operations.Mutation
import com.fsa_profgroep_4.auth.types.*
import io.ktor.server.application.ApplicationEnvironment
import kotlin.time.ExperimentalTime

class AuthMutation(environment: ApplicationEnvironment): Mutation {
    @OptIn(ExperimentalTime::class)
    fun register(input: RegisterInput): String {
        val user = User(
            username = input.username,
            email = input.email,
            password = input.password,
            firstname = input.firstname,
            middleName = input.middleName,
            lastname = input.lastname,
            dateOfBirth = input.dob,
        )

        return "Account for ${user.email} has successfully been created"
    }
}
