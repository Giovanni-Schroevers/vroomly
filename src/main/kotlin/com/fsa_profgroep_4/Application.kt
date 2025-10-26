package com.fsa_profgroep_4

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.server.ktor.*
import com.fsa_profgroep_4.auth.AuthMutation
import com.fsa_profgroep_4.auth.AuthQuery
import com.fsa_profgroep_4.driving_report.DrivingReportMutation
import com.fsa_profgroep_4.reservations.ReservationsQuery
import com.fsa_profgroep_4.vehicles.VehiclesMutation
import com.fsa_profgroep_4.vehicles.VehiclesQuery
import graphql.GraphQLContext
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLType
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import io.ktor.serialization.jackson.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.ApplicationRequest
import kotlin.reflect.KType

fun main(args: Array<String>) {
    EngineMain.main(args)
}

class CustomGraphQLContextFactory(private val environment: ApplicationEnvironment) : DefaultKtorGraphQLContextFactory() {

    override suspend fun generateContext(request: ApplicationRequest): GraphQLContext {
        val ctx = super.generateContext(request)

        // Try to read Authorization: Bearer <token> and validate it manually so we can
        // always return a proper GraphQL JSON response even if the token is invalid.
        val authHeader = request.headers["Authorization"]
        if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ", ignoreCase = true)) {
            val token = authHeader.removePrefix("Bearer ").trim()
            try {
                val secret = environment.config.property("jwt.secret").getString()
                val issuer = environment.config.property("jwt.issuer").getString()
                val audience = environment.config.property("jwt.audience").getString()

                val algorithm = com.auth0.jwt.algorithms.Algorithm.HMAC256(secret)
                val verifier = com.auth0.jwt.JWT
                    .require(algorithm)
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build()

                val decoded = verifier.verify(token)
                val principal = JWTPrincipal(decoded)
                ctx.put("principal", principal)
            } catch (_: Exception) {
                // Invalid or expired token – do not interrupt HTTP pipeline here.
                // We simply don't set a principal so resolvers can decide what to do.
            }
        }

        return ctx
    }
}

fun Application.graphQLModule(){
    install(GraphQL) {
        schema {
            packages = listOf("com.fsa_profgroep_4")
            queries = listOf(
                AuthQuery(environment),
                ReservationsQuery(environment),
                VehiclesQuery(environment)
            )
            mutations = listOf(
                AuthMutation(environment),
                VehiclesMutation(environment),
                DrivingReportMutation(environment)
            )
            hooks = object : SchemaGeneratorHooks {
                override fun willGenerateGraphQLType(type: KType): GraphQLType? =
                    when (type.classifier) {
                        java.time.LocalDate::class -> ExtendedScalars.Date
                        java.time.OffsetDateTime::class -> ExtendedScalars.DateTime
                        Long::class -> ExtendedScalars.GraphQLLong
                        else -> null
                    }
            }
        }
        server {
            contextFactory = CustomGraphQLContextFactory(environment)
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
