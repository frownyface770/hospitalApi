package com.example.routes
import com.example.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class MedicalInformationRoutes {
    //private val MedicalInformationDB = MedicalInformations
    //private val MedicalInformationService = MedicalInformationService(MedicalInformationDB)
    fun setUpMedicalInformationRoutes(route: Route) {
        route.route("/medicalinformation") {
            //Calls the functions that actually handle the routing

        }
    }

}