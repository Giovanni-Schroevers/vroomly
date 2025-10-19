package com.fsa_profgroep_4.auth.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.time.LocalDate

@GraphQLDescription("Registration input.")
data class EditInput(
    @param:GraphQLDescription("Users username.")
    val username: String? = null,
    @param:GraphQLDescription("Users password.")
    val password: String? = null,
    @param:GraphQLDescription("Users first name.")
    val firstname: String? = null,
    @param:GraphQLDescription("Users middle name.")
    val middleName: String? = null,
    @param:GraphQLDescription("Users last name.")
    val lastname: String? = null,
    @param:GraphQLDescription("Users date of birth.")
    val dob: LocalDate? = null,
)

@GraphQLDescription("Edit response data.")
data class EditResponse(
    @property:GraphQLDescription("Update users data.")
    val user: UserResponse,
)
