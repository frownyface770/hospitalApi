package com.example.models
import com.example.models.Doctors.autoIncrement
import org.jetbrains.exposed.sql.ForeignKeyConstraint
import org.jetbrains.exposed.sql.Table


object Patients : Table() {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", length = 100)
    val lastName = varchar("last_name", length = 100)
    val age = integer("age")
    val email = varchar("email", length = 255).nullable()
    var gender = enumeration("gender", Gender::class)
    var dateOfBirth = varchar("date_of_birth", length = 20)
    val nationalHealthNumber = varchar("national_health_number", length = 9).uniqueIndex()

    //Overides the primaryKey and names it for clarity's sake
    override val primaryKey = PrimaryKey(id,name ="PK_Patients_ID")
}
object Doctors : Table() {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", length=100)
    val lastName = varchar("last_name", length=100)
    val age = integer("age")
    val email = varchar("email", length = 255).nullable()
    var gender = enumeration("gender", Gender::class)
    var dateOfBirth = varchar("date_of_birth", length=20)
    var department = varchar("department", length=20)

    //Overides the primaryKey and names it for clarity's sake
    override val primaryKey = PrimaryKey(id,name ="PK_Doctor_ID")
}
//creation of the table MedicalInformations
object MedicalInformations : Table() {
    val id = integer("id").autoIncrement()
    val patientId = integer("patientId").references(Patients.id)
    val data = varchar("data", length = 10)
    val sintoms = varchar("sintoms", length = 255)
    val diagonostic = varchar("diagonostic", length = 255)
    val medication = varchar("medication", length = 255)
    val notes = text("notes")
}
    object Appointments : Table() {
    val id = integer("id").autoIncrement()
    val date = varchar("date", length=10)
    val time = varchar("time", length = 5)
    val patientID = integer("patientID").references(Patients.id)
    val doctorID = integer("doctorID").references(Doctors.id)
    val patientComments = text("patientComments")

    override val primaryKey = PrimaryKey(id)
        init {
            uniqueIndex(date,time, doctorID)
        }
}
