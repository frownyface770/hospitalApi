package com.example.services

import com.example.exceptions.PatientAlreadyExistsException
import com.example.exceptions.PatientNotFoundException
import com.example.exceptions.ServiceException
import com.example.models.Patient
import com.example.models.PatientDB

//Service layer that contains the logic or updating and general operations to the patient class
//This separates the database and the routing making for a more robust approach with clear separation of duties.
//Database handles the data, service layer does the logic and interfaces with routing and database
//Routing does handle the client requests and passes them on to the service layer.
//Seems to be the common architecture these days as I understand it
class PatientService(private val patientDB: PatientDB) {
    fun updatePatientDetails(id: String, updatedPatient: Patient) {
        if (patientDB.patientExists(id.toInt())) {
            patientDB.updatePatient(id.toInt(), updatedPatient)
        } else {
            throw PatientNotFoundException(id)
        }

    }

    fun getPatients(): List<Patient> {
        return patientDB.getPatients()
    }

    fun addPatient(newPatient: Patient) {
        try {
            patientDB.addPatient(newPatient)
        } catch (e: PatientAlreadyExistsException) {
            println(e.message)
            throw ServiceException(e.message, e)
        }
    }

    fun deletePatient(id: String) {
        if (patientDB.patientExists(id.toInt())) {
            patientDB.deletePatient(id)
        } else {
            throw PatientNotFoundException(id)
        }
    }

    fun getPatientById(id: String): Patient? {
        return patientDB.getPatientById(id)
    }
}
