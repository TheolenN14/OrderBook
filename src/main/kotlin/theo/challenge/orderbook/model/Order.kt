package theo.challenge.orderbook.model

data class Order(
    val orderId: String,
    val price: Double,
    var quantity: Double,
    val type: OrderType,
    val currencyPair: String
)