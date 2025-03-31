package theo.challenge.orderbook.model

import java.time.Instant
import java.util.UUID


data class Trade(
    val id: String = UUID.randomUUID().toString(),
    val price: Double,
    val quantity: Double,
    val quoteVolume: Double = price * quantity,
    val currencyPair: String, // *Potential for validation via type
    val tradedAt: Instant = Instant.now(),
    val takerSide: OrderType,
    val buyOrderId: String,
    val sellOrderId: String,
    val sequenceId: Long = System.currentTimeMillis()
)