package com.example.exceptions

class PatientAlreadyExistsException(id: String) : Exception("Patient with $id already exists")
class PatientNotFoundException(id: String) : Exception("Patient with $id doesn't exist")


class AppointmentAlreadyExistsException() : Exception("An appointment for that day, time and doctor already exists.")
class AppointmentNotFoundException(id:String): Exception("Appoint with $id id not found")
class ServiceException(message: String?, val originalException: Exception) : Exception(message)