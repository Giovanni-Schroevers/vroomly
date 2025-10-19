package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.auth.User
import com.fsa_profgroep_4.repository.UserTable
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
    override fun findById(id: Int): User? {
        var user: User? = null

        transaction(database) {

            val result: ResultRow? = UserTable
                .selectAll()
                .where { UserTable.id eq id }
                .limit(1)
                .singleOrNull()

            if (result != null) {
                user = User(
                    result[UserTable.username],
                    result[UserTable.email],
                    result[UserTable.password],
                    result[UserTable.firstName],
                    result[UserTable.middleName],
                    result[UserTable.lastName],
                    result[UserTable.dateOfBirth].toJavaLocalDate(),
                    result[UserTable.id],
                    result[UserTable.creationDate].toInstant(TimeZone.currentSystemDefault()),
                )
            }
        }
        return user
    }

    @OptIn(ExperimentalTime::class)
    override fun register(user: User): User {
        var userId = user.id

        transaction(database) {
            userId = UserTable.insert {
                it[username] = user.username
                it[email] = user.email
                it[password] = user.password
                it[firstName] = user.firstname
                it[middleName] = user.middleName
                it[lastName] = user.lastname
                it[dateOfBirth] = user.dateOfBirth.toKotlinLocalDate()
                it[creationDate] = user.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            } get UserTable.id
        }

        return user.copy(id = userId)
    }

}