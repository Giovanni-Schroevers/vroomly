package com.fsa_profgroep_4.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fsa_profgroep_4.auth.types.LoginInput
import com.fsa_profgroep_4.repository.UserRepository
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

    suspend fun authenticate(repository: UserRepository, input: LoginInput): Pair<String, User>? {
        val foundUser = repository.findByCredentials(input.email, input.password)

        return if (foundUser !== null) {
            createAccessToken(foundUser.id) to foundUser
        } else null
    }

    private fun createAccessToken(id: Int): String = JWT
        .create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim("id", id)
        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
        .sign(Algorithm.HMAC256(jwtSecret))
}
