package com.example.models

import com.example.exceptions.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Date

@Serializable
class Appointment(
    val id: Int = 1,
    var date: String,
    var time: String,
    var patientID: Int,
    var doctorID: Int,
    var patientComments: String)

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
    fun getAppointmentById(id: Int): Appointment? {
        try {
            return transaction {
                Appointments.select { Appointments.id eq id }.map { rowToAppointment(it) }.firstOrNull()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e

        }
    }

    fun addAppointment(appointment: Appointment) {
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

        } catch (e: Exception) {
            e.printStackTrace()
            //We have a unique index in the database table of the date, time and doc id, if we try to insert another
            // appointment with those same details and exception is thrown. That message contains this exact message.
            // So we catch it and return a more meaningful message to the user.
            if (e.message?.contains("appointments_date_time_doctorid_unique") == true) {
                throw AppointmentAlreadyExistsException()
            }
            throw e
           // return false
        }
    }
     fun updateAppointment(updatedAppointment: Appointment) {
        try {
            transaction {
                Appointments.update({ Appointments.id eq updatedAppointment.id }) {
                    it[date] = updatedAppointment.date
                    it[time] = updatedAppointment.time
                    it[patientID] = updatedAppointment.patientID
                    it[doctorID] = updatedAppointment.doctorID
                    it[patientComments] = updatedAppointment.patientComments
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
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

    fun addAppointment(newAppointment: Appointment) {
        appointmentDB.addAppointment(newAppointment)
    }

    fun updateAppointment(id:String,updatedAppointment: Appointment) {
        val existingAppointment = appointmentDB.getAppointmentById(id.toInt()) ?: throw AppointmentNotFoundException(id)
        existingAppointment.apply {
            existingAppointment.date = updatedAppointment.date
            existingAppointment.time = updatedAppointment.date
            existingAppointment.doctorID = updatedAppointment.doctorID
            existingAppointment.patientID = updatedAppointment.patientID
            existingAppointment.patientComments = updatedAppointment.patientComments
        }
        appointmentDB.updateAppointment(updatedAppointment)

    }
}









