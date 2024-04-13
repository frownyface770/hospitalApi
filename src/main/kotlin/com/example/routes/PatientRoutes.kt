package com.example.routes
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.reflect.full.memberProperties

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
        }
    }
    private fun Route.createPatient() {
        post("/createPatient") {
            val patient = call.receive<Patient>()
//            if(patient::class.memberProperties.any { it.call(patient) == null }) {
//                return@post call.respondText ( "Please provide all the required information.", status=HttpStatusCode.NotAcceptable )
//            }
            try {
                val addSuccess = patientService.addPatient(patient)
                //Adds the patient details and stores the boolean value of operation's success
                //Checks if successful and acts accordingly
                if (addSuccess) {
                    call.respondText("Patient stored corretly", status = HttpStatusCode.Created)
                } else {
                    call.respondText("Patient already exists", status = HttpStatusCode.Conflict)
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
                //Check if patients exist and respond accordingly
                if (patientStorage.isNotEmpty()) {
                    call.respond(patientStorage)
                } else {
                    call.respondText("No patients found", status = HttpStatusCode.OK)
                }
            } catch (e: Exception) {
                call.respondText("Error retrieving patients. ${e.message}", status = HttpStatusCode.InternalServerError)
            }

        }
    }

    private fun Route.updatePatient() {
        post("{id?}/updateInfo") {
            //Get new patient info from the POST request
            val updatedPatient = call.receive<Patient>()
    println(updatedPatient)
            //Get the id from the url parameters, if it's not there we send a bad request http code back
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            //Updates the patient details and stores the boolean value of operation's success
            try {
                val success = patientService.updatePatientDetails(id,updatedPatient)
                if (success) {
                    call.respondText("Patient with $id information updated succesfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Patient with $id Not Found", status = HttpStatusCode.NotFound)
                }
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
                val deletionResult = patientService.deletePatient(idToDelete)
                if (deletionResult) {
                    call.respondText("Patient with ID $idToDelete deleted successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Patient with ID $idToDelete NOT FOUND", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText("Error deleting patient: ${e.message}",status = HttpStatusCode.InternalServerError)
            }
        }
    }
}