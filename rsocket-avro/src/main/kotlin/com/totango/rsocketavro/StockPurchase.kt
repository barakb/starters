package com.totango.rsocketavro

import com.github.avrokotlin.avro4k.serializer.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class StockPurchase(
    val ticker: String = "",
    @Serializable(with = LocalDateSerializer::class) val purchaseDate: LocalDate = LocalDate.now(),
)