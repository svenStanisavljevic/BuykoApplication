package hr.tvz.buykoclient.controllers.user

import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import hr.tvz.buykoclient.model.Location
import hr.tvz.buykoclient.model.User
import hr.tvz.buykoclient.userSession
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.PixelWriter
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*
import javax.imageio.ImageIO

class ProfileController {

    val phImagePath = "src/main/resources/phPicture.png"
    val tempFilePath = "src/main/resources/tempPicture.png"
    val ini = IniConfig()
    var selectedUser = User(0, "", "", "", "", "", "", 1,"", 0.0, Location(0, "", "", ""))
    @Override
    fun initialize() {
        updatePhoto()
        translateFX()
        buttonInit()
        selectedUser = UserSearchController.selectedUser

        username.text = selectedUser.username
        firstName.text = selectedUser.firstName
        lastName.text = selectedUser.lastName
        email.text = selectedUser.email
        phone.text = selectedUser.phoneNumber
        country.text = selectedUser.location.country
        region.text = selectedUser.location.region
        city.text = selectedUser.location.city


    }

    @FXML
    private lateinit var username: Label

    @FXML
    private lateinit var firstName: Label

    @FXML
    private lateinit var lastName: Label

    @FXML
    private lateinit var email: Label

    @FXML
    private lateinit var phone: Label

    @FXML
    private lateinit var city: Label

    @FXML
    private lateinit var region: Label

    @FXML
    private lateinit var country: Label

    @FXML
    private lateinit var back: Button

    @FXML
    private var edit = Button()

    @FXML
    private var delete = Button()

    @FXML
    var photo = ImageView()


    @FXML
    fun edit() {
        try {
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("userEdit.fxml"))
            val parent = loader.load<Parent>()
            val scene = Scene(parent, ini.getWidth(), ini.getHeight())
            BuykoApplication.getStage().scene = scene
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @FXML
    fun delete() {
        val response = Unirest
            .delete("http://localhost:8080/users/${selectedUser.id}")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson()
        try {
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("userSearch.fxml"))
            val parent = loader.load<Parent>()
            val scene = Scene(parent, ini.getWidth(), ini.getHeight())
            BuykoApplication.getStage().scene = scene
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun translateFX() {
        val ini = IniConfig()
        val translate = Translate()
        back.text = translate.readXML(8, ini.getLang())
    }

    fun buttonInit() {
        back.setOnAction {
            try {
                val loader = FXMLLoader(BuykoApplication::class.java.getResource("userSearch.fxml"))
                val parent = loader.load<Parent>()
                val scene = Scene(parent, ini.getWidth(), ini.getHeight())
                BuykoApplication.getStage().scene = scene
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updatePhoto() {
        if (UserSearchController.selectedUser.profilePicture != null &&
            UserSearchController.selectedUser.profilePicture != "") {

            UserSearchController.selectedUser.profilePicture = UserSearchController.selectedUser.profilePicture.replace("=", "\\u003d")
            val decodedPicture: ByteArray = Base64.getMimeDecoder().decode(UserSearchController.selectedUser.profilePicture)
            val input: InputStream = ByteArrayInputStream(decodedPicture)
            val bi = ImageIO.read(input)
            ImageIO.write(bi, "png", File(tempFilePath))
            val imageFile = File(tempFilePath)
            val image = Image(imageFile.toURI().toString())
            photo.image = image
        }
        else {
            val imageFile = File(phImagePath)
            val image = Image(imageFile.toURI().toString())
            photo.image = image
        }
    }

}