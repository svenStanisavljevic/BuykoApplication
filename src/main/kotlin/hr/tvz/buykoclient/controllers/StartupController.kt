package hr.tvz.buykoclient.controllers

import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import kotlin.system.exitProcess

class StartupController {

    val ini = IniConfig()

    @Override
    fun initialize() {
        if (BuykoApplication.initProcessState == 1) {
            welcomeText.text = "Failed to download latest resources, please restart"
        } else if (BuykoApplication.initProcessState == 2) {
            welcomeText.text = "Failed to connect to server, please wait"
        }
        else {
            translateFX()
        }
    }

    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private lateinit var registerButton: Button

    @FXML
    private lateinit var loginButton: Button

    @FXML
    private fun onLogInButtonClick() {
        if (BuykoApplication.initProcessState != 0) {
            exitProcess(BuykoApplication.initProcessState)
        }
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("login.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    @FXML
    private fun onRegisterButtonClick() {
        if (BuykoApplication.initProcessState != 0) {
            exitProcess(BuykoApplication.initProcessState)
        }
        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("register.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene
    }

    fun translateFX() {
        val ini = IniConfig()
        val translate = Translate()
        loginButton.text = translate.readXML(0, ini.getLang())
        registerButton.text = translate.readXML(1, ini.getLang())
        welcomeText.text = translate.readXML(2, ini.getLang())
    }
}