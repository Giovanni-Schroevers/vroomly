package com.fsa_profgroep_4

import com.expediagroup.graphql.server.ktor.*
import com.fsa_profgroep_4.auth.AuthQuery
import com.fsa_profgroep_4.example.ExampleQuery
import com.fsa_profgroep_4.reservations.ReservationsQuery
import com.fsa_profgroep_4.vehicles.VehiclesQuery
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.serialization.jackson.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.graphQLModule(){
    install(GraphQL) {
        schema {
            packages = listOf("com.fsa_profgroep_4")
            queries = listOf(
                ExampleQuery(),
                AuthQuery(),
                ReservationsQuery(),
                VehiclesQuery()
            )
        }
    }

    routing {
        graphQLPostRoute()
        graphQLGetRoute()
        graphQLSDLRoute()
        graphiQLRoute("playground")
    }

    install(StatusPages) {
        defaultGraphQLStatusPages()
    }

    install(ContentNegotiation) {
        jackson()
    }
}
