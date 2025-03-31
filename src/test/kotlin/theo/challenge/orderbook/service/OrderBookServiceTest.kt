package theo.challenge.orderbook.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import theo.challenge.orderbook.middleware.TradeBroadcaster
import theo.challenge.orderbook.model.Order
import theo.challenge.orderbook.model.OrderType
import theo.challenge.orderbook.repository.OrderRepository
import theo.challenge.orderbook.repository.TradeRepository

class OrderBookServiceTest {

    private val broadcaster = mock<TradeBroadcaster>()
    private lateinit var orderBook: OrderBookService
    private lateinit var orderRepo: OrderRepository
    private lateinit var tradeRepo: TradeRepository

    @BeforeEach
    fun setup() {
        orderRepo = OrderRepository()
        orderRepo.clear()
        tradeRepo = TradeRepository()
        tradeRepo.clear()
        orderBook = OrderBookService(orderRepo, tradeRepo, broadcaster)
    }

    @Test
    fun `buy order without match should go into book`() {
        val order = Order("b1", 100.0, 1.0, OrderType.Buy, "BTCZAR")
        orderBook.addOrder(order)

        val buys = orderRepo.getBuyOrders("BTCZAR")
        assertEquals(1, buys.size)
        assertEquals(order.orderId, buys.first().orderId)
    }

    @Test
    fun `sell order without match should go into book`() {
        val order = Order("s1", 150.0, 1.5, OrderType.Sell, "BTCZAR")
        orderBook.addOrder(order)

        val sells = orderRepo.getSellOrders("BTCZAR")
        assertEquals(1, sells.size)
        assertEquals(order.orderId, sells.first().orderId)
    }

    @Test
    fun `should match a buy order with a sell`() {
        val sell = Order("s1", 100.0, 1.0, OrderType.Sell, "BTCZAR")
        orderBook.addOrder(sell)

        val buy = Order("b1", 120.0, 1.0, OrderType.Buy, "BTCZAR")
        orderBook.addOrder(buy)

        val buys = orderRepo.getBuyOrders("BTCZAR")
        val sells = orderRepo.getSellOrders("BTCZAR")
        val trades = tradeRepo.getTradeHistory("BTCZAR")

        assertTrue(buys.isEmpty())
        assertTrue(sells.isEmpty())
        assertEquals(1, trades.size)

        val trade = trades.first()
        assertEquals(1.0, trade.quantity)
        assertEquals(100.0, trade.price)
        assertEquals("BTCZAR", trade.currencyPair)

        verify(broadcaster).broadcast(any())
    }

    @Test
    fun `should match a sell order with a buy`() {
        val buy = Order("b1", 120.0, 1.0, OrderType.Buy, "BTCZAR")
        orderBook.addOrder(buy)

        val sell = Order("s1", 100.0, 1.0, OrderType.Sell, "BTCZAR")
        orderBook.addOrder(sell)

        val buys = orderRepo.getBuyOrders("BTCZAR")
        val sells = orderRepo.getSellOrders("BTCZAR")
        val trades = tradeRepo.getTradeHistory("BTCZAR")

        assertTrue(buys.isEmpty())
        assertTrue(sells.isEmpty())
        assertEquals(1, trades.size)

        val trade = trades.first()
        assertEquals(1.0, trade.quantity)
        assertEquals(120.0, trade.price)
        assertEquals("BTCZAR", trade.currencyPair)

        verify(broadcaster).broadcast(any())
    }

    @Test
    fun `should handle partial match where buy remains`() {
        val sell = Order("s1", 100.0, 0.6, OrderType.Sell, "BTCZAR")
        orderBook.addOrder(sell)

        val buy = Order("b1", 120.0, 1.0, OrderType.Buy, "BTCZAR")
        orderBook.addOrder(buy)

        val buys = orderRepo.getBuyOrders("BTCZAR")
        assertEquals(1, buys.size)
        assertEquals(0.4, buys.first().quantity)

        val sells = orderRepo.getSellOrders("BTCZAR")
        assertEquals(0, sells.size)

        verify(broadcaster).broadcast(any())
    }

    @Test
    fun `should handle partial match where sell remains`() {
        val buy = Order("b1", 120.0, 0.3, OrderType.Buy, "BTCZAR")
        orderBook.addOrder(buy)

        val sell = Order("s1", 100.0, 0.7, OrderType.Sell, "BTCZAR")
        orderBook.addOrder(sell)

        val sells = orderRepo.getSellOrders("BTCZAR")
        assertEquals(1, sells.size)
        assertEquals(0.4, sells.first().quantity, 0.000001)

        val buys = orderRepo.getBuyOrders("BTCZAR")
        assertEquals(0, buys.size)

        verify(broadcaster).broadcast(any())
    }

    @Test
    fun `should log multiple trades if many matches`() {
        val sell1 = Order("s1", 100.0, 0.5, OrderType.Sell, "BTCZAR")
        val sell2 = Order("s2", 99.0, 0.5, OrderType.Sell, "BTCZAR")
        orderBook.addOrder(sell1)
        orderBook.addOrder(sell2)

        val buy = Order("b1", 120.0, 1.0, OrderType.Buy, "BTCZAR")
        orderBook.addOrder(buy)

        val trades = tradeRepo.getTradeHistory("BTCZAR")
        assertEquals(2, trades.size)
        assertEquals(1.0, trades.sumOf { it.quantity })

        verify(broadcaster, times(2)).broadcast(any())
    }

    @Test
    fun `should broadcast trade when match occurs`() {
        val sell = Order("s1", 100.0, 1.0, OrderType.Sell, "BTCZAR")
        orderBook.addOrder(sell)

        val buy = Order("b1", 120.0, 1.0, OrderType.Buy, "BTCZAR")
        orderBook.addOrder(buy)

        verify(broadcaster).broadcast(check {
            assertEquals("BTCZAR", it.currencyPair)
            assertEquals(100.0, it.price)
            assertEquals(1.0, it.quantity)
        })
    }

    @Test
    fun `should match two buys to fulfill a sell, with buy remainder`() {
        val buy1 = Order("b1", 110.0, 0.5, OrderType.Buy, "BTCZAR")
        val buy2 = Order("b2", 105.0, 2.0, OrderType.Buy, "BTCZAR")
        orderBook.addOrder(buy1)
        orderBook.addOrder(buy2)

        val sell = Order("s1", 100.0, 1.5, OrderType.Sell, "BTCZAR")
        orderBook.addOrder(sell)

        val trades = tradeRepo.getTradeHistory("BTCZAR")
        assertEquals(2, trades.size)
        assertEquals(0.5 + 1.0, trades.sumOf { it.quantity })

        val buys = orderRepo.getBuyOrders("BTCZAR")
        assertEquals(1, buys.size)
        assertEquals("b2", buys.first().orderId)
        assertEquals(1.0, buys.first().quantity)

        val sells = orderRepo.getSellOrders("BTCZAR")
        assertTrue(sells.isEmpty())

        verify(broadcaster, times(2)).broadcast(any())
    }
}
