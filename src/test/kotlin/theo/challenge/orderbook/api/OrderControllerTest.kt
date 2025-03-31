package theo.challenge.orderbook.api

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import theo.challenge.orderbook.repository.OrderRepository
import theo.challenge.orderbook.repository.TradeRepository
import theo.challenge.orderbook.service.OrderBookService
import theo.challenge.orderbook.middleware.TradeBroadcaster
import org.junit.jupiter.api.Assertions.assertEquals

//Integration Style Tests
@ExtendWith(VertxExtension::class)
class OrderControllerTest {

    private lateinit var vertx: Vertx
    private lateinit var webClient: WebClient

    @BeforeEach
    fun setup() {
        vertx = Vertx.vertx()
        val router = Router.router(vertx)

        val orderBook = OrderBookService(OrderRepository(), TradeRepository(), TradeBroadcaster)
        val controller = OrderController(router, orderBook)
        controller.setupRoutes()

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8888)

        webClient = WebClient.create(
            vertx,
            WebClientOptions().setDefaultPort(8888).setDefaultHost("localhost")
        )
    }

    @Test
    fun `should reject missing auth token`(testContext: VertxTestContext) {
        webClient.get("/v1/orderbook")
            .send()
            .onComplete { ar ->
                testContext.verify {
                    assertEquals(401, ar.result().statusCode())
                    testContext.completeNow()
                }
            }
    }

    @Test
    fun `should reject missing currency pair on orderbook call`(testContext: VertxTestContext) {
        webClient.get("/v1/orderbook")
            .putHeader("Authorization", "Bearer secret-token")
            .send()
            .onComplete { ar ->
                testContext.verify {
                    assertEquals(400, ar.result().statusCode())
                    val error = ar.result().bodyAsJsonObject().getString("error")
                    assertEquals("Missing currencyPair", error)
                    testContext.completeNow()
                }
            }
    }

    @Test
    fun `should return trades for valid pair`(testContext: VertxTestContext) {
        webClient.get("/v1/trades?currencyPair=BTCZAR")
            .putHeader("Authorization", "Bearer secret-token")
            .send()
            .onComplete { ar ->
                testContext.verify {
                    assertEquals(200, ar.result().statusCode())
                    val trades = ar.result().bodyAsJsonObject().getJsonArray("trades")
                    assertEquals(0, trades.size())
                    testContext.completeNow()
                }
            }
    }

    @AfterEach
    fun teardown() {
        vertx.close()
    }
}
