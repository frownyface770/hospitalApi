package com.example.routes

import com.example.models.*
import com.example.exceptions.*
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
            updateAppointment()
            deleteAppointment()
        }
    }

    //If an error occurs it will always go into the catch and never use the success boolean
    //Must change
    private fun Route.createAppointment() {
        post("/createAppointment") {
            val appointment = call.receive<Appointment>()

            try {
                 appointmentService.addAppointment(appointment)

                call.respondText("Appointment stored correctly", status = HttpStatusCode.Created)

            } catch (e: AppointmentAlreadyExistsException) {
                call.respondText("${e.message}", status= HttpStatusCode.Conflict)
            }catch (e: InvalidDateException) {
                call.respondText("${e.message}", status = HttpStatusCode.BadRequest)
            }catch (e: InvalidTimeException) {
                call.respondText("${e.message}", status = HttpStatusCode.BadRequest)
            }
            catch (e: Exception) {
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

                call.respond(appointmentStorage)

            } catch (e: Exception) {
                call.respondText("Error retrieving appointments ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    private fun Route.updateAppointment() {
        put("/updateAppointment/{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            val updatedAppointment = call.receive<Appointment>()

            try {
                appointmentService.updateAppointment(id,updatedAppointment)
                call.respondText("Appointment updated correctly", status = HttpStatusCode.OK)
            } catch (e: AppointmentNotFoundException) {
                call.respondText("${e.message}", status = HttpStatusCode.NotFound)
            }catch (e: InvalidDateException) {
                call.respondText("${e.message}", status = HttpStatusCode.BadRequest)
            }catch (e: InvalidTimeException) {
                call.respondText("${e.message}", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                call.respondText("Error updating appointment. ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }

    private fun Route.deleteAppointment() {
        delete("/deleteAppointment/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest)
            try {
                appointmentService.deleteAppointment(id)
                call.respondText("Appointment deleted successfully", status = HttpStatusCode.OK)
            } catch (e: AppointmentNotFoundException) {
                call.respondText("${e.message}", status = HttpStatusCode.NotFound)
            } catch (e: Exception) {
                call.respondText("Error deleting appointment. ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }



}
