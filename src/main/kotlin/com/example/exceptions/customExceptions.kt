package com.example.exceptions

class PatientAlreadyExistsException(id: String) : Exception("Patient with $id already exists")
class PatientNotFoundException(id: String) : Exception("Patient with $id doesn't exist")

class ServiceException(message: String?, val originalException: Exception) : Exception(message)