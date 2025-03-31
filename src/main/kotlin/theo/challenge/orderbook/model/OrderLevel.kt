package theo.challenge.orderbook.model

data class OrderLevel(
    val side: String,
    val quantity: String,
    val price: String,
    val currencyPair: String,
    val orderCount: Int
)