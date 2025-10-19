package com.fsa_profgroep_4.auth.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.time.LocalDate

@GraphQLDescription("User response data.")
data class UserResponse(
    @property:GraphQLDescription("Users unique id.")
    val id: Int,
    @property:GraphQLDescription("Users username.")
    val username: String,
    @property:GraphQLDescription("Users email address.")
    val email: String,
    @property:GraphQLDescription("Users first name.")
    val firstname: String,
    @property:GraphQLDescription("Users middle name.")
    val middleName: String? = null,
    @property:GraphQLDescription("Users last name.")
    val lastname: String,
    @property:GraphQLDescription("Users date of birth.")
    val dateOfBirth: LocalDate,
)
