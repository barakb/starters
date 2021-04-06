package com.totango.rsocketavro.model

import java.util.*


enum class TicketStatus {
    TICKET_PENDING, TICKET_ISSUED, TICKET_CANCELLED
}

data class TicketRequest(
    val requestId: UUID,
    var status: TicketStatus = TicketStatus.TICKET_PENDING
)

data class MovieScene(
    private val sceneId: Int = 0,
    val sceneDescription: String? = null
)