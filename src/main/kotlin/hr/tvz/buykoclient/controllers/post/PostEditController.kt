package hr.tvz.buykoclient.controllers.post

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import hr.tvz.buykoclient.userSession
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import java.io.IOException

class PostEditController {

    val ini = IniConfig()

    @Override
    fun initialize() {
        translateFX()
        title.text = PostSearchController.selectedPost.title
        category.text = PostSearchController.selectedPost.category
        price.text = PostSearchController.selectedPost.price.toString()
        description.text = PostSearchController.selectedPost.description
    }

    @FXML
    private var title = TextField()

    @FXML
    private var category = TextField()

    @FXML
    private var price = TextField()

    @FXML
    private var description = TextArea()

    @FXML
    private var titleLabel = Label()

    @FXML
    private var categoryLabel = Label()

    @FXML
    private var priceLabel = Label()

    @FXML
    private var descriptionLabel = Label()

    @FXML
    private var button = Button()

    @FXML
    fun submit() {
        val u = userSession
        val url = "http://localhost:8080/posts/${PostSearchController.selectedPost.id}"
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
            addProperty("title", title.text)
            addProperty("description", description.text)
            addProperty("price", price.text.toDouble())
            addProperty("category", category.text)
            add("user", userObject)
        }

        val jsonString = gson.toJson(json)
        val response = Unirest.put(url)
                .header("Content-type", "application/json")
                .header("username", userSession.username)
                .header("password", userSession.password)
                .body(jsonString)
                .asJson()
        println("${response.status} ${response.body}")

        try {
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("postSearch.fxml"))
            val parent = loader.load<Parent>()
            val scene = Scene(parent, ini.getWidth(), ini.getHeight())
            BuykoApplication.getStage().scene = scene
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun translateFX() {
        val translate = Translate()
        titleLabel.text = translate.readXML(12, ini.getLang())
        descriptionLabel.text = translate.readXML(13, ini.getLang())
        priceLabel.text = translate.readXML(14, ini.getLang())
        categoryLabel.text = translate.readXML(15, ini.getLang())
        button.text = translate.readXML(28, ini.getLang())
    }
}