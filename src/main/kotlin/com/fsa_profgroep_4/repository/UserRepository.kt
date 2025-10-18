package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.auth.User

interface UserRepository {
    fun findById(id: Int): User?
    fun register(user: User): User
}