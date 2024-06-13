package com.example.exceptions

class PatientAlreadyExistsException : Exception("Patient already exists")
class PatientNotFoundException(id: String) : Exception("Patient with $id id not found")


class AppointmentAlreadyExistsException() : Exception("An appointment for that day, time and doctor already exists.")
class AppointmentNotFoundException(id:String): Exception("Appointment with $id id not found")

class InvalidDateException(date: String): Exception("Invalid date format: $date . Please use the format dd-MM-yyyy")
class InvalidTimeException(time: String): Exception("Invalid time format: $time . Please use a valid time format.")
class DoctorNotFoundException(id: String): Exception("Doctor with $id id not found")
class ServiceException(message: String?, val originalException: Exception) : Exception(message)