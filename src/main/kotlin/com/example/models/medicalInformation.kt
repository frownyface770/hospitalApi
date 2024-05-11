package com.example.models
import io.ktor.server.engine.*
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
    val id:Int,
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
    fun rowToMedicalInformation(row : ResultRow) : MedicalInformation{
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
    fun getMedicalInformationByPatientId(patientId: Int): MedicalInformation?{
        try {
            return transaction{
                MedicalInformations.select{
                    MedicalInformations.patientId eq patientId
                }
                    .mapNotNull{
                        rowToMedicalInformation(it)
                    }.first()
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
            return null
        }
    }
    //this function works to return a value true or false
    fun medicalInformationExists(patientId: Int): Boolean{
        try {
            return transaction{
                !MedicalInformations.select{ MedicalInformations.patientId eq patientId}.empty()
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
                    it[data] = MedicalInformations.data
                    it[sintoms] = MedicalInformations.sintoms
                    it[diagonostic] = MedicalInformations.diagonostic
                    it[medication] = MedicalInformations.medication
                    it[notes] = MedicalInformations.notes
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
            transaction {
                MedicalInformations.deleteWhere { id eq idMed }
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
        }
    }
}
//This is a class to interact with the "program" it self. Basicaly is a class that takes care of the logic of the information above
//that comes from the database.
class MedicalInformationService(private val medicalInformationDB:MedicalInformationDB){
    //this method of this class MedicalInformationService its used as a way to make the updateMedicalInformation work by id.
    fun updateMedicalInformationData(id:Int,medicalInformationUpdated: MedicalInformation): Boolean {
        val existingMedicalInformation = medicalInformationDB.getMedicalInformationById(id) ?: throw Exception("This MedicalInformation doesn't exist")
        existingMedicalInformation.apply {
            data = medicalInformationUpdated.data
            sintoms = medicalInformationUpdated.sintoms
            diagnostic = medicalInformationUpdated.diagnostic
            medication = medicalInformationUpdated.medication
            notes = medicalInformationUpdated.notes
        }
        medicalInformationDB.updateMedicalInformation(existingMedicalInformation)
        return true
    }
    //this method of this class MedicalInformationService its used as a way to make the updateMedicalInformation work by patientId.
    fun updateMedicalInformationDataPatient(patientid:Int,medicalInformationUpdated: MedicalInformation): Boolean {
        val existingInformation = medicalInformationDB.getMedicalInformationByPatientId(patientid) ?: throw Exception("This MedicalInformation doesn't exist")
        existingInformation.apply {
            data = medicalInformationUpdated.data
            sintoms = medicalInformationUpdated.sintoms
            diagnostic = medicalInformationUpdated.diagnostic
            medication = medicalInformationUpdated.medication
            notes = medicalInformationUpdated.notes
        }
        medicalInformationDB.updateMedicalInformationPatient(existingInformation)
        return true
    }
    //this method of this class MedicalInformationService its used as a way to make the getMedicalInformation work.
    fun getMedicalInformation(): List<MedicalInformation> {
        return medicalInformationDB.getMedicalInformations()
    }
    //this method of this class MedicalInformationService its used as a way to make the addMedicalInformation work.
    fun addMedicalInformation(newmedicalInformation: MedicalInformation): Boolean {
        try {
            return medicalInformationDB.addMedicalInformation(newmedicalInformation)
        }catch (e:Exception){
            println("Error: ${e.message}")
            return false
        }
    }
    //this method of this class MedicalInformationService its used as a way to make the deleteMedicalInformation work.
    fun deleteMedicalInformation(idMed: Int):Boolean{
        if (medicalInformationDB.medicalInformationExists(idMed)) {
            medicalInformationDB.deleteMedicalInformation(idMed)
            return true
        }else{
            throw Exception("This MedicalInformation doesn't exist")
            return false
        }
    }
}


