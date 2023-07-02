package hr.tvz.buykoclient.controllers.location

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
import javafx.scene.control.TextField
import java.io.IOException

class LocationEditController {

    val ini = IniConfig()

    @Override
    fun initialize() {
        translateFX()
        city.text = LocationSearchController.selectedLocation.city
        region.text = LocationSearchController.selectedLocation.region
        country.text = LocationSearchController.selectedLocation.country
    }

    @FXML
    private var button = Button()

    @FXML
    private var cityLabel = Label()

    @FXML
    private var regionLabel = Label()

    @FXML
    private var countryLabel = Label()

    @FXML
    private var city = TextField()

    @FXML
    private var region = TextField()

    @FXML
    private var country = TextField()

    @FXML
    fun submit() {
        val url = "http://localhost:8080/locations/${LocationSearchController.selectedLocation.id}"
        val gson = Gson()

        val json= JsonObject().apply {
            addProperty("country", country.text)
            addProperty("region", region.text)
            addProperty("city", city.text)
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
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("locationSearch.fxml"))
            val parent = loader.load<Parent>()
            val scene = Scene(parent, ini.getWidth(), ini.getHeight())
            BuykoApplication.getStage().scene = scene
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun translateFX() {

        val translate = Translate()

        countryLabel.text = translate.readXML(25, ini.getLang())
        regionLabel.text = translate.readXML(26, ini.getLang())
        cityLabel.text = translate.readXML(27, ini.getLang())
        button.text = translate.readXML(28, ini.getLang())
    }

}