package hr.tvz.buykoclient.util

import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Logging {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BuykoApplication::class.java)
    }


    val ini = IniConfig()
    fun log(messageInfo: String, messageDebug: String) {
        if (ini.getLogLevel().toLowerCase().equals("info")) {
            logger.info(messageInfo)
        }
        if (ini.getLogLevel().toLowerCase().equals("debug")) {
            logger.debug(messageDebug)
        }
    }
}