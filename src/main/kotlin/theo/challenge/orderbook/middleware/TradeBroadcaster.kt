package theo.challenge.orderbook.middleware

import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonObject
import theo.challenge.orderbook.model.Trade
import java.util.concurrent.CopyOnWriteArrayList

object TradeBroadcaster {

    private val clients = CopyOnWriteArrayList<ServerWebSocket>()

    fun register(socket: ServerWebSocket) {
        clients.add(socket)
        socket.closeHandler { clients.remove(socket) }
    }

    fun broadcast(trade: Trade) {
        val message = JsonObject.mapFrom(trade).encode()
        clients.forEach { client ->
            if (client.isClosed.not()) {
                client.writeTextMessage(message)
            }
        }
    }
}