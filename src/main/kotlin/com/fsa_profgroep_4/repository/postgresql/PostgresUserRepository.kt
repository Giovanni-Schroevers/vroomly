package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.auth.User
import com.fsa_profgroep_4.auth.hash
import com.fsa_profgroep_4.auth.types.EditInput
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
                .where { UsersTable.Email eq email }
                .limit(1)
                .singleOrNull()

            if (result != null) {
                if (!verify(result[UsersTable.Password], password)) {
                    return@transaction
                }

                user = User(
                    result[UsersTable.Username],
                    result[UsersTable.Email],
                    result[UsersTable.Password],
                    result[UsersTable.FirstName],
                    result[UsersTable.MiddleName],
                    result[UsersTable.LastName],
                    result[UsersTable.DateOfBirth].toJavaLocalDate(),
                    result[UsersTable.Id],
                    result[UsersTable.CreationDate].toInstant(TimeZone.currentSystemDefault()),
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
                it[Username] = user.username
                it[Email] = user.email
                it[Password] = hash(user.password)
                it[FirstName] = user.firstname
                if (user.middleName != null) it[MiddleName] = user.middleName
                it[LastName] = user.lastname
                it[DateOfBirth] = user.dateOfBirth.toKotlinLocalDate()
                it[CreationDate] = user.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            } get UsersTable.Id
        }

        return user.copy(id = userId)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun update(userId: Int, input: EditInput): User {
        var updated: User? = null
        transaction(database) {
            val result: ResultRow? = UsersTable.updateReturning(
                returning = listOf(
                    UsersTable.Id,
                    UsersTable.Username,
                    UsersTable.Email,
                    UsersTable.Password,
                    UsersTable.FirstName,
                    UsersTable.MiddleName,
                    UsersTable.LastName,
                    UsersTable.DateOfBirth,
                    UsersTable.CreationDate,
                ),
                where = { UsersTable.Id eq userId }
            ) {
                if (input.username != null) it[Username] = input.username
                if (input.password != null) it[Password] = hash(input.password)
                if (input.firstname != null) it[FirstName] = input.firstname
                if (input.middleName != null) it[MiddleName] = input.middleName
                if (input.lastname != null) it[LastName] = input.lastname
                if (input.dob != null) it[DateOfBirth] = input.dob.toKotlinLocalDate()
            }.singleOrNull()

            if (result == null) {
                error("User with Id '$userId' not found")
            } else {
                updated = User(
                    result[UsersTable.Username],
                    result[UsersTable.Email],
                    result[UsersTable.Password],
                    result[UsersTable.FirstName],
                    result[UsersTable.MiddleName],
                    result[UsersTable.LastName],
                    result[UsersTable.DateOfBirth].toJavaLocalDate(),
                    result[UsersTable.Id],
                    result[UsersTable.CreationDate].toInstant(TimeZone.currentSystemDefault()),
                )
            }
        }
        return updated!!
    }

    override suspend fun delete(userId: Int) {
        transaction(database) {
            UsersTable.deleteWhere { UsersTable.Id eq userId }
        }
    }
}
