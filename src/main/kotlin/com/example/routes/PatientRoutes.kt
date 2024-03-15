package com.example.routes
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
class PatientRoutes {
    val patientDB = PatientDB()
    val patientService = PatientService(patientDB)
    fun setUpPatientRoutes(route: Route) {
        route.route("/patients") {
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
            val patientStorage = patientService.getPatients()
            if (patientStorage.isNotEmpty()) {
                call.respond(patientStorage)
            } else {
                call.respondText("No patients found", status = HttpStatusCode.OK)
            }
        }
    }

    private fun Route.updatePatient() {
        post("{id?}/updateInfo") {
            val updatedPatient = call.receive<Patient>()

            val id = call.parameters["id"] ?: return@post call.respondText(
                "Bad request",
                status = HttpStatusCode.BadRequest
            )
            val success = patientService.updatePatientDetails(id,updatedPatient)
            if (success) {
                call.respondText("Patient information updated succesfully", status = HttpStatusCode.OK)
            } else {
                call.respondText("Patient Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}