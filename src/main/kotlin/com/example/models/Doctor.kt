package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
class Doctor(
    //Must change the id generation, its 128 bits and that is too long
    val id: Int,
    internal var name :Name,
    internal var age: Int,
    internal var email: String? = "",
    internal var gender: Gender= Gender.NONE,
    internal var dateOfBirth:String = "",
    internal var department: String = "")

class DoctorDB {
    init {
        transaction {
            SchemaUtils.create(Doctors)
        }
    }
    fun getDoctorById(id: String): Doctor? {
        //Finds the Doctor by id
        try {
            return transaction {
                Doctors.select { Doctors.id eq id.toInt() }.map { rowToDoctor(it) }.singleOrNull()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //If list returned from db is empty, return false
    fun doctorExists(id: Int): Boolean {
        try {
            return transaction {
                !Doctors.select { Doctors.id eq id}.empty()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }


    fun updateDoctor(doctor: Doctor) {
        //Improvement
        //Make it only changes values that actually need changing and
        //Doesn't require all the values.
        try {
            transaction {
                Doctors.update({Doctors.id eq doctor.id}) {
                    it[firstName] = doctor.name.firstName
                    it[lastName] = doctor.name.lastName
                    it[age] = doctor.age
                    it[email] = doctor.email
                    it[gender] = doctor.gender
                    it[dateOfBirth] = doctor.dateOfBirth
                    it[department] = doctor.department
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //Returns a list of the patients or an empty list if something happens
    //NEEDS REFACTORING, WE SHOULD ALLOW EMPTY LIST RETURN AND NOT CONFUSE IT WITH EXCEPTION HANDLING
    fun getDoctor():List<Doctor> {
        try {
            return transaction {
                Doctors.selectAll().map { rowToDoctor(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

    }

    fun addDoctor(doctor: Doctor):Boolean {
        try {
            transaction {
                Doctors.insert {
                    it[firstName] = doctor.name.firstName
                    it[lastName] = doctor.name.lastName
                    it[age] = doctor.age
                    it[email] = doctor.email
                    it[gender] = doctor.gender
                    it[dateOfBirth] = doctor.dateOfBirth
                    it[department] = doctor.department
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }
    fun deleteDoctor(idToDelete: String) {
        try {
            transaction {
                Doctors.deleteWhere { id eq idToDelete.toInt() }
                println("Doctor with ID $idToDelete deleted successfully.")
            }
        } catch (e:Exception) {
            println("Error deleting doctor with ID $idToDelete: ${e.message}")
            e.printStackTrace()
        }

    }


    //Helper function
    private fun rowToDoctor(row : ResultRow) : Doctor {
        return Doctor(
            id = row[Doctors.id],
            name = Name(row[Doctors.firstName], row[Doctors.lastName]),
            age = row[Doctors.age],
            email = row[Doctors.email],
            gender = row[Doctors.gender],
            dateOfBirth = row[Doctors.dateOfBirth]
        )
    }

}

//Service layer that contains the logic or updating and general operations to the patient class
//This separates the database and the routing making for a more robust approach with clear separation of duties.
//Database handles the data, service layer does the logic and interfaces with routing and database
//Routing does handle the client requests and passes them on to the service layer.
//Seems to be the common architecture these days as I understand it
class DoctorService(private val doctorDB: DoctorDB) {
    fun updateDoctorDetails(id:String, updatedDoctor: Doctor): Boolean {
        val existingDoctor = doctorDB.getDoctorById(id) ?: return false
        //Updates the existing doctor information with the one received.
        existingDoctor.apply {
            name = Name(
                firstName = updatedDoctor.name.firstName,
                lastName = updatedDoctor.name.lastName
            )
            age = updatedDoctor.age
            email = updatedDoctor.email
            gender = updatedDoctor.gender
            dateOfBirth = updatedDoctor.dateOfBirth
            department = updatedDoctor.department
        }
        doctorDB.updateDoctor(existingDoctor)
        return true
    }

    fun getDoctor(): List<Doctor> {
        return doctorDB.getDoctor()
    }
    fun addDoctor(newDoctor: Doctor): Boolean {
        val success = doctorDB.addDoctor(newDoctor)
        if (success) {
            println("It worked!!!!!")
            return success
        } else {
            println("Didnt work, service layer")
            return success
        }
    }

    fun deleteDoctor(id: String):Boolean {
        if (doctorDB.doctorExists(id.toInt())) {
            doctorDB.deleteDoctor(id)
            return true
        } else {
            return false
        }
    }
}