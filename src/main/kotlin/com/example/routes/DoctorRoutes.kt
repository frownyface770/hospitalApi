package com.example.routes
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
        }
    }
    private fun Route.createDoctor() {
        post("/createDoctor") {
            val doctor = call.receive<Doctor>()
            //I dont know if i should check this here or not.
            //Probably not.
            //val addSuccess = doctorService.addDoctor(doctor)
            try {
                val addSuccess = doctorService.addDoctor(doctor)
                //Adds the doctor details and stores the boolean value of operation's success
                //Checks if successful and acts accordingly
                if (addSuccess) {
                    call.respondText("Doctor stored corretly", status = HttpStatusCode.Created)
                } else {
                    call.respondText("Doctor already exists", status = HttpStatusCode.Conflict)
                }
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
                if (doctorStorage.isNotEmpty()) {
                    call.respond(doctorStorage)
                } else {
                    call.respondText("No doctor found", status = HttpStatusCode.OK)
                }
            } catch (e: Exception) {
                call.respondText("Error retrieving doctors ${e.message}", status = HttpStatusCode.InternalServerError)
            }

        }
    }

    private fun Route.updateDoctor() {
        post("{id?}/updateInfo") {
            //Get new doctor info from the POST request
            val updatedDoctor = call.receive<Doctor>()

            //Get the id from the url parameters, if it's not there we send a bad request http code back
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            //Updates the doctor details and stores the boolean value of operation's success
            try {
                val success = doctorService.updateDoctorDetails(id,updatedDoctor)
                if (success) {
                    call.respondText("Doctor with $id information updated succesfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Doctor with $id Not Found", status = HttpStatusCode.NotFound)
                }
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
                val deletionResult = doctorService.deleteDoctor(idToDelete)
                if (deletionResult) {
                    call.respondText("Doctor with ID $idToDelete deleted successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Doctor with ID $idToDelete NOT FOUND", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText("Error deleting doctor: ${e.message}",status = HttpStatusCode.InternalServerError)
            }
        }
    }
}