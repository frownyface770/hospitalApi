package com.example.plugins

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
    //Every appointment is an hour long, and always starts at the hour.
     fun isValidTime(time: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return try {
            val parsedTime = LocalTime.parse(time, formatter)
            parsedTime.minute == 0
        } catch (e: DateTimeParseException) {
            false
        }
    }

}