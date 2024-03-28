package com.example.models
import org.jetbrains.exposed.sql.ForeignKeyConstraint
import org.jetbrains.exposed.sql.Table
import java.util.Date

object Patients : Table() {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", length=100)
    val lastName = varchar("last_name", length=100)
    val age = integer("age")
    val email = varchar("email", length = 255).nullable()
    var gender = enumeration("gender", Gender::class)
    var dateOfBirth = varchar("date_of_birth", length=20)

    //Overides the primaryKey and names it for clarity's sake
    override val primaryKey = PrimaryKey(id,name ="PK_Patients_ID")
}

object Appointments : Table() {
    val id = integer("id").autoIncrement()
    val date = varchar("date", length=20)
    val patientID = integer("patientID").references(Patients.id)
    val doctorID = integer("doctorID").references(Doctor.id)
    val patientComments = text("patientComments")

    override val primaryKey = PrimaryKey(id)
}