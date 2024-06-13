package com.example.models

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object Validation {
     fun isValidDate(date: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return try {
            LocalDate.parse(date, formatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
     fun isValidTime(time: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return try {
            LocalTime.parse(time, formatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

}