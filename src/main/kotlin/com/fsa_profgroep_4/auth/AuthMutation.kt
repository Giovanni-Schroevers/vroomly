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

    @OptIn(ExperimentalTime::class)
    suspend fun editUser(input: EditInput, env: DataFetchingEnvironment): EditResponse {
        val token = requirePrincipal(env)
        val email = token.payload.getClaim("email").asString()

        if (input.username == null && input.password == null && input.firstname == null &&
            input.middleName == null && input.lastname == null && input.dob == null
        ) {
            throw GraphQLException("No fields to update")
        }

        val repository = repositoryFactory.createUserRepository()

        val updatedUser = try {
            repository.update(email, input)
        } catch (e: Exception) {
            throw GraphQLException(e.message)
        }

        val userResponse = UserResponse(
            id = updatedUser.id,
            username = updatedUser.username,
            email = updatedUser.email,
            firstname = updatedUser.firstname,
            middleName = updatedUser.middleName,
            lastname = updatedUser.lastname,
            dateOfBirth = updatedUser.dateOfBirth,
        )

        return EditResponse(user = userResponse)
    }
}
