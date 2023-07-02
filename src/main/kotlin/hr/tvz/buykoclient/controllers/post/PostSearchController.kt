package hr.tvz.buykoclient.controllers.post

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mashape.unirest.http.Unirest
import hr.tvz.buykoclient.BuykoApplication
import hr.tvz.buykoclient.config.IniConfig
import hr.tvz.buykoclient.config.Translate
import hr.tvz.buykoclient.model.Post
import hr.tvz.buykoclient.userSession
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import java.io.IOException


class PostSearchController {

    companion object Companion {
        var selectedPost = Post(0, "", "", 0.0, "", userSession)
    }

    private lateinit var posts: List<Post>
    private var selectedPosts: MutableList<Post> = mutableListOf()

    @Override
    fun initialize() {
        translateFX()
        posts = fetchPosts()
        selectedPosts.addAll(posts)
        displayPosts()
        val categories = mutableListOf("All categories")
        posts.forEach {
            it.adjustCurrency()
            if (!categories.contains(it.category)) {
                categories.add(it.category)
            }
        }
        val observableCategories = FXCollections.observableArrayList(categories)
        categoryFilter.items = observableCategories
        categoryFilter.value = "All categories"
        categoryFilter.valueProperty().addListener { _, oldValue, newValue ->
            if (newValue !== "All categories") {
                selectedPosts.clear()
                posts.forEach {
                    if (it.category == newValue) {
                        selectedPosts.add(it)
                    }
                }
            } else {
                selectedPosts.clear()
                selectedPosts.addAll(posts)
            }
            displayPosts()
        }
    }

    @FXML
    private lateinit var postsTable: TableView<Post>

    @FXML
    private var title = TableColumn<Post, String>("title")

    @FXML
    private var description = TableColumn<Post, String>("description")

    @FXML
    private var price = TableColumn<Post, Double>("price")

    @FXML
    private var category = TableColumn<Post, String>("category")

    @FXML
    private var categoryFilter = ChoiceBox<String>()

    @FXML
    private fun postClickListener() {
        val ini = IniConfig()
        postsTable.rowFactory = Callback {
            val row = TableRow<Post>()
            row.setOnMouseClicked { event ->
                if (event.clickCount == 2 && (!row.isEmpty)) {
                    selectedPost = row.item
                    try {
                        val loader = FXMLLoader(BuykoApplication::class.java.getResource("postView.fxml"))
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

    private fun translateFX() {
        val translate = Translate()
        val ini = IniConfig()
        title.text = translate.readXML(12, ini.getLang())
        description.text = translate.readXML(13, ini.getLang())
        price.text = translate.readXML(14, ini.getLang()) + " (${ini.getCur()})"
        category.text = translate.readXML(15, ini.getLang())
    }

    private fun fetchPosts(): List<Post> {
        postClickListener()
        val response = Unirest
            .get("http://localhost:8080/posts")
            .header("username", userSession.username)
            .header("password", userSession.password)
            .asJson().body.toString()
        val g = Gson()
        val postListType = object : TypeToken<ArrayList<Post>>() {}.type
        return g.fromJson(response, postListType)
    }

    private fun displayPosts() {
        val observablePosts = FXCollections.observableArrayList(selectedPosts)
        title.cellValueFactory = PropertyValueFactory("title")
        description.cellValueFactory = PropertyValueFactory("description")
        price.cellValueFactory = PropertyValueFactory("price")
        category.cellValueFactory = PropertyValueFactory("category")
        postsTable.items = FXCollections.observableArrayList()
        postsTable.items.addAll(observablePosts)
    }
}