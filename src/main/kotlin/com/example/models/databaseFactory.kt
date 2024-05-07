package com.example.models

import org.jetbrains.exposed.sql.Database

object DatabseFactory {
    fun init() {
        val url = "jdbc:postgresql://ep-restless-snowflake-a27dt0ab.eu-central-1.aws.neon.tech/hospital?sslmode=require"
        val driver = "org.postgresql.Driver"
        val username = "hospital_owner"
        val password = "jKnfgah7l3Vs"
        Database.connect(url,driver,username,password)
    }
}
