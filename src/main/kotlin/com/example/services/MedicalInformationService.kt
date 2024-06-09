package com.example.services

import com.example.models.MedicalInformation
import com.example.models.MedicalInformationDB

//This is a class to interact with the "program" it self. Basicaly is a class that takes care of the logic of the information above
//that comes from the database.
class MedicalInformationService(private val medicalInformationDB: MedicalInformationDB){
    //this method of this class MedicalInformationService its used as a way to make the updateMedicalInformation work by id.
    fun updateMedicalInformationData(id: Int, medicalInformationUpdated: MedicalInformation): Boolean {
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
    //function to return all medical information by patient id
    fun getAllMedicalInformationByPatientId(patientId: Int): List<MedicalInformation>{
        return medicalInformationDB.getMedicalInformationByPatientId(patientId)
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


