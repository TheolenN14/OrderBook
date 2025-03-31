package theo.challenge.orderbook.mappers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import theo.challenge.orderbook.model.OrderType
import theo.challenge.orderbook.model.Trade
import java.time.Instant

class TradeResponseMapperTest {

    @Test
    fun `should map single trade correctly`() {
        val trade = Trade(
            takerSide = OrderType.Buy,
            quantity = 0.5,
            price = 120.0,
            quoteVolume = 60.0,
            buyOrderId = "b123",
            sellOrderId = "s456",
            currencyPair = "BTCZAR",
            tradedAt = Instant.parse("2025-03-29T12:00:00Z"),
            sequenceId = 123456789L,
            id = "t789"
        )

        val result = TradeResponseMapper.mapToTradeResponse(listOf(trade))

        assertEquals(1, result.size)
        val response = result.first()
        assertEquals("BUY", response["takerSide"])
        assertEquals("0.5", response["quantity"])
        assertEquals("120.0", response["price"])
        assertEquals("BTCZAR", response["currencyPair"])
        assertEquals("60.0", response["quoteVolume"])
        assertEquals("t789", response["id"])
        assertEquals(123456789L, response["sequenceId"])
        assertEquals("2025-03-29T12:00:00Z", response["tradedAt"])
    }

    @Test
    fun `should map multiple trades and preserve order (latest first)`() {
        val trade1 = Trade(
            takerSide = OrderType.Buy,
            quantity = 0.1,
            price = 100.0,
            quoteVolume = 10.0,
            buyOrderId = "b1",
            sellOrderId = "s1",
            currencyPair = "BTCZAR",
            tradedAt = Instant.parse("2025-03-29T10:00:00Z"),
            sequenceId = 1L,
            id = "t1"
        )

        val trade2 = Trade(
            takerSide = OrderType.Sell,
            quantity = 0.2,
            price = 200.0,
            quoteVolume = 40.0,
            buyOrderId = "b2",
            sellOrderId = "s2",
            currencyPair = "BTCZAR",
            tradedAt = Instant.parse("2025-03-29T11:00:00Z"),
            sequenceId = 2L,
            id = "t2"
        )

        val result = TradeResponseMapper.mapToTradeResponse(listOf(trade1, trade2))

        assertEquals(2, result.size)
        assertEquals("t2", result[0]["id"]) // latest first
        assertEquals("t1", result[1]["id"])
    }
}
