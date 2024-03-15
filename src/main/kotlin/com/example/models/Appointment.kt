package com.example.models

import java.util.Date

data class Appointment(val id: String, val date: Date, val patient: Patient, val doctor: Doctor)