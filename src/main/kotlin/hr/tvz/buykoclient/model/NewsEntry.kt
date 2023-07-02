package hr.tvz.buykoclient.model

import java.time.LocalDateTime

data class NewsEntry (
    val date: LocalDateTime,
    val content: String,
    val username: String,
)