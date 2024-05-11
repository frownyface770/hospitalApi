package com.example.exceptions

class PatientAlreadyExistsException(id: String) : Exception("Patient with $id already exists")
class PatientNotFoundException(id: String) : Exception("Patient with $id id not found")


class AppointmentAlreadyExistsException() : Exception("An appointment for that day, time and doctor already exists.")
class AppointmentNotFoundException(id:String): Exception("Appointment with $id id not found")
class DoctorNotFoundException(id: String): Exception("Doctor with $id id not found")
class ServiceException(message: String?, val originalException: Exception) : Exception(message)