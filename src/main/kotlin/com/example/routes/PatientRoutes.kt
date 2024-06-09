package com.example.routes
import com.example.exceptions.*
import com.example.models.*
import com.example.services.PatientService
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


class PatientRoutes {
    //Initializes the patient Database, will be replaced by an actual database, maybe...
    private val patientDB = PatientDB()
    //Initializes service layer that handles the patients
    private val patientService = PatientService(patientDB)
    fun setUpPatientRoutes(route: Route) {
        route.route("/patients") {
            //Calls the functions that actually handle the routing
            listPatients()
            createPatient()
            updatePatient()
            deletePatient()
            getPatientById()
        }
    }



    private fun Route.createPatient() {
        post("/createPatient") {
            val patient = call.receive<Patient>()
//            if(patient::class.memberProperties.any { it.call(patient) == null }) {
//                return@post call.respondText ( "Please provide all the required information.", status=HttpStatusCode.NotAcceptable )
//            }
            try {
                patientService.addPatient(patient)
                //Adds the patient details and stores the boolean value of operation's success
                //Checks if successful and acts accordingly

                call.respondText("Patient stored corretly", status = HttpStatusCode.Created)
            } catch (e: ServiceException) {
                if (e.originalException is PatientAlreadyExistsException) {
                    call.respondText("${e.originalException.message}", status = HttpStatusCode.Conflict)
                } else {
                    call.respondText("Error creating patient ${e.message}", status = HttpStatusCode.InternalServerError)
                }
            } catch (e: Exception) {
                call.respondText("Error creating patient ${e.message}", status = HttpStatusCode.InternalServerError)
            }


        }
    }
    private fun Route.listPatients() {
        get {
            //Gets the patients from the service layer
            try {
                val patientStorage = patientService.getPatients()
                call.respond(patientStorage)
            } catch (e: Exception) {
                call.respondText("Error retrieving patients. ${e.message}", status = HttpStatusCode.InternalServerError)
            }

        }
    }
    private fun Route.getPatientById() {
        get("/{id}"){
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            try {
                val patient = patientService.getPatientById(id) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(patient)
            } catch (e: Exception) {
                call.respondText("Error retrieving patient. ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }

    }


    private fun Route.updatePatient() {
        put("{id?}/updateInfo") {
            //Get new patient info from the POST request
            val updatedPatient = call.receive<Patient>()
    println(updatedPatient)
            //Get the id from the url parameters, if it's not there we send a bad request http code back
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            //Updates the patient details and stores the boolean value of operation's success
            try {
                patientService.updatePatientDetails(id,updatedPatient)

                call.respondText("Patient with $id information updated succesfully", status = HttpStatusCode.OK)

            } catch (e: PatientNotFoundException) {
                call.respondText("${e.message}", status = HttpStatusCode.NotFound)
            } catch (e: Exception) {
                call.respondText("Error updating patient: ${e.message}", status = HttpStatusCode.InternalServerError)
            }

        }
    }

    private fun Route.deletePatient() {
        delete("/delete/{id}") {
            val idToDelete = call.parameters["id"] ?: return@delete call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            try {
                patientService.deletePatient(idToDelete)

                call.respondText("Patient with ID $idToDelete deleted successfully", status = HttpStatusCode.OK)

            } catch (e: PatientNotFoundException) {
                call.respondText("${e.message}", status = HttpStatusCode.NotFound)

            } catch (e: Exception) {
                call.respondText("Error deleting patient: ${e.message}",status = HttpStatusCode.InternalServerError)
            }
        }
    }
}