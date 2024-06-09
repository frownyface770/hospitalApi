package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
class Doctor(
    val id: Int = 1,
    val name :Name,
    val age: Int,
    val email: String? = "",
    val gender: Gender= Gender.NONE,
    val dateOfBirth:String = "",
    val department: String = ""){
    //Lets assume all doctors work the same schedule
    val workingHours = listOf(Pair("08:00","12:00"),Pair("13:00","18:00"))
}

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


    fun updateDoctor(id:Int,doctor: Doctor) {

        try {
            transaction {
                Doctors.update({Doctors.id eq id}) {
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
            throw e
        }

    }

    //Returns a list of the doctors

    fun getDoctor():List<Doctor> {
        try {
            return transaction {
                Doctors.selectAll().map { rowToDoctor(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

    fun addDoctor(doctor: Doctor){
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
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
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
            throw e
        }

    }


    //Helper function
    private fun rowToDoctor(row : ResultRow) : Doctor {

        val doctorToReturn = Doctor(
            id = row[Doctors.id],
            name = Name(row[Doctors.firstName], row[Doctors.lastName]),
            age = row[Doctors.age],
            email = row[Doctors.email],
            gender = row[Doctors.gender],
            dateOfBirth = row[Doctors.dateOfBirth],
            department = row[Doctors.department]
        )

        return doctorToReturn
    }
    //Fetches appointments for the doctor
     internal fun fetchAppointments(docId: String):List<Appointment> {
         try {
             return transaction { Appointments.select{ Appointments.doctorID eq docId.toInt() }.map{
                 Appointment(
                     id = it[Appointments.id],
                     doctorID = it[Appointments.doctorID],
                     patientID = it[Appointments.patientID],
                     date = it[Appointments.date],
                     time = it[Appointments.time],
                     patientComments = it[Appointments.patientComments]
                 )
             } }
         } catch (e: Exception) {
             e.printStackTrace()
             throw e
         }
    }
    //Fetches the working hours for the doctor
    internal fun fetchWorkingHours(docId: String):List<Pair<String, String>> {
        try {
            val doctor = transaction { Doctors.select { Doctors.id eq docId.toInt() }.first() }
            return rowToDoctor(doctor).workingHours
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

}
