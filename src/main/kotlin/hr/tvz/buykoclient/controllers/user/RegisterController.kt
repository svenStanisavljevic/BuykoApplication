package hr.tvz.buykoclient.controllers.user

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jayway.jsonpath.JsonPath
import com.mashape.unirest.http.HttpResponse
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
import javafx.scene.control.*

class RegisterController {

    val ini = IniConfig()

    var location = Location(0, "", "", "")

    @Override
    fun initialize() {
        translateFX()
        val countries = FXCollections.observableArrayList(fetchCountries())
        country.items = countries
        countryListener()
        regionListener()
        cityListener()
    }

    @FXML
    var username = TextField()

    @FXML
    var firstName = TextField()

    @FXML
    var lastName = TextField()

    @FXML
    var email = TextField()

    @FXML
    var phoneNumber = TextField()

    @FXML
    var country = ChoiceBox<String>()

    @FXML
    var region = ChoiceBox<String>()

    @FXML
    var city = ChoiceBox<String>()

    @FXML
    var password = PasswordField()

    @FXML
    var usernameLabel = Label()

    @FXML
    var firstNameLabel = Label()

    @FXML
    var lastNameLabel = Label()

    @FXML
    var emailLabel = Label()

    @FXML
    var phoneNumberLabel = Label()

    @FXML
    var countryLabel = Label()

    @FXML
    var regionLabel = Label()

    @FXML
    var cityLabel = Label()

    @FXML
    var passwordLabel = Label()

    @FXML
    var error = Label()

    @FXML
    var submit = Button()

    @FXML
    fun register() {

        val url = "http://localhost:8080/register"

        val gson = Gson()
        val userObject = JsonObject().apply {
            addProperty("username", username.text)
            addProperty("firstName", firstName.text)
            addProperty("lastName", lastName.text)
            addProperty("email", email.text)
            addProperty("profilePicture", "")
            addProperty("password", password.text)
            addProperty("phoneNumber", phoneNumber.text)
            addProperty("balanceEur", 0.0)
            addProperty("creationTS", "")
            add("location", JsonObject().apply {
                addProperty("country", country.value)
                addProperty("region", region.value)
                addProperty("city", city.value)
            })
        }
        val jsonString = gson.toJson(userObject)
        val response = Unirest.post(url)
            .header("Content-type", "application/json")
            .body(jsonString)
            .asJson().body.toString()

        val session = gson.fromJson(response, User::class.java)
        userSession = session
        userSession.password = password.text

        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("postSearch.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    fun translateFX() {
        val translate = Translate()
        submit.text = translate.readXML(18, ini.getLang())
    }

    private fun fetchCountries(): List<String> {
        val url = "http://localhost:8080/locations"

        val response: HttpResponse<String> = Unirest
            .get(url)
            .asString()

        val responseBody = response.body
        return JsonPath.parse(responseBody).read<List<String>>("$..country").distinct()
    }

    private fun fetchRegions(country: String): List<String> {
        val url = "http://localhost:8080/locations/country/${country}"

        val response: HttpResponse<String> = Unirest
            .get(url)
            .asString()

        val responseBody = response.body

        return JsonPath.parse(responseBody).read<List<String>>("$..region").distinct()
    }

    private fun fetchCitys(region: String): List<String> {
        val url = "http://localhost:8080/locations/region/${region}"

        val response: HttpResponse<String> = Unirest
            .get(url)
            .asString()

        val responseBody = response.body

        return JsonPath.parse(responseBody).read<List<String>>("$..city").distinct()
    }

    private fun fetchLocation(city: String): Location {
        val url = "http://localhost:8080/locations/city/${city}"

        val response: String = Unirest
            .get(url)
            .asJson().body.toString()

        val g = Gson()
        val type = object : TypeToken<Location>() {}.type
        return g.fromJson(response, type)
    }

    private fun countryListener() {
        country.valueProperty().addListener { _, oldValue, newValue ->
            val regions = FXCollections.observableArrayList(fetchRegions(country.value))
            region.items = regions
        }
    }

    private fun regionListener() {
        region.valueProperty().addListener { _, oldValue, newValue ->
            val citys = FXCollections.observableArrayList(fetchCitys(region.value))
            city.items = citys
        }
    }

    private fun cityListener() {
        city.valueProperty().addListener { _, oldValue, newValue ->
            location = fetchLocation(city.value)
        }
    }

}