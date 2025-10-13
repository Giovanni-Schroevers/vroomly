package com.fsa_profgroep_4.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationEnvironment
import java.util.Date

class JwtService(
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val jwtAudience: String
) {
    constructor(environment: ApplicationEnvironment): this(
        jwtSecret = environment.config.property("jwt.secret").getString(),
        jwtIssuer = environment.config.property("jwt.issuer").getString(),
        jwtAudience = environment.config.property("jwt.audience").getString(),
    )

    suspend fun authenticate(email: String, password: String): String? {
        val foundUser = if(email == "test@avans.nl" && password == "test123") email else null

        return if (foundUser !== null) {
            createAccessToken(foundUser)
        } else null
    }

    private fun createAccessToken(email: String): String = JWT
        .create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
        .sign(Algorithm.HMAC256(jwtSecret))
}