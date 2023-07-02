package hr.tvz.buykoclient.controllers.user

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import hr.tvz.buykoclient.model.Location
import hr.tvz.buykoclient.model.User
import hr.tvz.buykoclient.userSession
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import java.io.IOException

class UserSearchController {
    companion object Companion {
        var selectedUser = User(0, "","","","","","", 1,"", 0.0, Location(0, "", "", ""))
    }

    private lateinit var users: MutableList<User>

    @Override
    fun initialize() {
        translateFX()
        users = fetchUsers()

        val observableUsers = FXCollections.observableArrayList(users)
        username.cellValueFactory = PropertyValueFactory("username")
        firstName.cellValueFactory = PropertyValueFactory("firstName")
        lastName.cellValueFactory = PropertyValueFactory("lastName")
        email.cellValueFactory = PropertyValueFactory("email")
        phoneNumber.cellValueFactory = PropertyValueFactory("phoneNumber")
        userTable.items.addAll(observableUsers)
    }

    @FXML
    private var userTable = TableView<User>()

    @FXML
    private var username = TableColumn<User, String>("username")

    @FXML
    private var firstName = TableColumn<User, String>("firstName")

    @FXML
    private var lastName = TableColumn<User, String>("lastName")

    @FXML
    private var email = TableColumn<User, String>("email")

    @FXML
    private var phoneNumber = TableColumn<User, String>("phoneNumber")

    @FXML
    private fun userClickListener() {
        val ini = IniConfig()
        userTable.rowFactory = Callback {
            val row = TableRow<User>()
            row.setOnMouseClicked { event ->
                if (event.clickCount == 2 && (!row.isEmpty)) {
                    selectedUser = row.item
                    try {
                        val loader = FXMLLoader(BuykoApplication::class.java.getResource("profile.fxml"))
                        val parent = loader.load<Parent>()
                        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
                        BuykoApplication.getStage().scene = scene
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            row
        }
    }

    private fun translateFX() {
        val translate = Translate()
        val ini = IniConfig()
        username.text = translate.readXML(3, ini.getLang())
        firstName.text = translate.readXML(4, ini.getLang())
        lastName.text = translate.readXML(5, ini.getLang())
        email.text = translate.readXML(6, ini.getLang())
        phoneNumber.text = translate.readXML(7, ini.getLang())
    }

    private fun fetchUsers(): MutableList<User> {
        userClickListener()
        val response = Unirest
            .get("http://localhost:8080/users")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson().body.toString()
        val g = Gson()
        val userListType = object : TypeToken<ArrayList<User>>() {}.type
        return g.fromJson(response, userListType)
    }

}