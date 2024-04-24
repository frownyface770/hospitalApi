//package com.example.models
//
//
//import kotlinx.serialization.Serializable
//import org.jetbrains.exposed.sql.ResultRow
//import org.jetbrains.exposed.sql.SchemaUtils
//import org.jetbrains.exposed.sql.*
//import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
//import org.jetbrains.exposed.sql.select
//import org.jetbrains.exposed.sql.transactions.transaction
//import java.util.UUID
//
////serialização
//@Serializable
////criação da class
//class MedicalInformation(
//    //Must change the id generation, its 128 bits and that is too long
//    val patientId: Int,
//    internal var data: String,
//    internal var sintoms: String,
//    internal var diagnostic: String,
//    internal var medication: String,
//    internal var notes: String
//)
////class para as transações da API
//class MedicalInformatonDB{
//    //inicializador de transação
//    init {
//        transaction {
//            SchemaUtils.create(MedicalInformations)
//        }
//    }
//    fun rowToMedicalInformation(row : ResultRow) : MedicalInformation{
//        return MedicalInformation(
//            patientId = row[MedicalInformations.patientId],
//            data = row[MedicalInformations.data],
//            sintoms = row[MedicalInformations.sintoms],
//            diagnostic = row[MedicalInformations.diagonostic],
//            medication = row[MedicalInformations.medication],
//            notes = row[MedicalInformations.notes]
//        )
//    }
//    //função que retorna a pesquisa dos registos médicos através do ID do paciente
//    fun getMedicalInformationByID(patientId: Int): List<MedicalInformation> {
//        return transaction{
//            MedicalInformations.select{
//                MedicalInformations.patientId eq patientId
//            }
//                .mapNotNull{
//                    rowToMedicalInformation(it)
//                }
//        }
//    }
//    //função que retorna um valor verdadeiro ou falso se a informação sobre o registo existir
//    fun medicalInformationExists(patientId: Int): Boolean{
//        return transaction{
//            !MedicalInformations.select{ MedicalInformations.patientID eq patientId}.empty()
//        }
//    }
//    //faz o update dos dados dos registos medicos
//    fun updateMedicalInformation(medicalInformation: MedicalInformations){
//        transaction{
//            MedicalInformations.update({
//                it[data] = MedicalInformations.data
//                it[sintoms] = medicalInformations.sintoms
//                it[diagnostic] = medicalInformations.diagnostic
//                it[medication] = medicalInformations.medication
//                it[notes] = medicalInformations.notes
//            })
//        }
//    }
//    //extrair todos os registos médicos
//    fun getMedicalInformations(): List<Unit> {
//        return transaction{
//            MedicalInformations.selectAll().map{rowToMedicalInformation(it)}
//        }
//    }
//    //função para adicionar registo médico à base de dados
//    fun addMedicalInformation(medicalInformation: MedicalInformation):Boolean{
//        return try {
//            transaction{
//                MedicalInformations.insert{
//                    it[data] = medicalInformation.data
//                    it[sintoms] = medicalInformation.sintoms
//                    it[diagnostic] = medicalInformation.diagnostic
//                    it[medication] = medicalInformation.medication
//                    it[notes] = medicalInformation.notes
//                }
//            }
//            true
//        }catch (e:Exception){
//            e.printStackTrace()
//            false
//        }
//    }
//
//
//}
