package com.example.plugins

import com.example.routes.PatientRoutes
import com.example.routes.DoctorRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
       val patientRoutes = PatientRoutes()
        patientRoutes.setUpPatientRoutes(this)
       val doctorRoutes = DoctorRoutes()
        doctorRoutes.setUpDoctorRoutes(this)

    }
}
