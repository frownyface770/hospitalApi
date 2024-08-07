package com.example.routes
import com.example.models.*
import com.example.plugins.PdfGenerator
import com.example.services.MedicalInformationService
import com.example.services.PatientService
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class MedicalInformationRoutes {
    private val medicalInformationDB = MedicalInformationDB()
    private val medicalInformationService = MedicalInformationService(medicalInformationDB)
    private val pdfGenerator = PdfGenerator("src/medicalinformation")
    private val patientDB = PatientDB()
    private val patientService = PatientService(patientDB)
    fun setUpMedicalInformationRoutes(route: Route) {
        route.route("/medicalinformation") {
            createMedicalInformation()
            updateMedicalInformation()
            updateMedicalInformationPatient()
            getMedicalInformation()
            deleteMedicalInformation()
            genePdf()
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
        put("/{id}/updateMedicalInformation"){
            val id = call.parameters["id"]?.toInt() ?: return@put call.respond(HttpStatusCode.BadRequest)
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
        get("/{patientId}/getMedicalInformation"){
            val patientId = call.parameters["patientId"]?.toInt() ?: return@get call.respondText("Bad request", status = HttpStatusCode.BadRequest)
            try {
                val response = medicalInformationService.getAllMedicalInformationByPatientId(patientId)
                call.respond(response)
            }catch(e: Exception) {
                call.respondText("Error retrieving medical information ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    //function for get a list of the medical information
    private fun Route.getMedicalInformation() {
        get("/getMedicalInformation") {
            try {
                val response = medicalInformationService.getMedicalInformation()
                call.respond(response)
            }catch(e: Exception) {
                call.respondText("Error retrieving medical information ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    //function to delete medical information
    private fun Route.deleteMedicalInformation() {
        delete("/{id}/deleteMedicalInformation"){
            val id = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
            try {
                medicalInformationService.deleteMedicalInformation(id)
                call.respondText("Medical information deleted successfully", status = HttpStatusCode.OK)
            }catch(e: Exception) {
                call.respondText("Error deleting medical information ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    private fun Route.genePdf(){
        get("/{id}/genPdf") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respondText("Bad request", status = HttpStatusCode.BadRequest)
                val medicalInformation = medicalInformationService.getAllMedicalInformationByPatientId(id)
                val idforpat = call.parameters["id"]?: return@get call.respondText("Bad request", status = HttpStatusCode.BadRequest)
                val patientInformations = patientService.getPatientById(idforpat) ?: return@get call.respond(HttpStatusCode.NotFound)
                if (medicalInformation.isEmpty()){return@get call.respondText("No medical information found", status = HttpStatusCode.NoContent)}
                val pdffile = pdfGenerator.genPdf(medicalInformation,"${id}_medical_information",patientInformations)
                call.respond(pdffile)
            }catch(e: Exception) {
                call.respondText("Error generationg PDF file ${e.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}