package hr.tvz.buykoclient.controllers.other

import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField

class SettingsController {

    val ini = IniConfig()

    @Override
    fun initialize() {
        trasnslateFX()
        initFields()
    }

    @FXML
    private var widthLabel = Label()

    @FXML
    private var heightLabel = Label()

    @FXML
    private var languageLabel = Label()

    @FXML
    private var currencyLabel = Label()

    @FXML
    private var loggingLabel = Label()

    @FXML
    private var width = TextField()

    @FXML
    private var height = TextField()

    @FXML
    private var language = ChoiceBox<String>()

    @FXML
    private var currency = ChoiceBox<String>()

    @FXML
    private var logging = ChoiceBox<String>()

    @FXML
    private var button = Button()

    @FXML
    fun submit() {

        ini.editProperty("screen", "width", width.text)
        ini.editProperty("screen", "height", height.text)
        ini.editProperty("local", "lang", language.value)
        ini.editProperty("local", "cur", currency.value)
        ini.editProperty("logging", "level", logging.value)

        BuykoApplication.exchangeRate = BuykoApplication.fetchCurrencyRate()

        val parent: Parent = FXMLLoader.load(BuykoApplication::class.java.getResource("postSearch.fxml"))
        val scene = Scene(parent, ini.getWidth(), ini.getHeight())
        BuykoApplication.getStage().scene = scene

    }

    private fun initFields() {
        width.text = ini.getWidth().toString()
        height.text = ini.getHeight().toString()

        val languages = FXCollections.observableArrayList(listOf("hr", "en"))
        language.items = languages
        language.value = ini.getLang()

        val logLevels = FXCollections.observableArrayList(listOf("INFO", "DEBUG"))
        logging.items = logLevels
        logging.value = ini.getLogLevel()

        val currencies = FXCollections.observableArrayList(listOf("eur", "usd", "bam", "chf", "btc", "rsd"))
        currency.items = currencies
        currency.value = ini.getCur()
    }

    private fun trasnslateFX() {
        val translate = Translate()

        button.text = translate.readXML(28, ini.getLang())
        widthLabel.text = translate.readXML(29, ini.getLang())
        heightLabel.text = translate.readXML(30, ini.getLang())
        languageLabel.text = translate.readXML(31, ini.getLang())
        currencyLabel.text = translate.readXML(32, ini.getLang())
        loggingLabel.text = translate.readXML(33, ini.getLang())
    }
}