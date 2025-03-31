package theo.challenge.orderbook.repository

import theo.challenge.orderbook.model.Order
import java.util.*

class OrderRepository {

    private val buyOrders: MutableMap<String, NavigableMap<Double, MutableList<Order>>> = mutableMapOf()
    private val sellOrders: MutableMap<String, NavigableMap<Double, MutableList<Order>>> = mutableMapOf()

    fun addBuyOrder(order: Order) {
        val book = buyOrders.computeIfAbsent(order.currencyPair) {
            TreeMap(Collections.reverseOrder()) // Bid = Highest first
        }
        val list = book.computeIfAbsent(order.price) { mutableListOf() }
        list.removeIf { it.orderId == order.orderId }
        list.add(order)
    }

    fun addSellOrder(order: Order) {
        val book = sellOrders.computeIfAbsent(order.currencyPair) {
            TreeMap() // Ask = Lowest first
        }
        val list = book.computeIfAbsent(order.price) { mutableListOf() }
        list.removeIf { it.orderId == order.orderId }
        list.add(order)
    }

    @Deprecated("removeBuyOrders")
    fun removeBuyOrder(order: Order) {
        val book = buyOrders[order.currencyPair] ?: return
        val list = book[order.price] ?: return
        list.removeIf { it.orderId == order.orderId }
        if (list.isEmpty()) book.remove(order.price)
    }

    @Deprecated("removeSellOrders")
    fun removeSellOrder(order: Order) {
        val book = sellOrders[order.currencyPair] ?: return
        val list = book[order.price] ?: return
        list.removeIf { it.orderId == order.orderId }
        if (list.isEmpty()) book.remove(order.price)
    }

    // Match engine
    fun getBuyOrderMap(currencyPair: String): NavigableMap<Double, MutableList<Order>> =
        buyOrders.computeIfAbsent(currencyPair) {  TreeMap(Collections.reverseOrder())}

    fun getSellOrderMap(currencyPair: String): NavigableMap<Double, MutableList<Order>> =
        sellOrders.computeIfAbsent(currencyPair) { TreeMap()}

    // API views
    fun getBuyOrders(currencyPair: String): List<Order> =
        buyOrders[currencyPair]?.values?.flatten() ?: emptyList()

    fun getSellOrders(currencyPair: String): List<Order> =
        sellOrders[currencyPair]?.values?.flatten() ?: emptyList()

    // *Potential TestHelper
    fun clear() {
        buyOrders.clear()
        sellOrders.clear()
    }
}