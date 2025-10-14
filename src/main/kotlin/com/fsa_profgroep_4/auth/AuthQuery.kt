package com.fsa_profgroep_4.auth

import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.auth.types.*
import graphql.GraphQLException
import io.ktor.server.application.ApplicationEnvironment

class AuthQuery(private val environment: ApplicationEnvironment): Query {
    private val jwtService: JwtService = JwtService(environment)

    suspend fun login(input: LoginInput): LoginResponse {
        val token = jwtService.authenticate(input.email, input.password) ?: throw GraphQLException("Invalid username or password")

        return LoginResponse(token)
    }
}
