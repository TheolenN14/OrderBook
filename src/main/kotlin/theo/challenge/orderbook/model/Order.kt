package theo.challenge.orderbook.model

data class Order(
    val orderId: String,
    val price: Double,
    var quantity: Double,
    val type: OrderType,
    val currencyPair: String
){
    override fun equals(other: Any?): Boolean {
        return other is Order && other.orderId == this.orderId
    }

    override fun hashCode(): Int {
        return orderId.hashCode()
    }
}