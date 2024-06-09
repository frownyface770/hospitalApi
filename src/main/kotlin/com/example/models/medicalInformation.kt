package com.example.models
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

//it is like a compiler for the json data
@Serializable
//this is the class that gets the info of the MedicalInformation
class MedicalInformation(
    val id:Int =1,
    val patientId: Int,
    internal var data: String,
    internal var sintoms: String,
    internal var diagnostic: String,
    internal var medication: String,
    internal var notes: String
)
//This is a class for the sql transactions, comunicates with DB
class MedicalInformationDB{
    //inicializador de transação
    init {
        transaction {
            SchemaUtils.create(MedicalInformations)
        }
    }
    private fun rowToMedicalInformation(row : ResultRow) : MedicalInformation{
        return MedicalInformation(
            id = row[MedicalInformations.id],
            patientId = row[MedicalInformations.patientId],
            data = row[MedicalInformations.data],
            sintoms = row[MedicalInformations.sintoms],
            diagnostic = row[MedicalInformations.diagonostic],
            medication = row[MedicalInformations.medication],
            notes = row[MedicalInformations.notes]
        )
    }
    //this function returns a view in a list format where the Id is the filter
    fun getMedicalInformationById(id: Int) : MedicalInformation?{
        try {
            return transaction{
                MedicalInformations.select{
                    MedicalInformations.id eq id
                }.mapNotNull {
                        rowToMedicalInformation(it)
                }.first()
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
            return null
        }
    }
    //this function returns a view in a list format where the patientId is the filter
    fun getMedicalInformationByPatientId(patientId: Int): List<MedicalInformation>{
        try {
            return transaction{
                MedicalInformations.select{
                    MedicalInformations.patientId eq patientId
                }
                    .mapNotNull{
                        rowToMedicalInformation(it)
                    }
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
            return emptyList()
        }
    }
    //this function works to return a value true or false
    fun medicalInformationExists(id: Int): Boolean{
        try {
            return transaction{
                !MedicalInformations.select{ MedicalInformations.id eq id}.empty()
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
            return false
        }
    }
    //this makes the update of the MedicalInformation
    fun updateMedicalInformation(medicalInformation: MedicalInformation){
        try {
            transaction{
                MedicalInformations.update({ MedicalInformations.id eq medicalInformation.id }){
                    it[data] = medicalInformation.data
                    it[sintoms] = medicalInformation.sintoms
                    it[diagonostic] = medicalInformation.diagnostic
                    it[medication] = medicalInformation.medication
                    it[notes] = medicalInformation.notes
                }
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
        }
    }
    //this makes the update of the MedicalInformation using the patientId
    fun updateMedicalInformationPatient(medicalInformation: MedicalInformation){
        try {
            transaction{
                MedicalInformations.update({ MedicalInformations.patientId eq medicalInformation.patientId }){
                    it[data] = medicalInformation.data
                    it[sintoms] = medicalInformation.sintoms
                    it[diagonostic] = medicalInformation.diagnostic
                    it[medication] = medicalInformation.medication
                    it[notes] = medicalInformation.notes
                }
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
        }
    }
    //this makes a list of the data in the sql DB
    fun getMedicalInformations(): List<MedicalInformation> {
        try {
            return transaction{
                MedicalInformations.selectAll().map{rowToMedicalInformation(it)}
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
            return emptyList()
        }
    }
    //this makes the insert of the MedicalInformation
    fun addMedicalInformation(medicalInformation: MedicalInformation):Boolean{
        return try {
            transaction{
                MedicalInformations.insert{
                    it[patientId] = medicalInformation.patientId
                    it[data] = medicalInformation.data
                    it[sintoms] = medicalInformation.sintoms
                    it[diagonostic] = medicalInformation.diagnostic
                    it[medication] = medicalInformation.medication
                    it[notes] = medicalInformation.notes
                }
            }
            true
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
    fun deleteMedicalInformation(idMed: Int){
        try {
            println("idMed:$idMed")
            transaction {
                MedicalInformations.deleteWhere { MedicalInformations.id eq idMed }
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
        }
    }
}
