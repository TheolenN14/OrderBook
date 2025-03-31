package theo.challenge.orderbook.service

import org.slf4j.LoggerFactory
import theo.challenge.orderbook.middleware.TradeBroadcaster
import theo.challenge.orderbook.model.OpenOrders
import theo.challenge.orderbook.model.Order
import theo.challenge.orderbook.model.OrderType
import theo.challenge.orderbook.model.Trade
import theo.challenge.orderbook.repository.OrderRepository
import theo.challenge.orderbook.repository.TradeRepository

class OrderBookService(
    private val repository: OrderRepository,
    private val tradeRepository: TradeRepository,
    private val tradeBroadcaster: TradeBroadcaster
) {
    private val log = LoggerFactory.getLogger(OrderBookService::class.java)


    fun addOrder(order: Order) {
        synchronized(this) {
            when (order.type) {
                is OrderType.Buy -> matchBuyOrder(order)
                is OrderType.Sell -> matchSellOrder(order)
            }
        }
    }

    private fun matchBuyOrder(buyOrder: Order) {
        val candidateSells = repository.getSellOrderMap(buyOrder.currencyPair)
            .headMap(buyOrder.price, true)

        val iterator = candidateSells.entries.iterator()

        while (iterator.hasNext() && buyOrder.quantity > 0.0) {
            val (price, sellList) = iterator.next()

            val orderIterator = sellList.iterator()
            while (orderIterator.hasNext() && buyOrder.quantity > 0.0) {
                val sell = orderIterator.next()

                val matchQty = minOf(buyOrder.quantity, sell.quantity)
                buyOrder.quantity -= matchQty
                sell.quantity -= matchQty

                logTrade(OrderType.Buy, matchQty, price, buyOrder.orderId, sell.orderId, buyOrder.currencyPair)

                if (sell.quantity == 0.0) {
                    orderIterator.remove()
                }
            }
            if (sellList.isEmpty()) iterator.remove()
        }
        if (buyOrder.quantity > 0.0) {
            repository.addBuyOrder(buyOrder)
        }
    }


    private fun matchSellOrder(sellOrder: Order) {
        val candidateBuys = repository.getBuyOrderMap(sellOrder.currencyPair)
            .tailMap(sellOrder.price, true)

        val iterator = candidateBuys.entries.iterator()

        while (iterator.hasNext() && sellOrder.quantity > 0.0) {
            val (price, buyList) = iterator.next()

            val orderIterator = buyList.iterator()
            while (orderIterator.hasNext() && sellOrder.quantity > 0.0) {
                val buy = orderIterator.next()

                val matchQty = minOf(sellOrder.quantity, buy.quantity)
                sellOrder.quantity -= matchQty
                buy.quantity -= matchQty

                logTrade(OrderType.Sell, matchQty, price, buy.orderId, sellOrder.orderId, sellOrder.currencyPair)

                if (buy.quantity == 0.0) {
                    orderIterator.remove()
                }
            }
            if (buyList.isEmpty()) iterator.remove()
        }
        if (sellOrder.quantity > 0.0) {
            repository.addSellOrder(sellOrder)
        }
    }

    private fun logTrade(
        takerSide: OrderType,
        quantity: Double,
        price: Double,
        buyOrderId: String,
        sellOrderId: String,
        currencyPair: String
    ) {
        val trade = Trade(
            takerSide = takerSide,
            quantity = quantity,
            price = price,
            quoteVolume = quantity * price,
            buyOrderId = buyOrderId,
            sellOrderId = sellOrderId,
            currencyPair = currencyPair,
        )
        log.info("TRADE: $takerSide $quantity @ $price")
        tradeRepository.addTrade(trade)
        tradeBroadcaster.broadcast(trade)
    }

    fun getOpenOrders(currencyPair: String): OpenOrders = OpenOrders(
        buyOrders = repository.getBuyOrders(currencyPair),
        sellOrders = repository.getSellOrders(currencyPair)
    )

    fun getTradeHistory(currencyPair: String): List<Trade> = tradeRepository.getTradeHistory(currencyPair)
}