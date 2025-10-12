package com.fsa_profgroep_4.modules

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.defaultGraphQLStatusPages
import com.expediagroup.graphql.server.ktor.graphQLGetRoute
import com.expediagroup.graphql.server.ktor.graphQLPostRoute
import com.expediagroup.graphql.server.ktor.graphQLSDLRoute
import com.fsa_profgroep_4.GreetingQuery
import com.fsa_profgroep_4.ui.GraphiQLPage
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.graphQLModule(){
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