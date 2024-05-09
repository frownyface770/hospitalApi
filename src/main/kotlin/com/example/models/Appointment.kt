package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Date

@Serializable
class Appointment(
    val id: Int = 1,
    val date: String,
    val time: String,
    val patientID: Int,
    val doctorID: Int,
    val patientComments: String)

class AppointmentDB {
    init {
        transaction {
            SchemaUtils.create(Appointments)
        }
    }

    fun getAppointmentByPatient(id: Int):List<Appointment> {
        try {
            return transaction {
                Appointments.select { Appointments.patientID eq id}.map {rowToAppointment(it)}
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    fun addAppointment(appointment: Appointment): Boolean {
        try {
            transaction {
                Appointments.insert {
                    it[date] = appointment.date
                    it[patientID] = appointment.patientID
                    it[time] = appointment.time
                    it[doctorID] = appointment.doctorID
                    it[patientComments] = appointment.patientComments
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            //We have a unique index in the database table of the date, time and doc id, if we try to insert another
            // appointment with those same details and exception is thrown. That message contains this exact message.
            // So we catch it and return a more meaningful message to the user.
            if (e.message?.contains("appointments_date_time_doctorid_unique") == true) {
                throw Exception("Appointment already exists at that time for the given doctor. Choose another time or doctor")
            }
            throw Exception("Refer to the documentation for more information.")
           // return false
        }
    }



    private fun rowToAppointment(row: ResultRow): Appointment {
        return Appointment(
            id = row[Appointments.id],
            date = row[Appointments.date],
            time = row[Appointments.time],
            patientID = row[Appointments.patientID],
            doctorID = row[Appointments.doctorID],
            patientComments = row[Appointments.patientComments]
        )
    }
}

class AppointmentService(private val appointmentDB: AppointmentDB) {
    fun getApointments(id:Int): List<Appointment> {
        return appointmentDB.getAppointmentByPatient(id)
    }

    fun addAppointment(newAppointment: Appointment): Boolean {

        return appointmentDB.addAppointment(newAppointment)
    }
}









