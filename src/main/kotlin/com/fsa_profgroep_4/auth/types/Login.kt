package com.fsa_profgroep_4.auth.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Login input.")
data class LoginInput(
    @param:GraphQLDescription("Email used during signup")
    val email: String,
    @param:GraphQLDescription("Password used during signup")
    val password: String,
)

@GraphQLDescription("Login response data.")
data class LoginResponse(
    @param:GraphQLDescription("Token used to authenticate")
    val token: String
)
