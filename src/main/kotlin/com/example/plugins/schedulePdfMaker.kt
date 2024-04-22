package com.example.plugins

import com.example.models.Appointment
import com.example.models.Doctor
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.awt.Color
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


object PDFMaker {
    fun generatePDF(owner: Doctor, rawStartDate: String, fileName: String, appointments: List<Appointment>): String {
        val filePath = "src/schedules/"

        val document = PDDocument()
        val page = PDPage()
        document.addPage(page)

        val contentStream = PDPageContentStream(document, page)
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)

        // Write owner details
        contentStream.beginText()
        contentStream.newLineAtOffset(30f, 750f)
        contentStream.showText("Schedule for: ${owner.name.firstName} ${owner.name.lastName}     Department: ${owner.department}")
        contentStream.endText()

        // Write date for the week
        var dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val startDate = LocalDate.parse(rawStartDate,dateFormatter)

        dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        val formattedDate = startDate.format(dateFormatter)
        val endDate = startDate.plusDays(6).format(dateFormatter)
        contentStream.beginText()
        contentStream.newLineAtOffset(50f, 730f)
        contentStream.showText("Week of: $formattedDate - $endDate")
        contentStream.endText()

        // Draw table headers
        val columnWidth = 65f
        val rowHeight = 30f
        val startX = 30f
        val startY = 670f
        val days = mutableListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        //Get current day of the week
        val weekDay = startDate.dayOfWeek.toString()
        val daysToShift = when(weekDay) {
            "MONDAY" -> 0
            "TUESDAY" -> 6
            "WEDNESDAY" -> 5
            "THURSDAY" -> 4
            "FRIDAY" -> 3
            "SATURDAY" -> 2
            "SUNDAY" -> 1
            else -> 0
        }
        Collections.rotate(days,daysToShift)
        // Get Schedule data from the database

        val scheduleData = getData(appointments,startDate, weekDay)

        // Draw row headers with borders and centered text
        var y = startY - rowHeight
        for (hour in 8..18) { // Hours from 8 AM to 6 PM
            contentStream.setStrokingColor(0f, 0f, 0f) // Black color for borders
            contentStream.addRect(startX, y, columnWidth, rowHeight)
            contentStream.stroke()
            contentStream.beginText()
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10f)
            val textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("$hour:00") / 1000 * 10f
            contentStream.newLineAtOffset(startX + (columnWidth - textWidth) / 2, y + rowHeight / 2 - 5)
            contentStream.showText("$hour:00")
            contentStream.endText()
            y -= rowHeight
        }



        // Draw column headers
        //Start X coordinates
        var x = startX + columnWidth
        for (day in days) {
            contentStream.setStrokingColor(0f, 0f, 0f) // Black color for borders
            contentStream.addRect(x, startY, columnWidth, rowHeight)
            contentStream.stroke()
            contentStream.beginText()
            //Set font
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10f)
            //Get textWidth for proper alignment
            val textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(day) / 1000 * 10f
            //Set the line coordinates
            contentStream.newLineAtOffset(x + (columnWidth - textWidth) / 2, startY + rowHeight / 2 - 5)
            //Write the text
            contentStream.showText(day)
            contentStream.endText()
            x += columnWidth
        }


        // Draw schedule data
        for (i in 0 until days.size) {
            for (j in 0 until 11) {
                val cellData = scheduleData[i][j]
                //If it's lunch and not saturday or sunday
                if (j == 4 && days[i] != "Saturday" && days[i] != "Sunday" ) {
                    val lightGray = Color(220, 220, 220)
                    contentStream.setNonStrokingColor(lightGray)
                    contentStream.addRect(startX+columnWidth*(1+i),startY-rowHeight*5, columnWidth,rowHeight)
                    contentStream.fill()
                    contentStream.setNonStrokingColor(Color.BLACK)
                }
                //If there is data we will write it to the schedule
                if (cellData.isNotEmpty()) {
                    contentStream.beginText()
                    contentStream.setFont(PDType1Font.HELVETICA, 8f)
                    val textWidth = PDType1Font.HELVETICA.getStringWidth(cellData) / 1000 * 8f
                    contentStream.newLineAtOffset(startX + columnWidth * (i + 1) + (columnWidth - textWidth) / 2, startY - rowHeight * (j + 1) + rowHeight / 2 - 3)
                    contentStream.showText(cellData)
                    contentStream.endText()
                }
            }
        }
        // Draw horizontal lines between rows
        for (i in 0 until 12) {
            contentStream.moveTo(startX, startY - i * rowHeight)
            contentStream.lineTo(startX + columnWidth*8, startY - i * rowHeight)
            contentStream.stroke()
        }
        // Draw vertical lines between columns
        for (i in 0 until days.size + 2) {
            contentStream.moveTo(startX + i * columnWidth, startY)
            contentStream.lineTo(startX + i * columnWidth, startY - 11 * rowHeight)
            contentStream.stroke()
        }


        contentStream.close()
    try {
        //Save the file
        val file = File(filePath+fileName)
        document.save(file)
        document.close()
        println("Path $filePath$fileName")
        return filePath+fileName
    } catch (e: IOException) {
        e.printStackTrace()
        println("Error occured with saving the file.")
        return "Internal server error" //maybe change this
    }

    }

    fun getData(appointmentList: List<Appointment>, startDateRaw:LocalDate, weekDay:String):Array<Array<String>> {

        val scheduleData = Array(7) { Array(11) { "" } }

        //Use simpleDateFormat because if of the same time of Calender.time
        var dateFormat = SimpleDateFormat("yyyy-MM-dd")

        //Format the start date
        val startDate = dateFormat.parse(startDateRaw.toString())
        dateFormat = SimpleDateFormat("dd-MM-yyyy")

        //Get a calendar object and set it to the start time of the schedule
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        //Get the end date of schedule
        calendar.add(Calendar.DAY_OF_YEAR,6)
        val endDate = calendar.time
        //Reset calendar time to start date
        calendar.time = startDate

        val weekDayOffSet = when (weekDay) {
            "MONDAY" -> 5
            "TUESDAY" -> 4
            "WEDNESDAY" -> 3
            "THURSDAY" -> 2
            "FRIDAY" -> 1
            "SATURDAY" -> 0
            "SUNDAY" -> -1
            else -> 0
        }
        //While we dont reach the endDate, for every appointment we have if the dates match, store it in the appointments for day
        while (calendar.time <= endDate) {
            val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) +weekDayOffSet)%7 //- 2 // Adjusting for Sunday
            val appointmentsForDay = appointmentList.filter { appointment ->
                val dateString = appointment.date

                val (day,month,year) = dateString.split("-")


                //val calendarDate = calendar.time

                calendar.get(Calendar.YEAR) == year.toInt() && calendar.get(Calendar.MONTH) +1== month.toInt() && calendar.get(Calendar.DAY_OF_MONTH) == day.toInt()

            }

            //For every appointment today write appointment to file at the appropriate times
            appointmentsForDay.forEach { appointment ->

                val timeString = appointment.time

                val hourOfDay = timeString.split(":")[0].toInt() - 8 // Adjusting for 8:00 starting hour

                val appointmentInfo = "Appointment"
                scheduleData[dayOfWeek][hourOfDay] = appointmentInfo
            }


            //While they still have the same 8:00 to 12:00 and 13:00 to 18:00
           // if (dayOfWeek+2 != Calendar.SUNDAY && dayOfWeek+2 != Calendar.SATURDAY) {
            if (dayOfWeek != weekDayOffSet +1 && dayOfWeek != weekDayOffSet && weekDayOffSet != 0 && weekDayOffSet != -1) {
                scheduleData[dayOfWeek][12-8] = "Lunch"
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to the next day

        }


        return scheduleData
    }

}
