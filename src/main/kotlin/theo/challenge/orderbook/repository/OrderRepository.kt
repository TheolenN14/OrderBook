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
        book.computeIfAbsent(order.price) { mutableListOf() }.add(order)
    }

    fun addSellOrder(order: Order) {
        val book = sellOrders.computeIfAbsent(order.currencyPair) {
            TreeMap() // Ask = Lowest first
        }
        book.computeIfAbsent(order.price) { mutableListOf() }.add(order)
    }

    fun removeBuyOrder(order: Order) {
        val book = buyOrders[order.currencyPair] ?: return
        book[order.price]?.remove(order)
        if (book[order.price]?.isEmpty() == true) book.remove(order.price)
    }

    fun removeSellOrder(order: Order) {
        val book = sellOrders[order.currencyPair] ?: return
        book[order.price]?.remove(order)
        if (book[order.price]?.isEmpty() == true) book.remove(order.price)
    }

    // Match engine
    fun getBuyOrderMap(currencyPair: String): NavigableMap<Double, MutableList<Order>> =
        buyOrders[currencyPair] ?: TreeMap(Collections.reverseOrder())

    fun getSellOrderMap(currencyPair: String): NavigableMap<Double, MutableList<Order>> =
        sellOrders[currencyPair] ?: TreeMap()

    // API views
    fun getBuyOrders(currencyPair: String): List<Order> =
        buyOrders[currencyPair]?.values?.flatten() ?: emptyList()

    fun getSellOrders(currencyPair: String): List<Order> =
        sellOrders[currencyPair]?.values?.flatten() ?: emptyList()
}