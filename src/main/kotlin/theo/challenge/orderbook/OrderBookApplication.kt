package theo.challenge.orderbook

import theo.challenge.orderbook.api.OrderController
import theo.challenge.orderbook.repository.OrderRepository
import theo.challenge.orderbook.service.OrderBookService
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import theo.challenge.orderbook.middleware.TradeBroadcaster
import theo.challenge.orderbook.repository.TradeRepository

object App {
    private val log = LoggerFactory.getLogger(App::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val vertx = Vertx.vertx()
        val router = Router.router(vertx)

        val orderBookService = OrderBookService(OrderRepository(), TradeRepository(), TradeBroadcaster)
        val controller = OrderController(router, orderBookService)
        controller.setupRoutes()

        run {
            vertx.createHttpServer()
                .webSocketHandler { ws ->
                    if (ws.path() == "/ws/trades") {
                        log.info("WebSocket client connected: ${ws.remoteAddress()}")
                        TradeBroadcaster.register(ws)
                    } else {
                        ws.reject()
                    }
                }
                .requestHandler(router)
                .listen(8080).onSuccess { log.info("Server started on port 8080") }.onFailure {
                    log.error("Server failed to start", it)
                }
        }
    }
}
