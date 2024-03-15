package com.example.routes
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
class PatientRoutes {
    //Initializes the patient Database, will be replaced by an actual database, maybe...
    val patientDB = PatientDB()
    //Initializes service layer that handles the patients
    val patientService = PatientService(patientDB)
    fun setUpPatientRoutes(route: Route) {
        route.route("/patients") {
            //Calls the functions that actually handle the routing
            listPatients()
            createPatient()
            updatePatient()
        }
    }
    private fun Route.createPatient() {
        post("/createPatient") {
            val patient = call.receive<Patient>()
            //I dont know if i should check this here or not.
            //Probably not.
            val addSuccess = patientService.addPatient(patient)

            //Adds the patient details and stores the boolean value of operation's success
            //Checks if successful and acts accordingly
            if (addSuccess) {
                call.respondText("Patient stored corretly", status = HttpStatusCode.Created)
            } else {
                call.respondText("Patient already exists", status = HttpStatusCode.Conflict)
            }
            //val patientExists = patientStorage.any { it.id == patient.id}
//            if (patientExists) {
//                call.respondText("Patient already exists", status = HttpStatusCode.Conflict)
//            } else {
//                patientStorage.add(patient)
//                call.respondText("Patient store correctly", status = HttpStatusCode.Created)
//            }

        }
    }
    private fun Route.listPatients() {
        get {
            //Gets the patients from the service layer
            val patientStorage = patientService.getPatients()
            //Check if patients exist and respond accordingly
            if (patientStorage.isNotEmpty()) {
                call.respond(patientStorage)
            } else {
                call.respondText("No patients found", status = HttpStatusCode.OK)
            }
        }
    }

    private fun Route.updatePatient() {
        post("{id?}/updateInfo") {
            //Get new patient info from the POST request
            val updatedPatient = call.receive<Patient>()

            //Get the id from the url parameters, if it's not there we send a bad request http code back
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            //Updates the patient details and stores the boolean value of operation's success
            val success = patientService.updatePatientDetails(id,updatedPatient)
            //Checks the success of the update and acts accordingly
            if (success) {
                call.respondText("Patient information updated succesfully", status = HttpStatusCode.OK)
            } else {
                call.respondText("Patient Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}