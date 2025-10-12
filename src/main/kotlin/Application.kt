package com.fsa_profgroep_4

import com.fsa_profgroep_4.types.Greeting
import com.expediagroup.graphql.server.ktor.*
import com.expediagroup.graphql.server.operations.Query
import com.fsa_profgroep_4.modules.*
import com.fsa_profgroep_4.ui.GraphiQLPage
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

// Example query
class GreetingQuery: Query {
    fun greet(name: String): Greeting = Greeting(greeting = "Hello, $name!", name = name)
}

fun Application.module() {
    authModule()
    reservationModule()
    vehicleModule()
}

fun Application.graphQLModule() {
    install(GraphQL) {
        schema {
            packages = listOf("com.fsa_profgroep_4")
            queries = listOf(
                GreetingQuery()
            )
        }
    }

    routing {
        graphQLPostRoute()
        graphQLGetRoute()
        graphQLSDLRoute()

        get("/playground") {
            call.respondText(GraphiQLPage.html(endpoint = "/graphql"), ContentType.Text.Html)
        }

    }

    install(StatusPages) {
        defaultGraphQLStatusPages()
    }
}
