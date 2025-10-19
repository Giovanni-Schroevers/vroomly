package com.fsa_profgroep_4.auth

import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.auth.types.*
import com.fsa_profgroep_4.repository.RepositoryFactory
import graphql.GraphQLException
import io.ktor.server.application.ApplicationEnvironment

class AuthQuery(private val environment: ApplicationEnvironment): Query {
    private val repositoryFactory: RepositoryFactory = RepositoryFactory(environment)
    private val jwtService: JwtService = JwtService(environment)

    suspend fun login(input: LoginInput): LoginResponse {
        val repository = repositoryFactory.createUserRepository()

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
