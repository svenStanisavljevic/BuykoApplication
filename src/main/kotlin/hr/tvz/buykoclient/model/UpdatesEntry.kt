package hr.tvz.buykoclient.model

import java.time.LocalDateTime

data class UpdatesEntry(
    val date: LocalDateTime,
    val content: String,
    val version: String,
)