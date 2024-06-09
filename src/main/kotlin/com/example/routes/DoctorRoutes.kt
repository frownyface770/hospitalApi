package com.example.routes
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.exceptions.*
import com.example.services.DoctorService

class DoctorRoutes {
    //Initializes the doctor Database, will be replaced by an actual database, maybe...
    private val doctorDB = DoctorDB()
    //Initializes service layer that handles the doctor's
    private val doctorService = DoctorService(doctorDB)
    fun setUpDoctorRoutes(route: Route) {
        route.route("/doctor") {
            //Calls the functions that actually handle the routing
            listDoctors()
            createDoctor()
            updateDoctor()
            deleteDoctor()
            availableHours()
            printSchedule()
        }
    }
    private fun Route.createDoctor() {
        post("/createDoctor") {
            val doctor = call.receive<Doctor>()
            //I dont know if i should check this here or not.
            //Probably not.
            //val addSuccess = doctorService.addDoctor(doctor)
            try {
                doctorService.addDoctor(doctor)
                //Adds the doctor details and stores the boolean value of operation's success
                //Checks if successful and acts accordingly

                call.respondText("Doctor stored corretly", status = HttpStatusCode.Created)
//
//                    call.respondText("Doctor already exists", status = HttpStatusCode.Conflict)

            } catch (e: Exception) {
                call.respondText("Error creating doctor ${e.message}", status = HttpStatusCode.InternalServerError)
            }


        }
    }
    private fun Route.listDoctors() {
        get {
            //Gets the doctors from the service layer
            try {
                val doctorStorage = doctorService.getDoctor()
                //Check if doctor exist and respond accordingly
//                if (doctorStorage.isNotEmpty()) {
                    call.respond(doctorStorage)
//                } else {
//                    call.respondText("No doctor found", status = HttpStatusCode.OK)
//                }
            } catch (e: Exception) {
                call.respondText("Error retrieving doctors ${e.message}", status = HttpStatusCode.InternalServerError)
            }

        }
    }

    private fun Route.updateDoctor() {
        put("{id?}/updateInfo") {
            //Get new doctor info from the POST request
            val updatedDoctor = call.receive<Doctor>()

            //Get the id from the url parameters, if it's not there we send a bad request http code back
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            //Updates the doctor details and stores the boolean value of operation's success
            try {
                doctorService.updateDoctorDetails(id,updatedDoctor)

                call.respondText("Doctor with $id information updated succesfully", status = HttpStatusCode.OK)
            } catch (e: DoctorNotFoundException) {
                call.respondText("${e.message}", status = HttpStatusCode.NotFound)

            } catch (e: Exception) {
                call.respondText("Error updating patient: ${e.message}", status = HttpStatusCode.InternalServerError)
            }

        }
    }

    private fun Route.deleteDoctor() {
        delete("/delete/{id}") {
            val idToDelete = call.parameters["id"] ?: return@delete call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            try {
                doctorService.deleteDoctor(idToDelete)

                call.respondText("Doctor with ID $idToDelete deleted successfully", status = HttpStatusCode.OK)
            } catch (e: DoctorNotFoundException) {
                call.respondText("${e.message}", status = HttpStatusCode.NotFound)

            } catch (e: Exception) {
                call.respondText("Error deleting doctor: ${e.message}",status = HttpStatusCode.InternalServerError)
            }
        }
    }

    private fun Route.availableHours() {
        get("/availableHours/{id}/{day}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            val day = call.parameters["day"] ?: return@get call.respondText ("Bad request",
                status = HttpStatusCode.BadRequest  )
            try {
                val docHours = doctorService.availableHours(id, day)
                call.respond(docHours)
            } catch (e: InvalidDateException) {
                call.respondText("${e.message}", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                call.respondText("Error retrieving available hours ${e.message}", status = HttpStatusCode.InternalServerError)
            }

        }
    }

    private fun Route.printSchedule() {
        get("/printSchedule/{id}/{startDate}") {
            val id = call.parameters["id"] ?: return@get call.respondText("Bad request", status = HttpStatusCode.BadRequest)
            val startDate = call.parameters["startDate"] ?: return@get call.respondText ("Bad request",
                status = HttpStatusCode.BadRequest  )
            try {
                val docPath = doctorService.printSchedule(id,startDate)
                call.respondText("Path: $docPath")
            } catch (e: InvalidDateException) {
                call.respondText("${e.message}", status = HttpStatusCode.BadRequest)
            }
            catch (e: Exception) {
                call.respondText("Error retrieving schedule ${e.message}", status = HttpStatusCode.InternalServerError)
            }

        }
    }
}