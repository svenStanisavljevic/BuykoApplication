package hr.tvz.buykoclient.util

import javafx.scene.image.Image
import java.nio.charset.Charset
import java.util.Base64

class ProfilePhotoUtil {

    fun encodeImage(image: Image): String {
        return Base64.getEncoder().encode(image.toString().toByteArray()).toString()
    }

    fun decodeImage(string: String): Image {
        return Image(Base64.getDecoder().decode(string).toString())
    }


}