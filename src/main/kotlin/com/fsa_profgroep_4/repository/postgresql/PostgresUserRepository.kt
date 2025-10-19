package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.auth.User
import com.fsa_profgroep_4.auth.hash
import com.fsa_profgroep_4.auth.verify
import com.fsa_profgroep_4.repository.UsersTable
import com.fsa_profgroep_4.repository.UserRepository
import kotlinx.datetime.*
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.ExperimentalTime

class PostgresUserRepository(jdbc: String, user: String, password: String) : UserRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    @OptIn(ExperimentalTime::class)
    override suspend fun findByCredentials(email: String, password: String): User? {
        var user: User? = null

        transaction(database) {
            val result: ResultRow? = UsersTable
                .selectAll()
                .where { UsersTable.email eq email }
                .limit(1)
                .singleOrNull()

            if (result != null) {
                if (!verify(result[UsersTable.password], password)) {
                    return@transaction
                }

                user = User(
                    result[UsersTable.username],
                    result[UsersTable.email],
                    result[UsersTable.password],
                    result[UsersTable.firstName],
                    result[UsersTable.middleName],
                    result[UsersTable.lastName],
                    result[UsersTable.dateOfBirth].toJavaLocalDate(),
                    result[UsersTable.id],
                    result[UsersTable.creationDate].toInstant(TimeZone.currentSystemDefault()),
                )
            }
        }
        return user
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun register(user: User): User {

        var userId = user.id

        transaction(database) {
            userId = UsersTable.insert {
                it[username] = user.username
                it[email] = user.email
                it[password] = hash(user.password)
                it[firstName] = user.firstname
                if (user.middleName != null) it[middleName] = user.middleName
                it[lastName] = user.lastname
                it[dateOfBirth] = user.dateOfBirth.toKotlinLocalDate()
                it[creationDate] = user.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            } get UsersTable.id
        }

        return user.copy(id = userId)
    }
}
