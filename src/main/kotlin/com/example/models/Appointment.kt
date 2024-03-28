package com.example.models

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Date

class Appointment(
    val id: Int,
    val date: String,
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
                    it[doctorID] = appointment.doctorID
                    it[patientComments] = appointment.patientComments
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun rowToAppointment(row: ResultRow): Appointment {
        return Appointment(
            id = row[Appointments.id],
            date = row[Appointments.date],
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









