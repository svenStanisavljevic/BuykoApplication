package hr.tvz.buykoclient.controllers.location

import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.model.Location
import hr.tvz.buykoclient.userSession
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import java.io.IOException


class LocationViewController {

    var selectedLocation = Location(0, "", "", "")
    val ini = IniConfig()

    @Override
    fun initialize() {
        if (userSession.role !== 2) {
            edit.isVisible = false
            delete.isVisible = false
        }

        selectedLocation = LocationSearchController.selectedLocation
        country.text = selectedLocation.country
        region.text = selectedLocation.region
        city.text = selectedLocation.city
    }

    @FXML
    private var country = Label()

    @FXML
    private var region = Label()

    @FXML
    private var city = Label()

    @FXML
    private var edit = Button()

    @FXML
    private var delete = Button()

    @FXML
    fun edit() {
        try {
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("locationEdit.fxml"))
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
            .delete("http://localhost:8080/locations/${selectedLocation.id}")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson()
        try {
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("locationSearch.fxml"))
            val parent = loader.load<Parent>()
            val scene = Scene(parent, ini.getWidth(), ini.getHeight())
            BuykoApplication.getStage().scene = scene
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}