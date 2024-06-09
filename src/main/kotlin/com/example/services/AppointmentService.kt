package com.example.services

import com.example.exceptions.AppointmentNotFoundException
import com.example.exceptions.InvalidDateException
import com.example.exceptions.InvalidTimeException
import com.example.models.Appointment
import com.example.models.AppointmentDB
import com.example.models.Validation

class AppointmentService(private val appointmentDB: AppointmentDB) {
    fun getApointments(id:Int): List<Appointment> {
        return appointmentDB.getAppointmentByPatient(id)
    }

    fun addAppointment(newAppointment: Appointment) {
        if (!Validation.isValidDate(newAppointment.date)) {
            throw InvalidDateException(newAppointment.date)
        } else if (!Validation.isValidTime(newAppointment.time)) {
            throw InvalidTimeException(newAppointment.time)
        }
        appointmentDB.addAppointment(newAppointment)
    }

    fun updateAppointment(id:String,updatedAppointment: Appointment) {
        if ( appointmentDB.appointmentExists(id.toInt()) ) {
            if (!Validation.isValidDate(updatedAppointment.date)) {
                throw InvalidDateException(updatedAppointment.date)
            } else if (!Validation.isValidTime(updatedAppointment.time)) {
                throw InvalidTimeException(updatedAppointment.time)
            }
            appointmentDB.updateAppointment(id.toInt(),updatedAppointment)
        } else {
            throw AppointmentNotFoundException(id)
        }
    }

    fun deleteAppointment(idRaw:String) {
        val id = idRaw.toInt()
        if (appointmentDB.appointmentExists(id)) {
            appointmentDB.deleteAppointment(id)
        } else {
            throw AppointmentNotFoundException(idRaw)
        }

    }


}