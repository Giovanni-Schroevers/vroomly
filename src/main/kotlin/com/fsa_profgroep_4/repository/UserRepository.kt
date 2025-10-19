package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.auth.User

interface UserRepository {
    suspend fun findByCredentials(email: String, password: String): User?
    suspend fun register(user: User): User
}
