package hr.tvz.buykoclient.controllers.other

import hr.tvz.buykoclient.model.NewsEntry
import hr.tvz.buykoclient.model.UpdatesEntry
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
class NewsUpdatesController {

    @Override
    fun initialize() {
        val news = deserializeFromBin("src/main/resources/news.bin") as List<NewsEntry>
        val updates = deserializeFromBin("src/main/resources/updates.bin") as List<UpdatesEntry>

        val observableNews = FXCollections.observableArrayList(news)
        nDate.cellValueFactory = PropertyValueFactory("date")
        nContent.cellValueFactory = PropertyValueFactory("content")
        nUsername.cellValueFactory = PropertyValueFactory("username")
        nTable.items.addAll(observableNews)

        val observableUpdates = FXCollections.observableArrayList(updates)
        uDate.cellValueFactory = PropertyValueFactory("date")
        uContent.cellValueFactory = PropertyValueFactory("content")
        uVersion.cellValueFactory = PropertyValueFactory("version")
        uTable.items.addAll(observableUpdates)

    }

    @FXML
    private var nTable = TableView<NewsEntry>()

    @FXML
    private var nDate = TableColumn<NewsEntry, String>("date")

    @FXML
    private var nContent = TableColumn<NewsEntry, String>("content")

    @FXML
    private var nUsername = TableColumn<NewsEntry, String>("username")

    @FXML
    private var uTable = TableView<UpdatesEntry>()

    @FXML
    private var uDate = TableColumn<UpdatesEntry, String>("date")

    @FXML
    private var uContent = TableColumn<UpdatesEntry, String>("content")

    @FXML
    private var uVersion = TableColumn<UpdatesEntry, String>("version")


    private fun deserializeFromBin(filePath: String): List<Any> {
        val file = File(filePath)
        val fis = FileInputStream(file)
        val dis = DataInputStream(fis)

        val result = mutableListOf<Any>()

        val itemCount = dis.readInt()
        repeat(itemCount) {
            val marker = dis.readByte()
            when (marker) {
                0.toByte() -> {
                    val date = LocalDateTime.ofEpochSecond(dis.readLong(), 0, ZoneOffset.UTC)
                    val content = dis.readUTF()
                    val username = dis.readUTF()
                    val entry = NewsEntry(date, content, username)
                    result.add(entry)
                }
                1.toByte() -> {
                    val date = LocalDateTime.ofEpochSecond(dis.readLong(), 0, ZoneOffset.UTC)
                    val content = dis.readUTF()
                    val version = dis.readUTF()
                    val entry = UpdatesEntry(date, content, version)
                    result.add(entry)
                }
            }
        }

        dis.close()
        fis.close()

        return result
    }

}