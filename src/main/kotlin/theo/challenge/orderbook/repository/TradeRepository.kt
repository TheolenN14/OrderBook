package theo.challenge.orderbook.repository

import theo.challenge.orderbook.model.Trade

class TradeRepository {

    private val trades: MutableMap<String, MutableList<Trade>> = mutableMapOf()

    fun addTrade(trade: Trade) {
        trades.computeIfAbsent(trade.currencyPair) { mutableListOf() }.add(trade)
    }

    fun getTradeHistory(currencyPair: String): List<Trade> =
        trades[currencyPair]?.toList() ?: emptyList()

    fun clear() {
        trades.clear()
    }
}