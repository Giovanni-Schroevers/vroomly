package com.fsa_profgroep_4.auth


import at.favre.lib.crypto.bcrypt.BCrypt
import com.fsa_profgroep_4.auth.types.EditInput
import com.fsa_profgroep_4.auth.types.RegisterInput
import graphql.GraphqlErrorException
import graphql.schema.DataFetchingEnvironment
import io.ktor.server.auth.jwt.*
import java.time.LocalDate

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

private const val MIN_PASSWORD_LENGTH = 8
private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

private fun throwValidationIfAny(errors: Map<String, String>) {
    if (errors.isNotEmpty()) {
        throw GraphqlErrorException
            .newErrorException()
            .message("Validation failed")
            .extensions(
                mapOf(
                    "code" to "BAD_REQUEST",
                    "httpStatus" to 400,
                    "errors" to errors
                )
            )
            .build()
    }
}

fun validateRegister(input: RegisterInput) {
    val errors = mutableMapOf<String, String>()

    if (input.username.isBlank()) errors["username"] = "Field can not be empty"
    if (input.email.isBlank()) errors["email"] = "Field can not be empty"
    if (input.password.isBlank()) errors["password"] = "Field can not be empty"
    if (input.firstname.isBlank()) errors["firstname"] = "Field can not be empty"
    if (input.lastname.isBlank()) errors["lastname"] = "Field can not be empty"

    if (input.email.isNotBlank() && !emailRegex.matches(input.email)) {
        errors["email"] = "Format is invalid"
    }

    if (input.password.length < MIN_PASSWORD_LENGTH) {
        errors["password"] = "Must be at least $MIN_PASSWORD_LENGTH characters long"
    }

    val eighteenYearsAgo = LocalDate.now().minusYears(18)
    if (input.dob.isAfter(eighteenYearsAgo)) {
        errors["dob"] = "Must be at least 18 years old"
    }

    throwValidationIfAny(errors)
}

fun validateEdit(input: EditInput) {
    val errors = mutableMapOf<String, String>()

    if (input.username == null && input.password == null && input.firstname == null &&
        input.middleName == null && input.lastname == null && input.dob == null
    ) {
        throw GraphqlErrorException
            .newErrorException()
            .message("No fields to update")
            .extensions(
                mapOf(
                    "code" to "BAD_REQUEST",
                    "httpStatus" to 400,
                )
            )
            .build()
    }

    input.username?.let { if (it.isBlank()) errors["username"] = "Field can not be empty" }
    input.password?.let { if (it.isBlank()) errors["password"] = "Field can not be empty" }
    input.firstname?.let { if (it.isBlank()) errors["firstname"] = "Field can not be empty" }
    input.lastname?.let { if (it.isBlank()) errors["lastname"] = "Field can not be empty" }

    input.password?.let {
        if (it.length < MIN_PASSWORD_LENGTH) errors["password"] = "Must be at least $MIN_PASSWORD_LENGTH characters long"
    }

    input.dob?.let {
        val eighteenYearsAgo = LocalDate.now().minusYears(18)
        if (it.isAfter(eighteenYearsAgo)) errors["dob"] = "Must be at least 18 years old"
    }

    throwValidationIfAny(errors)
}
