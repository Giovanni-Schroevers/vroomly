package com.fsa_profgroep_4.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Greeting payload with both the message and the provided name.")
data class Greeting(
    @GraphQLDescription("The full greeting message.")
    val greeting: String,
    @GraphQLDescription("The name provided by the client.")
    val name: String
)