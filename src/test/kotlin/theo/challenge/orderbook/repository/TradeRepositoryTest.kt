package theo.challenge.orderbook.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import theo.challenge.orderbook.model.OrderType
import theo.challenge.orderbook.model.Trade
import java.time.Instant

class TradeRepositoryTest {

    private lateinit var repository: TradeRepository

    @BeforeEach
    fun setup() {
        repository = TradeRepository()
    }

    @Test
    fun `should add a trade correctly`() {
        val trade = sampleTrade()
        repository.addTrade(trade)

        val trades = repository.getTradeHistory("BTCZAR")
        assertEquals(1, trades.size)
        assertEquals(trade, trades.first())
    }

    @Test
    fun `should return empty list for unknown currency pair`() {
        val trades = repository.getTradeHistory("ETHZAR")
        assertTrue(trades.isEmpty())
    }

    @Test
    fun `should return copy not mutable reference`() {
        val trade = sampleTrade()
        repository.addTrade(trade)

        val retrieved = repository.getTradeHistory("BTCZAR")
        val modified = retrieved.toMutableList()
        modified.clear()

        val resultAfter = repository.getTradeHistory("BTCZAR")
        assertEquals(1, resultAfter.size)
    }

    private fun sampleTrade(): Trade = Trade(
        takerSide = OrderType.Buy,
        quantity = 0.5,
        price = 10000.0,
        quoteVolume = 5000.0,
        buyOrderId = "b1",
        sellOrderId = "s1",
        currencyPair = "BTCZAR",
        tradedAt = Instant.now()
    )
}
