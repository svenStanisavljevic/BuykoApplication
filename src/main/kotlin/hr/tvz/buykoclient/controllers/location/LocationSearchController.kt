package hr.tvz.buykoclient.controllers.location

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.model.Location
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

class LocationSearchController {

    companion object Companion {
        var selectedLocation = Location(0, "", "", "")
    }

    private lateinit var locations: List<Location>

    @Override
    fun initialize() {
        locations = fetchLocations()
        val observableLocations = FXCollections.observableArrayList(locations)
        city.cellValueFactory = PropertyValueFactory("city")
        region.cellValueFactory = PropertyValueFactory("region")
        country.cellValueFactory = PropertyValueFactory("country")
        locationTable.items.addAll(observableLocations)

    }

    @FXML
    private lateinit var locationTable: TableView<Location>

    @FXML
    private var city = TableColumn<Location, String>("city")

    @FXML
    private var region = TableColumn<Location, String>("region")

    @FXML
    private var country = TableColumn<Location, String>("country")

    @FXML
    private fun locationClickListener() {
        val ini = IniConfig()
        locationTable.rowFactory = Callback {
            val row = TableRow<Location>()
            row.setOnMouseClicked { event ->
                if (event.clickCount == 2 && (!row.isEmpty)) {
                    selectedLocation = row.item
                    try {
                        val loader = FXMLLoader(BuykoApplication::class.java.getResource("locationView.fxml"))
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

    private fun fetchLocations(): List<Location> {
        locationClickListener()
        val response = Unirest
            .get("http://localhost:8080/locations")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson().body.toString()

        val g = Gson()
        val locationListType = object : TypeToken<ArrayList<Location>>() {}.type
        return g.fromJson(response, locationListType)
    }


}