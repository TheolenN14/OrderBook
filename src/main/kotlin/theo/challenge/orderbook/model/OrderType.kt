package theo.challenge.orderbook.model


sealed class OrderType(private val label: String) {
    object Buy : OrderType("BUY")
    object Sell : OrderType("SELL")

    companion object {
        fun from(input: String): OrderType = when (input.lowercase()) {
            "buy" -> Buy
            "sell" -> Sell
            else -> throw IllegalArgumentException("Invalid side: $input")
        }
    }

    override fun toString(): String = label
}