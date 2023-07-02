package hr.tvz.buykoclient.config

import org.ini4j.Ini
import java.io.File

class IniConfig {

    private val iniConfFile = File("src/main/resources/configuration.ini")
    val iniConf = Ini(iniConfFile)

    fun getHeight(): Double {
        return iniConf["screen", "height", Double::class.java]
    }

    fun getWidth(): Double {
        return iniConf["screen", "width", Double::class.java]
    }

    fun getLang(): String {
        return iniConf["local", "lang", String::class.java]
    }

    fun getCur(): String {
        return iniConf["local", "cur", String::class.java]
    }

    fun getTranslateKey(): String {
        return iniConf["api", "translate", String::class.java]
    }

    fun getLogLevel(): String {
        return iniConf["logging", "level", String::class.java]
    }

    fun editProperty(root: String, prop: String, value: String) {
        iniConf[root]?.set(prop, value)
        iniConf.store()
    }
}