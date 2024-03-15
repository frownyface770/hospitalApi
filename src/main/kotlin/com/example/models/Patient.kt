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

    fun updatePatient(patient: Patient) {
        //Finds the patient index from the db and updates the patient
        val index = patientStorage.indexOfFirst { it.id == patient.id }
        if (index != -1) {
            patientStorage[index] = patient
        }
    }
    fun getPatients():List<Patient> {
        return patientStorage
    }
    fun addPatient(newPatient: Patient): Boolean {
        if (!patientStorage.any{it.id == newPatient.id}) {
            patientStorage.add(newPatient)
        } else {
            return false
        }
        return true
    }
}
//internal val patientStorage = mutableListOf(
//    Patient(
//        "1",
//        Name("Miguel", "Silva"),
//        2,
//        "sample@gmail.com",
//        Gender.MALE,
//        "23-02-1998")
//)

//Service layer that contains the logic or updating and general operations to the patient class
//Seems to be the common architecture these days
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
        val success = patientDB.addPatient(newPatient)
        return success
    }
    fun getPatients(): List<Patient> {
        return patientDB.getPatients()
    }
}
