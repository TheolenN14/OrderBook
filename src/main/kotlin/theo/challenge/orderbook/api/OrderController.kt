package theo.challenge.orderbook.api


import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.slf4j.LoggerFactory
import theo.challenge.orderbook.service.OrderBookService
import theo.challenge.orderbook.mappers.OrderBookResponseMapper
import theo.challenge.orderbook.mappers.TradeResponseMapper
import theo.challenge.orderbook.model.Order
import theo.challenge.orderbook.model.OrderType
import theo.challenge.orderbook.middleware.AuthMiddleware
import theo.challenge.orderbook.middleware.SignatureMiddleware
import java.util.*

class OrderController(private val router: Router, private val orderBookService: OrderBookService) {

    private val log = LoggerFactory.getLogger(OrderController::class.java)
    private val allowedPairs = setOf("BTCZAR", "ETHZAR")

    fun setupRoutes() {
        router.route().handler(BodyHandler.create())

        // Logging middleware
        router.route().handler { ctx ->
            log.info("[${ctx.request().method()}] ${ctx.request().path()}")
            ctx.next()
        }

        // Global error handler
        router.route().failureHandler { ctx ->
            val error = ctx.failure()?.message ?: "Internal Server Error"
            log.error("Unhandled exception", ctx.failure())
            ctx.response()
                .setStatusCode(500).putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(mapOf("error" to error, "status" to 500)).encode())
        }

        //Middleware
        router.route("/v1/*").handler(AuthMiddleware::handle)

        /**
         * Handles POST /v1/orders/limit
         *
         * Submits a new limit order (buy/sell) to the order book.
         *
         * Body fields:
         * - price: Double
         * - quantity: Double
         * - side: "buy" or "sell"
         * - currencyPair: BTCZAR
         *
         * Headers:
         * - Authorization: Bearer {token}
         * - X-Signature: HMAC-SHA256
         *
         * Response:
         * 200 OK: { "orderId": "...", "status": "accepted" }
         * 400 Bad Request: { "error": "Invalid input" }
         * 401 Unauthorized: { "error": "Unauthorized" }
         */
        router.post("/v1/orders/limit")
            .handler(SignatureMiddleware::handle)
            .handler { ctx ->
            val body = ctx.body().asJsonObject()
            val price = body.getDouble("price")
            val quantity = body.getDouble("quantity")
            val type = OrderType.from(body.getString("side"))
            val currencyPair = body.getString("currencyPair")
            val id = UUID.randomUUID().toString()

            // *Potential to abstract handling and/or failures
            if (currencyPair !in allowedPairs) {
                ctx.response().setStatusCode(400).putHeader("Content-Type", "application/json")
                    .end(JsonObject(
                        mapOf(
                            "status" to 400,
                            "error" to "Currency pair not supported",
                            "message" to "That currency pair isn't available yet, but we’re always expanding!",
                            "allowedPairs" to allowedPairs
                        )).encode())
            }

            val order = Order(id, price, quantity, type, currencyPair)
            orderBookService.addOrder(order)

            log.info("Accepted $type order: $id for $quantity @ $price")
            ctx.response()
                .setStatusCode(201).putHeader("Content-Type", "application/json")
                .end(JsonObject(mapOf("orderId" to id, "status" to "accepted")).encode())
        }

        /**
         * Handles GET /v1/orderbook
         *
         * Returns the current order book (bids & asks) for a given currency pair.
         *
         * Query param:
         * - currencyPair: required, e.g. BTCZAR
         *
         * Response:
         * 200 OK: {
         *   "bids": [ ... ],
         *   "asks": [ ... ],
         *   "lastChange": "...",
         *   "sequenceNumber": ... }
         *
         * 400 Bad Request: { "error": "Missing currencyPair" }
         */
        router.get("/v1/orderbook").handler { ctx ->
            val currencyPair = ctx.queryParam("currencyPair").firstOrNull()
            if (currencyPair.isNullOrBlank()) {
                ctx.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject.mapFrom(mapOf("error" to "Missing currencyPair")).encode())
                return@handler
            }

            val orders = orderBookService.getOpenOrders(currencyPair)
            val response = OrderBookResponseMapper.mapToOrderResponse(orders)

            ctx.response()
                .setStatusCode(200).putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(response).encode())
        }

        /**
         * Handles GET /v1/trades
         *
         * Returns a list of recent trades for a given currency pair.
         *
         * Query param:
         * - currencyPair: required, e.g. BTCZAR
         *
         * Response:
         * 200 OK: { "trades": [ ... ] }
         * 400 Bad Request: { "error": "Missing currencyPair" }
         */
        router.get("/v1/trades").handler { ctx ->
            val currencyPair = ctx.queryParam("currencyPair").firstOrNull()
            if (currencyPair.isNullOrBlank()) {
                ctx.response()
                    .setStatusCode(400)
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject.mapFrom(mapOf("error" to "Missing currencyPair")).encode())
                return@handler
            }

            val trades = orderBookService.getTradeHistory(currencyPair)
            val structured = TradeResponseMapper.mapToTradeResponse(trades)

            ctx.response()
                .setStatusCode(200).putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(mapOf("trades" to structured)).encode())
        }
    }
}