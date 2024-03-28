package com.example.models


import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID


@Serializable
class Patient(
    //Must change the id generation, its 128 bits and that is too long
    val id: String,
    internal var name :Name,
    internal var age: Int,
    internal var email: String? = "",
    internal var gender: Gender= Gender.NONE,
    internal var dateOfBirth:String = "")



//Class replacing the database for now
class PatientDB {
    init {
        transaction {
            SchemaUtils.create(Patients)
        }
    }
    fun getPatientById(id: String): Patient? {
        //Finds the patient by id
        try {
            return transaction {
                Patients.select { Patients.id eq id.toInt() }.map { rowToPatient(it) }.singleOrNull()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //If list returned from db is empty, return false
    fun patientExists(id: Int): Boolean {
        try {
            return transaction {
                !Patients.select { Patients.id eq id}.empty()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }


    fun updatePatient(patient: Patient) {
        //Improvement
        //Make it only changes values that actually need changing and
        //Doesn't require all the values.
        try {
            transaction {
                Patients.update({ Patients.id eq patient.id.toInt()}) {
                    it[firstName] = patient.name.firstName
                    it[lastName] = patient.name.lastName
                    it[age] = patient.age
                    it[email] = patient.email
                    it[gender] = patient.gender
                    it[dateOfBirth] = patient.dateOfBirth
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //Returns a list of the patients or an empty list if something happens
    //NEEDS REFACTORING, WE SHOULD ALLOW EMPTY LIST RETURN AND NOT CONFUSE IT WITH EXCEPTION HANDLING
    fun getPatients():List<Patient> {
        try {
            return transaction {
                Patients.selectAll().map { rowToPatient(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

    }

    fun addPatient(patient: Patient):Boolean {
        try {
            transaction {
                Patients.insert {
                    it[firstName] = patient.name.firstName
                    it[lastName] = patient.name.lastName
                    it[age] = patient.age
                    it[email] = patient.email
                    it[gender] = patient.gender
                    it[dateOfBirth] = patient.dateOfBirth
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }
    fun deletePatient(idToDelete: String) {
        try {
            transaction {
                Patients.deleteWhere { Patients.id eq idToDelete.toInt() }
                println("Patient with ID $idToDelete deleted successfully.")
            }
        } catch (e:Exception) {
            println("Error deleting patient with ID $idToDelete: ${e.message}")
            e.printStackTrace()
        }

    }


    //Helper function
    private fun rowToPatient(row : ResultRow) : Patient {
        return Patient(
            id = row[Patients.id].toString(),
            name = Name(row[Patients.firstName], row[Patients.lastName]),
            age = row[Patients.age],
            email = row[Patients.email],
            gender = row[Patients.gender],
            dateOfBirth = row[Patients.dateOfBirth]
        )
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
            name = Name(
                firstName = updatedPatient.name.firstName,
                lastName = updatedPatient.name.lastName
            )
            age = updatedPatient.age
            email = updatedPatient.email
            gender = updatedPatient.gender
            dateOfBirth = updatedPatient.dateOfBirth
        }
        patientDB.updatePatient(existingPatient)
        return true
    }

    fun getPatients(): List<Patient> {
        return patientDB.getPatients()
    }
    fun addPatient(newPatient: Patient): Boolean {
        val success = patientDB.addPatient(newPatient)
        if (success) {
            println("It worked!!!!!")
            return success
        } else {
            println("Didnt work, service layer")
            return success
        }
    }

    fun deletePatient(id: String):Boolean {
        if (patientDB.patientExists(id.toInt())) {
            patientDB.deletePatient(id)
        return true
        } else {
            return false
        }
    }
}
