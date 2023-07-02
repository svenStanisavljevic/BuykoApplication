package hr.tvz.buykoclient.controllers.user

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import hr.tvz.buykoclient.model.User
import hr.tvz.buykoclient.userSession
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField


class LoginController {

    val ini = IniConfig()

    @Override
    fun initialize() {
        translateFX()
    }

    @FXML
    var username = TextField()

    @FXML
    var password = PasswordField()

    @FXML
    var usernameLabel = Label()

    @FXML
    var passwordLabel = Label()

    @FXML
    var error = Label()

    @FXML
    var submit = Button()

    @FXML
    fun login() {
        error.text = ""
        if (loginStatus(username.text, password.text)) {
            fetchSession(username.text, password.text)
            val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("postSearch.fxml"))
            val scene = Scene(parent, ini.getWidth(), ini.getHeight())
            BuykoApplication.getStage().scene = scene
        } else {
            error.text = "Invalid Credentials"
        }
    }
    private fun loginStatus(username: String, password: String): Boolean {
        val url = "http://localhost:8080/login"
        val response: HttpResponse<String> = Unirest
            .get(url)
            .header("username", username)
            .header("password", password)
            .asString()

        val statusCode = response.status
        return statusCode == 200
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

    fun translateFX() {
        val translate = Translate()
        usernameLabel.text = translate.readXML(3, ini.getLang())
        passwordLabel.text = translate.readXML(17, ini.getLang())
        submit.text = translate.readXML(18, ini.getLang())
    }


}