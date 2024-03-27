package com.example.models

import org.jetbrains.exposed.sql.Database

object DatabseFactory {
    fun init() {
        val url = "jdbc:postgresql://localhost:5432/hospital"
        val driver = "org.h2.Driver"
        val username = "postgres"
        val password = "postgres"
        Database.connect(url,driver,username,password)
    }
}