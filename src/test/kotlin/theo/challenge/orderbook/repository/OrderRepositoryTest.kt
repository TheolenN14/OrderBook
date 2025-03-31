package theo.challenge.orderbook.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import theo.challenge.orderbook.model.Order
import theo.challenge.orderbook.model.OrderType

class OrderRepositoryTest {

    private lateinit var repository: OrderRepository

    @BeforeEach
    fun setup() {
        repository = OrderRepository()
    }

    @Test
    fun `should add buy order correctly`() {
        val order = Order("1", 100.0, 0.5, OrderType.Buy, "BTCZAR")
        repository.addBuyOrder(order)

        val buyOrders = repository.getBuyOrders("BTCZAR")
        assertEquals(1, buyOrders.size)
        assertEquals(order, buyOrders.first())
    }

    @Test
    fun `should add sell order correctly`() {
        val order = Order("2", 200.0, 1.0, OrderType.Sell, "BTCZAR")
        repository.addSellOrder(order)

        val sellOrders = repository.getSellOrders("BTCZAR")
        assertEquals(1, sellOrders.size)
        assertEquals(order, sellOrders.first())
    }

    @Test
    fun `should remove buy order correctly`() {
        val order = Order("3", 150.0, 1.0, OrderType.Buy, "BTCZAR")
        repository.addBuyOrder(order)
        repository.removeBuyOrder(order)

        assertTrue(repository.getBuyOrders("BTCZAR").isEmpty())
    }

    @Test
    fun `should remove sell order correctly`() {
        val order = Order("4", 250.0, 2.0, OrderType.Sell, "BTCZAR")
        repository.addSellOrder(order)
        repository.removeSellOrder(order)

        assertTrue(repository.getSellOrders("BTCZAR").isEmpty())
    }

    @Test
    fun `should return all buy orders`() {
        val order1 = Order("5", 100.0, 0.5, OrderType.Buy, "BTCZAR")
        val order2 = Order("6", 100.0, 0.3, OrderType.Buy, "BTCZAR")
        repository.addBuyOrder(order1)
        repository.addBuyOrder(order2)

        val orders = repository.getBuyOrders("BTCZAR")
        assertEquals(2, orders.size)
        assertTrue(orders.contains(order1) && orders.contains(order2))
    }

    @Test
    fun `should return all sell orders`() {
        val order1 = Order("7", 300.0, 1.2, OrderType.Sell, "BTCZAR")
        val order2 = Order("8", 300.0, 0.4, OrderType.Sell, "BTCZAR")
        repository.addSellOrder(order1)
        repository.addSellOrder(order2)

        val orders = repository.getSellOrders("BTCZAR")
        assertEquals(2, orders.size)
        assertTrue(orders.contains(order1) && orders.contains(order2))
    }
}
