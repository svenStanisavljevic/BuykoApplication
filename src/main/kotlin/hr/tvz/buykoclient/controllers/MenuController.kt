package hr.tvz.buykoclient.controllers

import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem


class MenuController {

    val ini = IniConfig()

    @FXML
    private lateinit var menuBar: MenuBar

    @FXML
    private var posts = Menu("Posts")

    @FXML
    private var users = Menu("Users")

    @FXML
    private var other = Menu("Other")

    @FXML
    private var locations = Menu("Locations")

    @FXML
    private var postCreate = MenuItem("create")

    @FXML
    private var postSearch = MenuItem("search")

    @FXML
    private var userSearch = MenuItem("search")

    @FXML
    private var locationCreate = MenuItem("create")

    @FXML
    private var locationSearch = MenuItem("search")

    @FXML
    private var newsAndUpdates = MenuItem("news and updates")

    @FXML
    private var profilePicture = MenuItem("profile picture")

    @FXML
    private var settings = MenuItem("settings")

    @FXML
    private var orders = MenuItem("orders")

    @Override
    fun initialize() {
        translateFX()

    }

    @FXML
    private fun handleUserSearch(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("userSearch.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handlePostsSearch(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("postSearch.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handleLocationSearch(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("locationSearch.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handleUserAdmin(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("userAdmin.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handlePostAdmin(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("postAdmin.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handlePostCreate(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("postCreate.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handleLocationCreate(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("locationCreate.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handleNewsUpdates(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("newsAndUpdates.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handleProfilePicture(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("profilePicture.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handleSettings(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("settings.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun handleOrders(event: ActionEvent) {
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("orders.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    private fun translateFX() {
        val translate = Translate()

        users.text = translate.readXML(9, ini.getLang())
        posts.text = translate.readXML(10, ini.getLang())
        other.text = translate.readXML(11, ini.getLang())
        locations.text = translate.readXML(19, ini.getLang())

        postCreate.text = translate.readXML(20, ini.getLang())
        locationCreate.text = translate.readXML(20, ini.getLang())

        postSearch.text = translate.readXML(21, ini.getLang())
        userSearch.text = translate.readXML(21, ini.getLang())
        locationSearch.text = translate.readXML(21, ini.getLang())

        newsAndUpdates.text = translate.readXML(22, ini.getLang())
        profilePicture.text = translate.readXML(23, ini.getLang())
        settings.text = translate.readXML(24, ini.getLang())
        orders.text = translate.readXML(34, ini.getLang())
    }

}
