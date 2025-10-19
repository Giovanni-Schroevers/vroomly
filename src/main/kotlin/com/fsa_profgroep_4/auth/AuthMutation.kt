package com.fsa_profgroep_4.auth

import com.expediagroup.graphql.server.operations.Mutation
import com.fsa_profgroep_4.auth.types.*
import com.fsa_profgroep_4.repository.RepositoryFactory
import graphql.GraphQLException
import graphql.schema.DataFetchingEnvironment
import io.ktor.server.application.ApplicationEnvironment
import java.time.LocalDate
import kotlin.time.ExperimentalTime

class AuthMutation(environment: ApplicationEnvironment): Mutation {
    private val repositoryFactory: RepositoryFactory = RepositoryFactory(environment)

    @OptIn(ExperimentalTime::class)
    suspend fun registerUser(input: RegisterInput): String {
        val repository = repositoryFactory.createUserRepository()

        val user = User(
            username = input.username,
            email = input.email,
            password = input.password,
            firstname = input.firstname,
            middleName = input.middleName,
            lastname = input.lastname,
            dateOfBirth = input.dob,
        )

        try {
            repository.register(user)
        } catch (e: Exception) {
            throw GraphQLException(e.message)
        }

        return "Account for ${user.email} has successfully been created"
    }
}
