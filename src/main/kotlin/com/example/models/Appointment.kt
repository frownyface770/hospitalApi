package com.example.models

import com.example.exceptions.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


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

    fun getAllAppointments(): List<Appointment> {
        return transaction { Appointments.selectAll().map { rowToAppointment(it) } }
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
    fun appointmentExists(id: Int): Boolean {
        return transaction {
            !Appointments.select { Appointments.id eq id }.empty()
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
            if (e.message?.contains("appointments_date_time_doctorid_unique",ignoreCase = true) == true) {
                throw AppointmentAlreadyExistsException()
            }
            throw e
        }
    }
     fun updateAppointment(id:Int,updatedAppointment: Appointment) {
        try {
            transaction {
                Appointments.update({ Appointments.id eq id }) {
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

    fun deleteAppointment(idToDelete: Int) {
        try {
            transaction {
                Appointments.deleteWhere { Appointments.id eq idToDelete }
            }
        } catch (e: Exception) {
            println("Error deleting appointment with ID $idToDelete: ${e.message}")
            e.printStackTrace()
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










