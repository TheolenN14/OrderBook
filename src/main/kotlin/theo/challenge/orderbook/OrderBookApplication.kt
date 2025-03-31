package theo.challenge.orderbook

import io.vertx.core.Vertx

object App {
    @JvmStatic
    fun main(args: Array<String>) {
        Vertx.vertx().deployVerticle(OrderBookVerticle())
    }
}