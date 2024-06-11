package com.example.plugins
import com.example.models.MedicalInformation
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.File

//class pdf generator
class PdfGenerator(private val outPath: String) {
    //call of the function
    init {
        makesureDirectoryExist(outPath)
    }
    //function to verify if the directory exists/ else it will create the missing directory
    private fun makesureDirectoryExist(filepath: String){
        val file = File(filepath)
        if (!file.exists()){
           file.mkdirs()
        }
    }
    //function to generate the pdf
    fun genPdf(medicalInformation: List<MedicalInformation>, filename: String){
        //the set of the basic variables
        val document = PDDocument()
        val page = PDPage()
        document.addPage(page)
        //the setup variable
        val contentStream = PDPageContentStream(document, page)
        //setup begins here (configs)
        contentStream.beginText()
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12F)
        contentStream.newLineAtOffset(50f, 750f)
        //display of the information
        medicalInformation.forEach { info ->
            contentStream.showText("Patient ID: ${info.patientId}")
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.showText("Date: ${info.data}")
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.showText("Sintoms: ${info.sintoms}")
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.showText("Diagonostic: ${info.diagnostic}")
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.showText("Medication: ${info.medication}")
            contentStream.newLineAtOffset(0f, -15f)
            contentStream.showText("Notes: ${info.notes}")
            contentStream.newLineAtOffset(0f, -30f)
        }
        //finishing of the text
        contentStream.endText()
        contentStream.close()
        //saving the pdf
        document.save("$outPath/$filename.pdf")
        document.close()
    }
}
