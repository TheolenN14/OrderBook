package theo.challenge.orderbook.model

import java.time.Instant

data class OpenOrders(
    val buyOrders: List<Order>,
    val sellOrders: List<Order>,
    val fetchedAt: Instant = Instant.now()
)