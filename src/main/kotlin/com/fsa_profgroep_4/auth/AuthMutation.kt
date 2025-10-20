package com.fsa_profgroep_4.auth

import com.expediagroup.graphql.server.operations.Mutation
import com.fsa_profgroep_4.auth.types.EditInput
import com.fsa_profgroep_4.auth.types.EditResponse
import com.fsa_profgroep_4.auth.types.RegisterInput
import com.fsa_profgroep_4.auth.types.UserResponse
import com.fsa_profgroep_4.repository.RepositoryFactory
import com.fsa_profgroep_4.repository.UserRepository
import graphql.GraphQLException
import graphql.schema.DataFetchingEnvironment
import io.ktor.server.application.ApplicationEnvironment
import kotlin.time.ExperimentalTime

class AuthMutation(private val userRepository: UserRepository): Mutation {
    constructor(environment: ApplicationEnvironment): this(RepositoryFactory(environment).createUserRepository())

    @OptIn(ExperimentalTime::class)
    @Suppress("unused")
    suspend fun registerUser(input: RegisterInput): String {
        val repository = userRepository

        validateRegister(input)

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
    @Suppress("unused")
    suspend fun editUser(input: EditInput, env: DataFetchingEnvironment): EditResponse {
        val token = requirePrincipal(env)
        val email = token.payload.getClaim("email").asString()

        validateEdit(input)

        val repository = userRepository

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
