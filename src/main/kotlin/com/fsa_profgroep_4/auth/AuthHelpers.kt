package com.fsa_profgroep_4.auth


import at.favre.lib.crypto.bcrypt.BCrypt
import graphql.GraphqlErrorException
import graphql.schema.DataFetchingEnvironment
import io.ktor.server.auth.jwt.*

fun requirePrincipal(env: DataFetchingEnvironment): JWTPrincipal =
    env.graphQlContext.get<JWTPrincipal>("principal")
        ?: throw GraphqlErrorException.newErrorException()
            .message("You must be authenticated to access this resource.")
            .extensions(
                mapOf(
                    "code" to "UNAUTHORIZED",
                    "httpStatus" to 401
                )
            )
            .build()

fun hash(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}


fun verify(hash: String, password: String): Boolean {
    val res = BCrypt.verifyer().verify(password.toCharArray(), hash)
    return res.verified
}
