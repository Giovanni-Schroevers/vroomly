package com.fsa_profgroep_4.example

import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.example.types.Greeting
import com.fsa_profgroep_4.example.types.GreetingInput

class ExampleQuery: Query {
    fun greet(input: GreetingInput): Greeting = Greeting(greeting = "Hello, ${input.name}!", name = input.name)
}