package com.example.models


import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Patient(
    //Must change the id generation, its 128 bits and that is too long
    val id: String = UUID.randomUUID().toString(),
    internal var name :Name,
    internal var age: Int,
    internal var email: String = "",
    internal var gender: Gender= Gender.NONE,
    internal var dateOfBirth:String = "")


//Class replacing the database for now
class PatientDB {
    private val patientStorage = mutableListOf(
        Patient(
            "1",
            Name("Miguel", "Silva"),
            2,
            "sample@gmail.com",
            Gender.MALE,
            "23-02-1998")
    )
    fun getPatientById(id: String): Patient? {
        //Finds the patient by id
        val patient = patientStorage.find {it.id == id} ?: return null
        return patient
    }

    //Updates the patient information by id
    fun updatePatient(patient: Patient) {
        //Finds the patient index from the db and updates the patient
        val index = patientStorage.indexOfFirst { it.id == patient.id }
        if (index != -1) {
            patientStorage[index] = patient
        }
    }
    //Returns a list of the patients
    fun getPatients():List<Patient> {
        return patientStorage
    }
    //Adds the patient if none with that id is found
    fun addPatient(newPatient: Patient) {
        patientStorage.add(newPatient)
    }
    fun patientExists(patient: Patient):Boolean {
        return patientStorage.any { it.id == patient.id}
    }
}

//Service layer that contains the logic or updating and general operations to the patient class
//This separates the database and the routing making for a more robust approach with clear separation of duties.
//Database handles the data, service layer does the logic and interfaces with routing and database
//Routing does handle the client requests and passes them on to the service layer.
//Seems to be the common architecture these days as I understand it
class PatientService(private val patientDB: PatientDB) {
    fun updatePatientDetails(id:String, updatedPatient: Patient): Boolean {
        val existingPatient = patientDB.getPatientById(id) ?: return false
        //Updates the existing patient information with the one received.
        existingPatient.apply {
            name = updatedPatient.name
            age = updatedPatient.age
            email = updatedPatient.email
            gender = updatedPatient.gender
            dateOfBirth = updatedPatient.dateOfBirth
        }
        patientDB.updatePatient(existingPatient)
        return true
    }
    fun addPatient(newPatient: Patient): Boolean {
        val patientExists = patientDB.patientExists(newPatient)
        if (!patientExists) {
            patientDB.addPatient(newPatient)
            return true
        } else {
            return false
        }
    }
    fun getPatients(): List<Patient> {
        return patientDB.getPatients()
    }
}
