package com.example.plugins

import com.example.routes.AppointmentRoutes
import com.example.routes.PatientRoutes
import com.example.routes.DoctorRoutes
import com.example.routes.MedicalInformationRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
       val patientRoutes = PatientRoutes()
        patientRoutes.setUpPatientRoutes(this)
       val doctorRoutes = DoctorRoutes()
        doctorRoutes.setUpDoctorRoutes(this)
        val appointmentRoutes = AppointmentRoutes()
        appointmentRoutes.setUpAppointmentRoutes(this)
        val medicalInformationRoutes = MedicalInformationRoutes()
        medicalInformationRoutes.setUpMedicalInformationRoutes(this)
    }
}
