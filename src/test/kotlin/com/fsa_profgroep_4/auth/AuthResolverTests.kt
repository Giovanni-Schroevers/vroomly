package com.fsa_profgroep_4.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import graphql.GraphQLContext
import com.fsa_profgroep_4.auth.types.*
import com.fsa_profgroep_4.repository.UserRepository
import graphql.GraphQLException
import graphql.GraphqlErrorException
import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import java.time.LocalDate
import kotlin.time.ExperimentalTime

@DisplayName("Auth Tests")
class AuthResolversTest {
    private lateinit var sharedRepo: InMemoryUserRepository
    private lateinit var mutation: AuthMutation
    private lateinit var query: AuthQuery

    @BeforeEach
    fun setUp() = runTest {
        sharedRepo = InMemoryUserRepository()
        mutation = AuthMutation(sharedRepo)
        query = AuthQuery(sharedRepo, JwtService("secret", "issuer", "audience"))

        mutation.registerUser(
            RegisterInput(
                username = "test",
                email = "test@student.avans.nl",
                password = "password123",
                firstname = "Firstname",
                lastname = "Lastname",
                dob = LocalDate.now().minusYears(20)
            )
        )
    }

    class InMemoryUserRepository : UserRepository {
        data class MemoryUser(
            var username: String,
            val email: String,
            var passwordHash: String,
            var firstname: String,
            var middleName: String?,
            var lastname: String,
            var dob: LocalDate,
            var id: Int = 1
        )

        private val users = mutableMapOf<String, MemoryUser>()

        @OptIn(ExperimentalTime::class)
        override suspend fun findByCredentials(email: String, password: String): User? {
            val u = users[email] ?: return null
            if (!verify(u.passwordHash, password)) return null
            return User(u.username, u.email, u.passwordHash, u.firstname, u.middleName, u.lastname, u.dob, u.id)
        }

        @OptIn(ExperimentalTime::class)
        override suspend fun register(user: User): User {
            val createdUser = MemoryUser(
                username = user.username,
                email = user.email,
                passwordHash = hash(user.password),
                firstname = user.firstname,
                middleName = user.middleName,
                lastname = user.lastname,
                dob = user.dateOfBirth,
                id = users.size + 1
            )
            users[user.email] = createdUser
            return user.copy(id = createdUser.id, password = createdUser.passwordHash)
        }

        @OptIn(ExperimentalTime::class)
        override suspend fun update(email: String, input: EditInput): User {
            val u = users[email] ?: error("User with email '$email' not found")
            if (input.username != null) u.username = input.username
            if (input.password != null) u.passwordHash = hash(input.password)
            if (input.firstname != null) u.firstname = input.firstname
            if (input.middleName != null) u.middleName = input.middleName
            if (input.lastname != null) u.lastname = input.lastname
            if (input.dob != null) u.dob = input.dob
            return User(u.username, u.email, u.passwordHash, u.firstname, u.middleName, u.lastname, u.dob, u.id)
        }
    }

    private fun envWithPrincipal(email: String): DataFetchingEnvironment {
        val token = JWT.create()
            .withClaim("email", email)
            .sign(Algorithm.HMAC256("secret"))
        val principal = JWTPrincipal(JWT.decode(token))

        val context = GraphQLContext.newContext().of("principal", principal).build()
        return DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
            .graphQLContext(context)
            .build()
    }

    @Test
    @DisplayName("Creating a user with invalid data should throw an exception")
    fun registerUserFail() = runTest {
        val repo = InMemoryUserRepository()
        val mutation = AuthMutation(repo)

        val input = RegisterInput(
            username = "",
            email = "invalid-email",
            password = "123",
            firstname = "",
            lastname = "",
            dob = LocalDate.now()
        )

        try {
            mutation.registerUser(input)
            assertTrue(false, "Expected exception was not thrown")
        } catch (e: GraphqlErrorException) {
            assertEquals("Validation failed", e.message)
            assertEquals("BAD_REQUEST", e.extensions["code"])
            assertEquals(400, e.extensions["httpStatus"])
            val errors = e.extensions["errors"] as Map<*, *>
            assertTrue(errors.containsKey("username"))
            assertTrue(errors.containsKey("email"))
            assertTrue(errors.containsKey("password"))
            assertTrue(errors.containsKey("firstname"))
            assertTrue(errors.containsKey("lastname"))
            assertTrue(errors.containsKey("dob"))
        }
    }

    @Test
    @DisplayName("Creating a user with valid data should succeed")
    fun registerUserSuccess() = runTest {
        val repo = InMemoryUserRepository()
        val mutation = AuthMutation(repo)

        val input = RegisterInput(
            username = "test3",
            email = "test3@student.avans.nl",
            password = "password123",
            firstname = "Test",
            lastname = "Test",
            dob = LocalDate.now().minusYears(25)
        )

        val result = mutation.registerUser(input)
        assertEquals("Account for test3@student.avans.nl has successfully been created", result)
    }

    @Test
    @DisplayName("Logging in with invalid credentials should throw an exception")
    fun loginFail() = runTest {
        try {
            query.login(LoginInput(email = "test@student.avans.nl", password = "wrong"))
            assertTrue(false, "Expected login failure")
        } catch (e: GraphQLException) {
            assertEquals("Invalid username or password", e.message)
        }
    }

    @Test
    @DisplayName("Logging in with valid credentials should succeed")
    fun loginSuccess() = runTest {
        val response = query.login(LoginInput(email = "test@student.avans.nl", password = "password123"))
        assertTrue(response.token.isNotBlank())
        assertEquals("test@student.avans.nl", response.user.email)
        assertEquals("test", response.user.username)
    }

    @Test
    @DisplayName("Editing a user without data should throw an exception")
    fun editUserFailNoFields() = runTest {
        val env = envWithPrincipal("test@student.avans.nl")

        try {
            mutation.editUser(EditInput(), env)
            assertTrue(false, "Expected edit validation failure")
        } catch (e: GraphqlErrorException) {
            assertEquals("No fields to update", e.message)
            assertEquals("BAD_REQUEST", e.extensions["code"])
            assertEquals(400, e.extensions["httpStatus"])
        }
    }

    @Test
    @DisplayName("Editing a user with invalid data should throw an exception")
    fun editUserFailInvalidFields() = runTest {
        val env = envWithPrincipal("test@student.avans.nl")

        try {
            mutation.editUser(EditInput(password = "123"), env)
            assertTrue(false, "Expected edit validation failure")
        } catch (e: GraphqlErrorException) {
            assertEquals("Validation failed", e.message)
            assertEquals("BAD_REQUEST", e.extensions["code"])
            assertEquals(400, e.extensions["httpStatus"])
            val errors = e.extensions["errors"] as Map<*, *>
            assertTrue(errors.containsKey("password"))
        }
    }

    @Test
    @DisplayName("Editing a user with valid data should succeed")
    fun editUserSuccess() = runTest {
        val env = envWithPrincipal("test@student.avans.nl")

        val result = mutation.editUser(EditInput(firstname = "Updated", lastname = "Updated"), env)
        assertEquals("test@student.avans.nl", result.user.email)
        assertEquals("Updated", result.user.firstname)
        assertEquals("Updated", result.user.lastname)
    }
}
