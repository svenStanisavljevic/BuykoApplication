package hr.tvz.buykoclient

import com.jayway.jsonpath.JsonPath
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.model.Location
import hr.tvz.buykoclient.model.Order
import hr.tvz.buykoclient.model.User
import hr.tvz.buykoclient.util.JsonUtil
import hr.tvz.buykoclient.util.Logging
import hr.tvz.buykoclient.util.PdfUtil
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import javax.tools.ToolProvider

var userSession = User(
    3,
    "elonmusk",
    "Elon",
    "Musk",
    "elon@musk.com",
    "",
    "secretpassword5",
    2,
    "555-555-0005",
    547800.0,
    Location(3, "Croatia", "Varazdinska", "Sracinec")
)

class BuykoApplication : Application() {
    override fun start(stage: Stage) {

        val ini = IniConfig()

        val fxmlLoader = FXMLLoader(BuykoApplication::class.java.getResource("startup.fxml"))
        val scene = Scene(fxmlLoader.load(), ini.getWidth() ,ini.getHeight())
        stage.title = "Buyko"
        mainStage = stage
        stage.scene = scene
        stage.show()
    }

    companion object {
        lateinit var mainStage: Stage
        var initProcessState = 1
        var exchangeRate = fetchCurrencyRate()
        fun getStage(): Stage = mainStage
        fun fetchCurrencyRate(): Double {
            val ini = IniConfig()
            if (ini.getCur() == "eur") {
                return 1.0
            }
            val switchTo = ini.getCur()
            val response = Unirest
                .get("https://api.apilayer.com/exchangerates_data/convert?to=$switchTo&from=EUR&amount=1.0")
                .header("apikey", ini.getTranslateKey())
                .asJson().body.toString()
            val rate = JsonPath.parse(response).read<Double>("$.info.rate")
            return rate
        }
    }

}

fun main() {
    initProcess()
    val log = Logging()
    log.log("info", "debug")
    Application.launch(BuykoApplication::class.java)
}



private fun initProcess() {
    try {
        val compiler = ToolProvider.getSystemJavaCompiler()
        val sourceFilePath = "src/main/kotlin/hr/tvz/buykoclient/tcp/TcpClientProcess.java"

        val compileResult = compiler.run(null, null, null, sourceFilePath)
        if (compileResult == 0) {
            println("Compilation successful.")

            val processBuilder = ProcessBuilder(
                "java", "-cp", "src/main/kotlin", sourceFilePath)

            val process = processBuilder.start()

            val exitCode = process.waitFor()
            println("Process exited with code: $exitCode")
            BuykoApplication.initProcessState = exitCode

        } else {
            println("Compilation failed.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

