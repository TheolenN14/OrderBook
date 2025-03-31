package theo.challenge.orderbook.mappers

import theo.challenge.orderbook.model.*
import java.time.Instant

object OrderBookResponseMapper {

    fun mapToOrderResponse(orders: OpenOrders): OrderBookResponse {
        val asks = orders.sellOrders
            .filter { it.type is OrderType.Sell }
            .groupBy { it.price }
            .map { (price, grouped) ->
                OrderLevel(
                    side = OrderType.Sell.toString(),
                    price = price.toString(),
                    quantity = grouped.sumOf { it.quantity }.toString(),
                    currencyPair = grouped.first().currencyPair,
                    orderCount = grouped.size
                )
            }
            .sortedBy { it.price.toDouble() }

        val bids = orders.buyOrders
            .filter { it.type is OrderType.Buy }
            .groupBy { it.price }
            .map { (price, grouped) ->
                OrderLevel(
                    side = OrderType.Buy.toString(),
                    price = price.toString(),
                    quantity = grouped.sumOf { it.quantity }.toString(),
                    currencyPair = grouped.first().currencyPair,
                    orderCount = grouped.size
                )
            }
            .sortedByDescending { it.price.toDouble() }

        return OrderBookResponse(
            asks = asks,
            bids = bids,
            lastChange = Instant.now().toString(),
            sequenceNumber = System.currentTimeMillis()
        )
    }
}