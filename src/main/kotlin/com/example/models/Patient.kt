package com.example.models

import com.example.exceptions.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
class Patient(
    val id: String = "10",
    internal val name: Name,
    internal val age: Int,
    internal val email: String? = "",
    internal val gender: Gender = Gender.NONE,
    internal val dateOfBirth: String = "",
    internal val nationalHealthNumber: String
)

//Class replacing the database for now
class PatientDB {
    init {
        transaction {
            SchemaUtils.create(Patients)
        }
    }

    fun getPatientById(id: String): Patient? {
        //Finds the patient by id
        return transaction {

            Patients.select { Patients.id eq id.toInt() }.map { rowToPatient(it) }.singleOrNull()
        }
    }

    //If list returned from db is empty, return false
    fun patientExists(id: Int): Boolean {
        try {
            return transaction {
                !Patients.select { Patients.id eq id }.empty()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    fun patientExistsByNhn(nhn: String): Boolean {
        return !transaction { Patients.select { Patients.nationalHealthNumber eq  nhn }.empty() }
    }

    fun updatePatient(id: Int, patient: Patient) {
        try {
            transaction {
                Patients.update({ Patients.id eq id }) {
                    it[firstName] = patient.name.firstName
                    it[lastName] = patient.name.lastName
                    it[age] = patient.age
                    it[email] = patient.email
                    it[gender] = patient.gender
                    it[dateOfBirth] = patient.dateOfBirth
                }
            }
        } catch (e: Exception) {
            println("Error updating patient: ${e.message}")
            throw e
        }
    }

    //Returns a list of the patients or an empty list if something happens
    fun getPatients(): List<Patient> {
//        try {
        return transaction {
            Patients.selectAll().map { rowToPatient(it) }

        }
    }

    fun addPatient(patient: Patient) {

        transaction {
            try {
                Patients.insert {
                    it[firstName] = patient.name.firstName
                    it[lastName] = patient.name.lastName
                    it[age] = patient.age
                    it[email] = patient.email
                    it[gender] = patient.gender
                    it[dateOfBirth] = patient.dateOfBirth
                    it[nationalHealthNumber] = patient.nationalHealthNumber
                }
            } catch (e: Exception) {
                println("Error inserting patient: ${e.message}")
                throw e
            }
        }
    }

    fun deletePatient(idToDelete: String) {
        try {
            transaction {
                Patients.deleteWhere { Patients.id eq idToDelete.toInt() }
            }
        } catch (e: Exception) {
            println("Error deleting patient with ID $idToDelete: ${e.message}")
            e.printStackTrace()
        }
    }


    //Helper function
    private fun rowToPatient(row: ResultRow): Patient {
        return Patient(
            id = row[Patients.id].toString(),
            name = Name(row[Patients.firstName], row[Patients.lastName]),
            age = row[Patients.age],
            email = row[Patients.email],
            gender = row[Patients.gender],
            dateOfBirth = row[Patients.dateOfBirth],
            nationalHealthNumber = row[Patients.nationalHealthNumber]
        )
    }

}

