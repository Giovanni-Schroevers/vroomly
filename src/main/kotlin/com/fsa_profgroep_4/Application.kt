package com.fsa_profgroep_4

import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.server.ktor.*
import com.fsa_profgroep_4.auth.AuthMutation
import com.fsa_profgroep_4.auth.AuthQuery
import com.fsa_profgroep_4.example.ExampleQuery
import com.fsa_profgroep_4.reservations.ReservationsQuery
import com.fsa_profgroep_4.vehicles.VehiclesQuery
import graphql.GraphQLContext
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLType
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.serialization.jackson.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.ApplicationRequest
import kotlin.reflect.KType

fun main(args: Array<String>) {
    EngineMain.main(args)
}

class CustomGraphQLContextFactory : DefaultKtorGraphQLContextFactory() {
    override suspend fun generateContext(request: ApplicationRequest): GraphQLContext {
        val ctx = super.generateContext(request)

        request.call.principal<JWTPrincipal>()?.let { jwt ->
            ctx.put("principal", jwt)   // mutate the existing context instead of merging new ones
        }

        return ctx
    }
}

fun Application.graphQLModule(){
    install(GraphQL) {
        schema {
            packages = listOf("com.fsa_profgroep_4")
            queries = listOf(
                ExampleQuery(),
                AuthQuery(environment),
                ReservationsQuery(),
                VehiclesQuery()
            )
            mutations = listOf(
                AuthMutation(environment),
            )
            hooks = object : SchemaGeneratorHooks {
                override fun willGenerateGraphQLType(type: KType): GraphQLType? =
                    when (type.classifier) {
                        java.time.LocalDate::class -> ExtendedScalars.Date
                        java.time.OffsetDateTime::class -> ExtendedScalars.DateTime
                        else -> null
                    }
            }
        }
        server {
            contextFactory = CustomGraphQLContextFactory()
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

    install(Authentication) {
        jwt {}
    }
}
