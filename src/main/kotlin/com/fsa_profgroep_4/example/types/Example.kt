package com.fsa_profgroep_4.example.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Greeting input.")
data class GreetingInput(
    @GraphQLDescription("Name of the user.")
    val name: String,
)

@GraphQLDescription("Greeting payload with both the message and the provided name")
data class Greeting(
    @GraphQLDescription("The full greeting message.")
    val greeting: String,
    @GraphQLDescription("The name provided by the client.")
    val name: String
)

