package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.auth.User
import com.fsa_profgroep_4.auth.types.EditInput

interface UserRepository {
    suspend fun findByCredentials(email: String, password: String): User?
    suspend fun register(user: User): User
    suspend fun update(userId: Int, input: EditInput): User
}
