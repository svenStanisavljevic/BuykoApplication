package hr.tvz.buykoclient.controllers.post

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import hr.tvz.buykoclient.userSession
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import java.io.IOException


class PostCreateController {

    @Override
    fun initialize() {
        translateFX()
        buttonInit()
    }

    @FXML
    private lateinit var title: Label

    @FXML
    private lateinit var category: Label

    @FXML
    private lateinit var price: Label

    @FXML
    private lateinit var description: Label

    @FXML
    private lateinit var button: Button

    @FXML
    private lateinit var titleBox: TextField

    @FXML
    private lateinit var categoryBox: TextField

    @FXML
    private lateinit var priceBox: TextField

    @FXML
    private lateinit var descriptionBox: TextArea

    fun translateFX() {
        val ini = IniConfig()
        val translate = Translate()
        title.text = translate.readXML(12, ini.getLang())
        description.text = translate.readXML(13, ini.getLang())
        price.text = translate.readXML(14, ini.getLang())
        category.text = translate.readXML(15, ini.getLang())
        button.text = translate.readXML(16, ini.getLang())
    }

    fun buttonInit() {
        button.setOnAction {
            val ini = IniConfig()
            sendRequest()
            println("request sent - ${titleBox.text} ${userSession.username}")
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

    fun sendRequest() {
        val u = userSession
        val url = "http://localhost:8080/posts"
        val gson = Gson()

        val userObject = JsonObject().apply {
            addProperty("id", u.id)
            addProperty("username", u.username)
            addProperty("firstName", u.firstName)
            addProperty("lastName", u.lastName)
            addProperty("email", u.email)
            addProperty("profilePicture", u.profilePicture)
            addProperty("password", u.password)
            addProperty("phoneNumber", u.phoneNumber)
            addProperty("balanceEur", u.balanceEur)
            addProperty("creationTS", "")
            add("location", JsonObject().apply {
                addProperty("country", u.location.country)
                addProperty("region", u.location.region)
                addProperty("city", u.location.city)
                addProperty("id", u.location.id)
            })
        }

        val json = JsonObject().apply {
            addProperty("title", titleBox.text)
            addProperty("description", descriptionBox.text)
            addProperty("price", priceBox.text.toDouble())
            addProperty("category", categoryBox.text)
            add("user", userObject)
        }

        val jsonString = gson.toJson(json)
        val response = Unirest.post(url)
            .header("Content-type", "application/json")
            .header("username", u.username)
            .header("password", u.password)
            .body(jsonString)
            .asJson()
        println("${response.status} ${response.body}")
    }


}