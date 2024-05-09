package com.example.models
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

//serialização
@Serializable
//criação da class
class MedicalInformation(
    val id:Int,
    val patientId: Int,
    internal var data: String,
    internal var sintoms: String,
    internal var diagnostic: String,
    internal var medication: String,
    internal var notes: String
)
//class para as transações da API
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
    //function to get the id of the medicalInformation
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
    //função que retorna a pesquisa dos registos médicos através do ID do paciente
    fun getMedicalInformationByPatientId(patientId: Int): List<MedicalInformation> {
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
    //função que retorna um valor verdadeiro ou falso se a informação sobre o registo existir
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
    //faz o update dos dados dos registos medicos
    fun updateMedicalInformation(medicalInformation: MedicalInformations){
        try {
            transaction{
                MedicalInformations.update{
                    it[data] = MedicalInformations.data
                    it[sintoms] = MedicalInformations.sintoms
                    it[diagonostic] = MedicalInformations.diagonostic
                    it[medication] = MedicalInformations.medication
                    it[notes] = MedicalInformations.notes
                }
            }
        }catch (e: Exception){
            println("Error: ${e.message}")
        }
    }
    //extrair todos os registos médicos
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
    //função para adicionar registo médico à base de dados
    fun addMedicalInformation(medicalInformation: MedicalInformations):Boolean{
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
}
//This is a class to interact with the "program" it self. Basicaly is a class that takes care of the logic of the information above
//that comes from the database.
class MedicalInformationService(private val medicalInformationDB:MedicalInformationDB){
    fun updateMedicalInformationData(id:Int,medicalInformationUpdated: MedicalInformation): Boolean {
        val existingMedicalInformation = medicalInformationDB.getMedicalInformationById(id) ?: throw Exception("This MedicalInformation doesn't exist")
        existingMedicalInformation.apply {
            data = medicalInformationUpdated.data
            sintoms = medicalInformationUpdated.sintoms
            diagnostic = medicalInformationUpdated.diagnostic
            medication = medicalInformationUpdated.medication
            notes = medicalInformationUpdated.notes
        }
        medicalInformationDB.updateMedicalInformationData(existingMedicalInformation)
        return trues
    }
}


