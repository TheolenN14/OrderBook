package theo.challenge.orderbook.mappers

import theo.challenge.orderbook.model.Trade

object TradeResponseMapper {

    fun mapToTradeResponse(trades: List<Trade>): List<Map<String, Any>> {
        return trades.map { trade ->
            mapOf(
                "id" to trade.id,
                "price" to trade.price.toString(),
                "quantity" to trade.quantity.toString(),
                "quoteVolume" to trade.quoteVolume.toString(),
                "currencyPair" to trade.currencyPair,
                "tradedAt" to trade.tradedAt.toString(),
                "takerSide" to trade.takerSide.toString(),
                "sequenceId" to trade.sequenceId,
                "buyOrderId" to trade.buyOrderId,
                "sellOrderId" to trade.sellOrderId
            )
        }
    }
}
