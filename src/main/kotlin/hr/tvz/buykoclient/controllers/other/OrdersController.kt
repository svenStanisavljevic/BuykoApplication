package hr.tvz.buykoclient.controllers.other

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.model.Order
import hr.tvz.buykoclient.model.Post
import hr.tvz.buykoclient.model.User
import hr.tvz.buykoclient.userSession
import hr.tvz.buykoclient.util.JsonUtil
import hr.tvz.buykoclient.util.PdfUtil
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.shape.Line
import kotlinx.serialization.json.buildJsonArray

class OrdersController {

    val ini = IniConfig()
    val jsonUtil = JsonUtil()
    val orders = jsonUtil.getAllOrders()


    @Override
    fun initialize() {
        populateData()
    }



    @FXML
    private var box1 = CheckBox()

    @FXML
    private var postName1 = Label()

    @FXML
    private var sellerName1 = Label()

    @FXML
    private var address1 = TextField()

    @FXML
    private var hours1 = TextField()

    @FXML
    private var payment1 = TextField()

    @FXML
    private var box2 = CheckBox()

    @FXML
    private var postName2 = Label()

    @FXML
    private var sellerName2 = Label()

    @FXML
    private var address2 = TextField()

    @FXML
    private var hours2 = TextField()

    @FXML
    private var payment2 = TextField()

    @FXML
    private var box3 = CheckBox()

    @FXML
    private var postName3 = Label()

    @FXML
    private var sellerName3 = Label()

    @FXML
    private var address3 = TextField()

    @FXML
    private var hours3 = TextField()

    @FXML
    private var payment3 = TextField()

    @FXML
    private var box4 = CheckBox()

    @FXML
    private var postName4 = Label()

    @FXML
    private var sellerName4 = Label()

    @FXML
    private var address4 = TextField()

    @FXML
    private var hours4 = TextField()

    @FXML
    private var payment4 = TextField()

    @FXML
    private var box5 = CheckBox()

    @FXML
    private var postName5 = Label()

    @FXML
    private var sellerName5 = Label()

    @FXML
    private var address5 = TextField()

    @FXML
    private var hours5 = TextField()

    @FXML
    private var payment5 = TextField()

    @FXML
    private var edit1 = Button()

    @FXML
    private var delete1 = Button()

    @FXML
    private var edit2 = Button()

    @FXML
    private var delete2 = Button()

    @FXML
    private var edit3 = Button()

    @FXML
    private var delete3 = Button()

    @FXML
    private var edit4 = Button()

    @FXML
    private var delete4 = Button()

    @FXML
    private var edit5 = Button()

    @FXML
    private var delete5 = Button()

    @FXML
    private var line1 = Line()

    @FXML
    private var line2 = Line()

    @FXML
    private var line3 = Line()

    @FXML
    private var line4 = Line()

    @FXML
    fun edit1() {
        editOrder(1, orders[0].orderId)
    }

    @FXML
    fun delete1() {
        deleteOrder(orders[0].orderId)
    }

    @FXML
    fun edit2() {
        editOrder(2, orders.get(1).orderId)
    }

    @FXML
    fun delete2() {
        deleteOrder(orders[1].orderId)
    }

    @FXML
    fun edit3() {
        editOrder(3, orders.get(2).orderId)
    }

    @FXML
    fun delete3() {
        deleteOrder(orders[2].orderId)
    }

    @FXML
    fun edit4() {
        editOrder(4, orders.get(3).orderId)
    }

    @FXML
    fun delete4() {
        deleteOrder(orders[3].orderId)
    }

    @FXML
    fun edit5() {
        editOrder(5, orders.get(4).orderId)
    }

    @FXML
    fun delete5() {
        deleteOrder(orders[4].orderId)
    }

    @FXML
    fun generate() {
        val pdfUtil = PdfUtil()
        val selectedOrders = mutableListOf<Order>()
        if (box1.isSelected) {
            selectedOrders.add(orders[0])
        }
        if (box2.isSelected) {
            selectedOrders.add(orders[1])
        }
        if (box3.isSelected) {
            selectedOrders.add(orders[2])
        }
        if (box4.isSelected) {
            selectedOrders.add(orders[3])
        }
        if (box5.isSelected) {
            selectedOrders.add(orders[4])
        }
        pdfUtil.generateOrder(selectedOrders)
    }

    private fun populateData() {
        if (orders.size > 5) {
            orders.subList(0, 5)
        }
        var counter = 0
        orders.forEach {
            val post = fetchPostById(it.postId)
            val user = fetchUserById(post.user.id)
            if (counter == 0) {
                postName1.text = post.title
                sellerName1.text = user.email
                address1.text = it.deliveryAddress
                hours1.text = it.availableHours
                payment1.text = it.paymentType
            }
            if (counter == 1) {
                postName2.text = post.title
                sellerName2.text = user.email
                address2.text = it.deliveryAddress
                hours2.text = it.availableHours
                payment2.text = it.paymentType
            }
            if (counter == 2) {
                postName3.text = post.title
                sellerName3.text = user.email
                address3.text = it.deliveryAddress
                hours3.text = it.availableHours
                payment3.text = it.paymentType
            }
            if (counter == 3) {
                postName4.text = post.title
                sellerName4.text = user.email
                address4.text = it.deliveryAddress
                hours4.text = it.availableHours
                payment4.text = it.paymentType
            }
            if (counter == 4) {
                postName5.text = post.title
                sellerName5.text = user.email
                address5.text = it.deliveryAddress
                hours5.text = it.availableHours
                payment5.text = it.paymentType
            }

            counter++
        }
        hideFields(orders.size)
    }

    private fun fetchPostById(id: Int): Post {
        val response = Unirest
            .get("http://localhost:8080/posts/${id}")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson().body.toString()
        val g = Gson()
        val postListType = object : TypeToken<Post>() {}.type
        return g.fromJson(response, postListType)
    }

    private fun fetchUserById(id: Int): User {
        val response = Unirest
            .get("http://localhost:8080/users/${id}")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson().body.toString()
        val g = Gson()
        val postListType = object : TypeToken<User>() {}.type
        return g.fromJson(response, postListType)
    }

    private fun editOrder(id: Int, orderId: Int) {
        if (id == 1) {
            jsonUtil.editOrder(orderId, address1.text, hours1.text, payment1.text)
        }
        if (id == 2) {
            jsonUtil.editOrder(orderId, address2.text, hours2.text, payment2.text)
        }
        if (id == 3) {
            jsonUtil.editOrder(orderId, address3.text, hours3.text, payment3.text)
        }
        if (id == 4) {
            jsonUtil.editOrder(orderId, address4.text, hours4.text, payment4.text)
        }
        if (id == 5) {
            jsonUtil.editOrder(orderId, address5.text, hours5.text, payment5.text)
        }
        populateData()
    }

    private fun deleteOrder(orderId: Int) {
        jsonUtil.deleteOrder(orderId)
        populateData()
    }

    private fun hideFields(count: Int) {
        if (count < 5) {
            postName5.isVisible = false
            sellerName5.isVisible = false
            address5.isVisible = false
            hours5.isVisible = false
            payment5.isVisible = false
            edit5.isVisible = false
            delete5.isVisible = false
            box5.isVisible = false
            line4.isVisible = false
        }
        if (count < 4) {
            postName4.isVisible = false
            sellerName4.isVisible = false
            address4.isVisible = false
            hours4.isVisible = false
            payment4.isVisible = false
            edit4.isVisible = false
            delete4.isVisible = false
            box4.isVisible = false
            line3.isVisible = false
        }
        if (count < 3) {
            postName3.isVisible = false
            sellerName3.isVisible = false
            address3.isVisible = false
            hours3.isVisible = false
            payment3.isVisible = false
            edit3.isVisible = false
            delete3.isVisible = false
            box3.isVisible = false
            line2.isVisible = false
        }
        if (count < 2) {
            postName2.isVisible = false
            sellerName2.isVisible = false
            address2.isVisible = false
            hours2.isVisible = false
            payment2.isVisible = false
            edit2.isVisible = false
            delete2.isVisible = false
            box2.isVisible = false
            line1.isVisible = false
        }
        if (count < 1) {
            postName1.isVisible = false
            sellerName1.isVisible = false
            address1.isVisible = false
            hours1.isVisible = false
            payment1.isVisible = false
            edit1.isVisible = false
            delete1.isVisible = false
            box1.isVisible = false
        }
    }
}