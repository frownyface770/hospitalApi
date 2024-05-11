package com.example.models

import com.example.plugins.PDFMaker
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalTime

@Serializable
class Doctor(
    val id: Int = 1,
    internal val name :Name,
    internal val age: Int,
    internal val email: String? = "",
    internal val gender: Gender= Gender.NONE,
    internal val dateOfBirth:String = "",
    internal val department: String = ""){
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
        //Improvement
        //Make it only changes values that actually need changing and
        //Doesn't require all the values.
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
         }
        return emptyList()
    }
    //Fetches the working hours for the doctor
    internal fun fetchWorkingHours(docId: String):List<Pair<String, String>> {
        val doctor = transaction { Doctors.select { Doctors.id eq docId.toInt() }.first() }
        return rowToDoctor(doctor).workingHours
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
//        existingDoctor.apply {
//            name = Name(
//                firstName = updatedDoctor.name.firstName,
//                lastName = updatedDoctor.name.lastName
//            )
//            age = updatedDoctor.age
//            email = updatedDoctor.email
//            gender = updatedDoctor.gender
//            dateOfBirth = updatedDoctor.dateOfBirth
//            department = updatedDoctor.department
//        }
        doctorDB.updateDoctor(id.toInt(),updatedDoctor)
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
    //Complicated function that returns the available hours for the given day and doctor. Assuming each appointment is an hour long.
    fun availableHours(id: String, date: String): List<String> {
        val today = LocalDate.now()
        val dateToCheck = LocalDate.of(date.slice(6..9).toInt(),date.slice(3..4).toInt(),date.slice(0..1).toInt())
        if (dateToCheck.isBefore(today)) {
            return emptyList()
        }

        val now = LocalTime.now()
        println("today: $today now $now")
        //Fetch the appointments for the day and doctor
        val appointments = doctorDB.fetchAppointments(id).filter { it.date == date }
        val availableSlots = mutableListOf<Int>()
        //Get the doctor's working hours, for now they are all the same but that can change in the future
        val workingHours = doctorDB.fetchWorkingHours(id).toMutableList()

        //For each workingHour pair(start time, end time) we have a list of 2 pairs.
        //Ex: Doctor starts at 8:00 and leaves at 12:00 for lunch returning at 13:00 and stays till 18:00.
        //So we have 2 pairs of start and end time. (8:00, 12:00) and (13:00, 18:00)
        workingHours.forEach { (start, end) ->
            //Set current time at start and drop the (:00) and convert to int. For now we only care about the hour.
            val startTime = LocalTime.parse(start)
            println("Start time: $startTime")
            var currentTime = if (!startTime.isAfter(now) && dateToCheck.isEqual(today)) {now.hour+1} else { start.dropLast(3).toInt()}
            //While the time +1 is before the end time for the doctor we add that timeframe as available
            while(currentTime + 1 <= end.dropLast(3).toInt()) {
                availableSlots.add(currentTime)//Pair(currentTime, currentTime + 1)
                currentTime += 1
                println("Current time: $currentTime")
            }
        }
        //For each of the appointments the doctor has for the day, we will take that timeSlot from his available hours
        //since he is busy
        appointments.forEach { appointment ->
            availableSlots.removeIf { slot ->
                slot >= appointment.time.dropLast(3).toInt() && slot +1 <= appointment.time.dropLast(3).toInt() + 1

            }
        }
        return availableSlots.map { "$it:00" }
    }

    fun printSchedule( id: String, startDate: String): String? {
        val appointmentList = doctorDB.fetchAppointments(id)
        val doctor = doctorDB.getDoctorById(id)
        if (doctor != null) {
            val path = PDFMaker.generatePDF(doctor,startDate,"$id-$startDate.pdf",appointmentList)
            return path
        }
        return null
    }

}