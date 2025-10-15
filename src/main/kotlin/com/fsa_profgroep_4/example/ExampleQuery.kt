package com.fsa_profgroep_4.example

import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.auth.requirePrincipal
import com.fsa_profgroep_4.example.types.Greeting
import com.fsa_profgroep_4.example.types.GreetingInput
import graphql.schema.DataFetchingEnvironment

class ExampleQuery: Query {
    fun greet(input: GreetingInput): Greeting = Greeting(greeting = "Hello, ${input.name}!", name = input.name)
    fun protectedQuery(env: DataFetchingEnvironment): String {
        val principal = requirePrincipal(env)

        return principal.payload.getClaim("email").asString()
    }
}