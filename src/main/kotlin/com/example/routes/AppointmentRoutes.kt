package com.example.routes

import com.example.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AppointmentRoutes {
    private val appointmentDB = AppointmentDB()

    private val appointmentService = AppointmentService(appointmentDB)

    fun setUpAppointmentRoutes(route: Route) {
        route.route("/appointment") {
            listAppointments()
            createAppointment()
        }
    }

    //If an error occurs it will always go into the catch and never use the success boolean
    //Must change
    private fun Route.createAppointment() {
        post("/createAppointment") {
            val appointment = call.receive<Appointment>()

            try {
                val success = appointmentService.addAppointment(appointment)

                if (success) {
                    call.respondText("Appointment stored correctly", status = HttpStatusCode.Created)
                }
//                else {
//                    call.respondText("Appointment already exists", status= HttpStatusCode.Conflict)
//                }
            } catch (e: Exception) {
                call.respondText("Error creating appointment. ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    private fun Route.listAppointments() {
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            try {
                val appointmentStorage = appointmentService.getApointments(id.toInt())
                if (appointmentStorage.isNotEmpty()) {
                    call.respond(appointmentStorage)
                } else {
                    call.respondText("No appointments found", status = HttpStatusCode.OK)
                }
            } catch (e: Exception) {
                call.respondText("Error retrieving appointments ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }

}
