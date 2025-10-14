package com.fsa_profgroep_4.auth.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.time.LocalDate

@GraphQLDescription("Registration input.")
data class RegisterInput(
    @param:GraphQLDescription("Users username.")
    val username: String,
    @param:GraphQLDescription("Users email address.")
    val email: String,
    @param:GraphQLDescription("Users password.")
    val password: String,
    @param:GraphQLDescription("Users first name.")
    val firstname: String,
    @param:GraphQLDescription("Users middle name.")
    val middleName: String? = null,
    @param:GraphQLDescription("Users last name.")
    val lastname: String,
    @param:GraphQLDescription("Users date of birth.")
    val dob: LocalDate,
)
