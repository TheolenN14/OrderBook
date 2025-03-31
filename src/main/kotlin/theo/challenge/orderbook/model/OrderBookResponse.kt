package theo.challenge.orderbook.model

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderBookResponse(
    @JsonProperty("Asks")
    val asks: List<OrderLevel>,

    @JsonProperty("Bids")
    val bids: List<OrderLevel>,

    @JsonProperty("lastChange")
    val lastChange: String,

    @JsonProperty("SequenceNumber")
    val sequenceNumber: Long
)