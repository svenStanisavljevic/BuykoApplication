package hr.tvz.buykoclient.util

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Configuration.ConfigurationBuilder
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import java.io.File

class BlockListService() {

    fun getBlockedUsers(): List<Int> {
        val jsonFile = File("src/main/resources/block-list.json")
        val jsonString = jsonFile.readText()

        val configuration = Configuration.builder()
            .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
            .build()

        return JsonPath.using(configuration).parse(jsonString).read("\$.blockedUsers[*].id")
    }

}