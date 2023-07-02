package hr.tvz.buykoclient.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.model.Order
import hr.tvz.buykoclient.model.Post
import hr.tvz.buykoclient.model.User
import hr.tvz.buykoclient.userSession
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.File

class PdfUtil {

    fun generateOrder(orders: MutableList<Order>) {
        val outputFile = File(
            "src/main/resources/orders/order-${System.currentTimeMillis().toString()}.pdf")
        val lines = mutableListOf<String>()
        orders.forEach {
            val p = fetchPostById(it.postId)
            val u = fetchUserById(it.sellerId)
            lines.add("")
            lines.add("Item: ${p.title}")
            lines.add("")
            lines.add("Buyer: ${userSession.username}")
            lines.add("       ${userSession.email}")
            lines.add("       ${userSession.phoneNumber}")
            lines.add("       ${userSession.location.country}")
            lines.add("       ${userSession.location.region}")
            lines.add("       ${userSession.location.city}")
            lines.add("       ${it.deliveryAddress}")
            lines.add("")
            lines.add("Seller: ${u.username}")
            lines.add("")
            lines.add("Delivery hours: ${it.availableHours}")
            lines.add("Payment option: ${it.paymentType}")
            lines.add("")
            lines.add("-----------------------------------------------------")
        }

        val document = PDDocument()
        try {

            val page = PDPage(PDRectangle.A3)
            document.addPage(page)

            val contentStream = PDPageContentStream(document, page)
            contentStream.setFont(PDType1Font.HELVETICA, 12f)

            val startX = 50f
            val startY = page.mediaBox.height - 50f
            val lineHeight = 20f

            var currentY = startY
            for (line in lines) {
                contentStream.beginText()
                contentStream.newLineAtOffset(startX, currentY)
                contentStream.showText(line)
                contentStream.endText()
                currentY -= lineHeight
            }

            contentStream.close()
            document.save(outputFile)

            println("PDF file generated successfully: ${outputFile.absolutePath}")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        } finally {
            document?.close()
        }
    }

    private fun fetchPostById(id: Int): Post {
        val response = Unirest
            .get("http://localhost:8080/posts/${id}")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson().body.toString()
        val g = Gson()
        val postListType = object : TypeToken<Post>() {}.type
        return g.fromJson(response, postListType)
    }

    private fun fetchUserById(id: Int): User {
        val response = Unirest
            .get("http://localhost:8080/users/${id}")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson().body.toString()
        val g = Gson()
        val postListType = object : TypeToken<User>() {}.type
        return g.fromJson(response, postListType)
    }

}