package com.fsa_profgroep_4.repository

import com.fsa_profgroep_4.payments.types.Payment

interface PaymentRepository {
    fun getPaymentById(paymentId: Int): Payment?
    fun savePayment(payment: Payment): Payment
    fun deletePayment(payment: Payment): Boolean
    fun updatePayment(payment: Payment): Payment
}