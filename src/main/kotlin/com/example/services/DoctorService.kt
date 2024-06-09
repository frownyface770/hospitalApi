package com.example.services

import com.example.exceptions.DoctorNotFoundException
import com.example.exceptions.InvalidDateException
import com.example.models.Appointment
import com.example.models.Doctor
import com.example.models.DoctorDB
import com.example.models.Validation
import com.example.plugins.PDFMaker
import java.time.LocalDate
import java.time.LocalTime


//Service layer that contains the logic or updating and general operations to the patient class
//This separates the database and the routing making for a more robust approach with clear separation of duties.
//Database handles the data, service layer does the logic and interfaces with routing and database
//Routing does handle the client requests and passes them on to the service layer.
//Seems to be the common architecture these days as I understand it
class DoctorService(private val doctorDB: DoctorDB) {
    fun updateDoctorDetails(id:String, updatedDoctor: Doctor) {
        if(doctorDB.doctorExists(id.toInt())) {
            doctorDB.updateDoctor(id.toInt(),updatedDoctor)
        } else {
            throw DoctorNotFoundException(id)
        }

    }

    fun getDoctor(): List<Doctor> {
        return doctorDB.getDoctor()
    }
    fun addDoctor(newDoctor: Doctor) {
        doctorDB.addDoctor(newDoctor)
    }

    fun deleteDoctor(id: String) {
        if (doctorDB.doctorExists(id.toInt())) {
            doctorDB.deleteDoctor(id)
        } else {
            throw DoctorNotFoundException(id)
        }
    }
    //Complicated function that returns the available hours for the given day and doctor. Assuming each appointment is an hour long.
    fun availableHours(id: String, date: String): List<String> {
        val today = LocalDate.now()

        if (!Validation.isValidDate(date)) {
            throw InvalidDateException(date)
        }

        val dateToCheck = LocalDate.of(date.slice(6..9).toInt(),date.slice(3..4).toInt(),date.slice(0..1).toInt())
        if (dateToCheck.isBefore(today)) {
            return emptyList()
        }

        val now = LocalTime.now()
        println("today: $today now $now")
        //Fetch the appointments for the day and doctor
        val appointments: List<Appointment>
        val workingHours: List<Pair<String, String>>
        try {
            appointments = doctorDB.fetchAppointments(id).filter { it.date == date }
            //Get the doctor's working hours, for now they are all the same but that can change in the future
            workingHours = doctorDB.fetchWorkingHours(id).toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        val availableSlots = mutableListOf<Int>()

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
        if (!Validation.isValidDate(startDate)) {
            throw InvalidDateException(startDate)
        }
        val appointmentList = doctorDB.fetchAppointments(id)
        val doctor = doctorDB.getDoctorById(id)
        if (doctor != null) {
            val path = PDFMaker.generatePDF(doctor,startDate,"$id-$startDate.pdf",appointmentList)
            return path
        }
        return null
    }

}