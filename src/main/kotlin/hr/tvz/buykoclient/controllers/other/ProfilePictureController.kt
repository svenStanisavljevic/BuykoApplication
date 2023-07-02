package hr.tvz.buykoclient.controllers.other

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.model.User
import hr.tvz.buykoclient.userSession
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.image.*
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

class ProfilePictureController {

    val HEIGHT = 512
    val WIDTH = 512
    val tempFilePath = "src/main/resources/tempPicture.png"

    @Override
    fun initialize() {
        fetchSession(userSession.username, userSession.password)
        if (userSession.profilePicture == "" || userSession.profilePicture == null) {
            println("SKIP")
        } else {
            userSession.profilePicture = userSession.profilePicture.replace("=", "\\u003d")
            val decodedPicture: ByteArray = Base64.getMimeDecoder().decode(userSession.profilePicture)
            val input: InputStream = ByteArrayInputStream(decodedPicture)
            val bi = ImageIO.read(input)
            ImageIO.write(bi, "png", File(tempFilePath))
            val imageFile = File(tempFilePath)
            val image = Image(imageFile.toURI().toString())
            photo.image = image
        }
    }

    @FXML
    private fun onButton() {
        val fileChooser = FileChooser()
        val selectedFile = fileChooser.showOpenDialog(BuykoApplication.getStage())
        val picture = Image(selectedFile.toURI().toString())
        val filePath = selectedFile.path

        photo.image = resizeImage(picture)
        saveImageAsPng(photo.image)
        postImage(imageToString(filePath))

    }



    @FXML
    var photo = ImageView()

    @FXML
    val button = Button()


    private fun imageToString(filePath: String): String {
        val imageBytes = Files.readAllBytes(Paths.get(filePath))
        return Base64.getMimeEncoder().encodeToString(imageBytes)
    }

    private fun saveStringToPng(string: String) {
        val decodedBytes = Base64.getMimeDecoder().decode(string)
        val fos = FileOutputStream(tempFilePath)
        fos.write(decodedBytes)
        fos.close()
    }

    private fun postImage(encodedPicture: String) {
        val url = "http://localhost:8080/upload"
        val gson = Gson()
        val pictureObject = JsonObject().apply {
            addProperty("picture", encodedPicture)
        }
        val jsonString = gson.toJson(pictureObject)
        println(jsonString)
        val response = Unirest.post(url)
            .header("Content-type", "application/json")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .body(jsonString)
            .asJson().body.toString()
        println("-------${response}")
    }

    private fun saveImageAsPng(image: Image) {
        val bufferedImage = SwingFXUtils.fromFXImage(image, null)
        val tempFile = File(tempFilePath)
        if (tempFile.exists()) {
            tempFile.delete()
        }

        try {
            ImageIO.write(bufferedImage, "png", tempFile)
            println("Image saved as png: $tempFilePath")
        } catch (e: Exception) {
            println("Error while saving png: ${e.message}")
        }
    }
    private fun resizeImage(image: Image): WritableImage {

        val reader = image.pixelReader
        val resizedPicture = WritableImage(WIDTH, HEIGHT)
        val writer = resizedPicture.pixelWriter

        for (y in 0 until HEIGHT) {
            for (x in 0 until WIDTH) {
                val sourceX = (x.toDouble() / WIDTH * image.width).toInt()
                val sourceY = (y.toDouble() / HEIGHT * image.height).toInt()
                val color: Color = reader.getColor(sourceX, sourceY)
                writer.setColor(x, y, color)
            }
        }
        return resizedPicture
    }

    private fun fetchSession(username: String, password: String) {
        val response = Unirest
            .get("http://localhost:8080/users")
            .header("username", username)
            .header("password", password)
            .asJson().body.toString()
        val g = Gson()
        val userListType = object : TypeToken<ArrayList<User>>() {}.type
        val users: MutableList<User> = g.fromJson(response, userListType)
        users.forEach {
            if (username == it.username) {
                userSession = it
                it.password = password
            }
        }
    }
}