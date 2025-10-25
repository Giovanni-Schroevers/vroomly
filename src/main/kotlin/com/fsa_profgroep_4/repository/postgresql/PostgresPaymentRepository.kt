package com.fsa_profgroep_4.repository.postgresql

import com.fsa_profgroep_4.payments.types.Payment
import com.fsa_profgroep_4.repository.PaymentRepository
import org.jetbrains.exposed.v1.jdbc.Database

class PostgresPaymentRepository(jdbc: String, user: String, password: String): PaymentRepository {
    val database : Database = Database.connect(
        jdbc,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    override fun getPaymentById(paymentId: Int): Payment? {
        TODO("Not yet implemented")
    }

    override fun savePayment(payment: Payment): Payment {
        TODO("Not yet implemented")
    }

    override fun deletePayment(payment: Payment): Boolean {
        TODO("Not yet implemented")
    }

    override fun updatePayment(payment: Payment): Payment {
        TODO("Not yet implemented")
    }
}