package com.example.models
import org.jetbrains.exposed.sql.Table
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
//criação da "base de dados" dos registos médicos
object MedicalInformations: Table(){
    //liga o id do paciente com a tabela do id do paciente
    val patientId = integer("patientId").references(Patients.id)
    val data = varchar("data", length=10)
    val sintoms = varchar("sintoms", length = 255)
    val diagonostic = varchar("diagonostic",length = 255)
    val medication = varchar("medication",length = 255)
    val notes = text("notes")
    //basicamente faz com que a chave primária seja o id do paciente
    override val primaryKey = PrimaryKey(patientId, name = "PK_MedicalInformation_ID")
}