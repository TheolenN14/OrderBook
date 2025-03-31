package theo.challenge.orderbook.mappers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import theo.challenge.orderbook.model.Order
import theo.challenge.orderbook.model.OrderType
import theo.challenge.orderbook.model.OpenOrders

class OrderBookResponseMapperTest {

    @Test
    fun `should group orders by price and return proper order book response`() {
        val orders = OpenOrders(
            buyOrders = listOf(
                Order("b1", 100.0, 0.5, OrderType.Buy, "BTCZAR"),
                Order("b2", 100.0, 0.3, OrderType.Buy, "BTCZAR"),
                Order("b3", 101.0, 0.2, OrderType.Buy, "BTCZAR")
            ),
            sellOrders = listOf(
                Order("s1", 105.0, 0.1, OrderType.Sell, "BTCZAR"),
                Order("s2", 105.0, 0.2, OrderType.Sell, "BTCZAR"),
                Order("s3", 106.0, 0.3, OrderType.Sell, "BTCZAR")
            )
        )

        val result = OrderBookResponseMapper.mapToOrderResponse(orders)

        assertEquals(2, result.bids.size)
        assertEquals("101.0", result.bids[0].price)
        assertEquals("0.2", result.bids[0].quantity)
        assertEquals(1, result.bids[0].orderCount)

        assertEquals("100.0", result.bids[1].price)
        assertEquals("0.8", result.bids[1].quantity)
        assertEquals(2, result.bids[1].orderCount)

        assertEquals(2, result.asks.size)
        assertEquals("105.0", result.asks[0].price)
        assertEquals(2, result.asks[0].orderCount)

        assertEquals("106.0", result.asks[1].price)
        assertEquals("0.3", result.asks[1].quantity)
        assertEquals(1, result.asks[1].orderCount)

        assertNotNull(result.lastChange)
        assertTrue(result.sequenceNumber > 0)
    }
}
