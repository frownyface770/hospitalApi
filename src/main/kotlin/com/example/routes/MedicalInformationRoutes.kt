package com.example.routes
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class MedicalInformationRoutes {
    private val medicalInformationDB = MedicalInformationDB()
    private val medicalInformationService = MedicalInformationService(medicalInformationDB)
    fun setUpMedicalInformationRoutes(route: Route) {
        route.route("/medicalinformation") {
            createMedicalInformation()
            updateMedicalInformation()
            updateMedicalInformationPatient()
            getMedicalInformation()
            deleteMedicalInformation()
        }
    }
    //function to create/add medical information
     private fun Route.createMedicalInformation() {
         post("/createMedicalInformation") {
             val medicalInformation = call.receive<MedicalInformation>()
             try {
                 medicalInformationService.addMedicalInformation(medicalInformation)
                 call.respondText("Medical information stored corretly", status = HttpStatusCode.Created)
             } catch(e: Exception) {
                 call.respondText("Error creating medical information ${e.message}", status = HttpStatusCode.InternalServerError)
             }
         }
     }
    //function for update medical information by id
    private fun Route.updateMedicalInformation() {
        put("/{id?}/updateMedicalInformation"){
            val id = call.parameters["id"]
            val medicalInformation = call.receive<MedicalInformation>()
            try {
                medicalInformationService.updateMedicalInformationData(id,medicalInformation)
                call.respondText("Medical information updated successfully", status = HttpStatusCode.OK)
            }catch(e: Exception) {
                call.respondText("Error updating medical information ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    //function for update medical information by patient id
    private fun Route.updateMedicalInformationPatient() {
        put("/{patientId}/updateMedicalInformation"){
            val patientId = call.parameters["patientId"]
            val medicalInformation = call.receive<MedicalInformation>()
            try {
                medicalInformationService.updateMedicalInformationDataPatient(patientId,medicalInformation)
                call.respondText("Medical information updated successfully", status = HttpStatusCode.OK)
            }catch(e: Exception) {
                call.respondText("Error updating medical information ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    //function for get a list of the medical information
    private fun Route.getMedicalInformation() {
        get("/getMedicalInformation") {
            try {
                medicalInformationService.getMedicalInformation()
                call.respondText("Medical information stored corretly", status = HttpStatusCode.OK)
            }catch(e: Exception) {
                call.respondText("Error retrieving medical information ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    //function to delete medical information
    private fun Route.deleteMedicalInformation() {
        delete("/{id}/deleteMedicalInformation"){
            val id = call.parameters["id"]
            try {
                medicalInformationService.deleteMedicalInformation(id)
                call.respondText("Medical information deleted successfully", status = HttpStatusCode.OK)
            }catch(e: Exception) {
                call.respondText("Error deleting medical information ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}