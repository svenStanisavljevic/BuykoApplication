package hr.tvz.buykoclient.controllers.post

import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import hr.tvz.buykoclient.controllers.user.UserSearchController
import hr.tvz.buykoclient.model.Order
import hr.tvz.buykoclient.model.Post
import hr.tvz.buykoclient.userSession
import hr.tvz.buykoclient.util.JsonUtil
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import java.io.IOException

class PostViewController {

    var selectedPost = Post(0, "", "", 0.0, "", userSession)
    val ini = IniConfig()
    @Override
    fun initialize() {

        if (userSession.role !== 2) {
            edit.isVisible = false
            delete.isVisible = false
        }

        translateFX()
        selectedPost = PostSearchController.selectedPost

        title.text = selectedPost.title
        category.text = selectedPost.category
        price.text = selectedPost.price.toString()
        description.text = selectedPost.description
        ownerUsername.text = selectedPost.user.username

        back.setOnAction {
            try {
                val loader = FXMLLoader(BuykoApplication::class.java.getResource("postSearch.fxml"))
                val parent = loader.load<Parent>()
                val scene = Scene(parent, ini.getWidth(), ini.getHeight())
                BuykoApplication.getStage().scene = scene
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @FXML
    private var title = Label()

    @FXML
    private var category = Label()

    @FXML
    private var price = Label()

    @FXML
    private var description = Label()

    @FXML
    private var back = Button()

    @FXML
    private var edit = Button()

    @FXML
    private var delete = Button()

    @FXML
    private var cart = Button()

    @FXML
    private var ownerUsername = Label()

    @FXML
    fun openUser() {
        UserSearchController.selectedUser = selectedPost.user
        try {
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("profile.fxml"))
            val parent = loader.load<Parent>()
            val scene = Scene(parent, ini.getWidth(), ini.getHeight())
            BuykoApplication.getStage().scene = scene
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @FXML
    fun openEdit() {
        try {
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("postEdit.fxml"))
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
            .delete("http://localhost:8080/posts/${selectedPost.id}")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson()
        try {
            val loader = FXMLLoader(BuykoApplication::class.java.getResource("postSearch.fxml"))
            val parent = loader.load<Parent>()
            val scene = Scene(parent, ini.getWidth(), ini.getHeight())
            BuykoApplication.getStage().scene = scene
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @FXML
    fun cart() {
        val jsonUtil = JsonUtil()
        jsonUtil.createOrder(Order(
            JsonUtil.idCount,
            PostSearchController.selectedPost.id,
            PostSearchController.selectedPost.user.id,
            System.currentTimeMillis().toString(),
            "adress",
            "0-24",
            "payment method"
        ))
        jsonUtil.getAllOrders().forEach {
            println("${it.orderId}, ${it.postId}, ${it.sellerId}, ${it.timestamp}, ${it.deliveryAddress}, ${it.availableHours}, ${it.paymentType}")
        }
    }

    fun translateFX() {
        val ini = IniConfig()
        val translate = Translate()
        back.text = translate.readXML(8, ini.getLang())
    }


}