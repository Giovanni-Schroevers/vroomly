package com.fsa_profgroep_4.reservations

import kotlinx.datetime.number

fun convertKotlinDateToJavaDate(date: kotlinx.datetime.LocalDate): java.time.LocalDate =
    java.time.LocalDate.of(date.year, date.month.number, date.day)

fun convertJavaDatetoKotlinDate(date: java.time.LocalDate): kotlinx.datetime.LocalDate =
    kotlinx.datetime.LocalDate(date.year, date.monthValue, date.dayOfMonth)