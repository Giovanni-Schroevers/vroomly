package com.fsa_profgroep_4.auth

import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.auth.types.LoginInput
import com.fsa_profgroep_4.auth.types.LoginResponse
import com.fsa_profgroep_4.auth.types.UserResponse
import com.fsa_profgroep_4.repository.RepositoryFactory
import com.fsa_profgroep_4.repository.UserRepository
import graphql.GraphQLException
import io.ktor.server.application.ApplicationEnvironment

class AuthQuery(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
): Query {
    constructor(environment: ApplicationEnvironment): this(
        RepositoryFactory(environment).createUserRepository(),
        JwtService(environment)
    )

    @Suppress("unused")
    suspend fun login(input: LoginInput): LoginResponse {
        val repository = userRepository

        val (token, user) = jwtService.authenticate(repository, input) ?: throw GraphQLException("Invalid username or password")

        val userResponse = UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            firstname = user.firstname,
            middleName = user.middleName,
            lastname = user.lastname,
            dateOfBirth = user.dateOfBirth,
        )

        return LoginResponse(token, userResponse)
    }
}
