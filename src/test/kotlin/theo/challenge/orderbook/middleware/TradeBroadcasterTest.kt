package theo.challenge.orderbook.middleware

import io.vertx.core.http.ServerWebSocket
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

import theo.challenge.orderbook.model.OrderType
import theo.challenge.orderbook.model.Trade
import java.time.Instant

class TradeBroadcasterTest {

    private lateinit var socket1: ServerWebSocket
    private lateinit var socket2: ServerWebSocket

    @BeforeEach
    fun setup() {
        socket1 = mock()
        socket2 = mock()

        whenever(socket1.isClosed).thenReturn(false)
        whenever(socket2.isClosed).thenReturn(false)

        // Register mock sockets
        TradeBroadcaster.register(socket1)
        TradeBroadcaster.register(socket2)
    }

    @AfterEach
    fun tearDown() {
        val socketField = TradeBroadcaster::class.java.getDeclaredField("clients")
        socketField.isAccessible = true
        val list = socketField.get(TradeBroadcaster) as MutableList<*>
        list.clear()
    }

    @Test
    fun `should broadcast trade to all connected sockets`() {
        val trade = sampleTrade()
        val captor = argumentCaptor<String>()
        TradeBroadcaster.broadcast(trade)

        verify(socket1).writeTextMessage(captor.capture())
        assertTrue(captor.firstValue.contains("BTCZAR"))
    }

    @Test
    fun `should skip closed sockets`() {
        whenever(socket1.isClosed).thenReturn(true)

        TradeBroadcaster.broadcast(sampleTrade())

        verify(socket1, never()).writeTextMessage(any())
        verify(socket2).writeTextMessage(any())
    }

    private fun sampleTrade() = Trade(
        takerSide = OrderType.Buy,
        quantity = 0.25,
        price = 15600.0,
        quoteVolume = 3900.0,
        buyOrderId = "b1",
        sellOrderId = "s1",
        currencyPair = "BTCZAR",
        tradedAt = Instant.now()
    )
}
