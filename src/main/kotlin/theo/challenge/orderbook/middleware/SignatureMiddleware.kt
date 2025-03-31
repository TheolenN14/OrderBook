package theo.challenge.orderbook.middleware

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

object SignatureMiddleware {

    private val log = LoggerFactory.getLogger(SignatureMiddleware::class.java)
    private const val SECRET = "super-secret-signing-key"

    fun handle(ctx: RoutingContext) {
        // In-line request validation (*Potential to isolate)
        val body = ctx.body().asJsonObject()
        val price = body.getDouble("price", null)
        val quantity = body.getDouble("quantity", null)
        val currencyPair = body.getString("currencyPair")
        val signature = ctx.request().getHeader("X-Signature")?.trim()

        when {
            price == null || quantity == null || currencyPair == null ->
                ctx.failJson(400, "Missing required fields")

            signature.isNullOrBlank() ->
                ctx.failJson(401, "Missing X-Signature header")

            // *Potential for hash algorithm
            !signature.equals("$price$currencyPair$quantity", ignoreCase = true) ->
                ctx.failJson(401, "Invalid signature")

            else -> ctx.next()
        }
    }

    //    private fun hmac(data: String): String {
    //        val hmacKey = SecretKeySpec(SECRET.toByteArray(), "HmacSHA256")
    //        val mac = Mac.getInstance("HmacSHA256").apply { init(hmacKey) }
    //        return Base64.getEncoder().encodeToString(mac.doFinal(data.toByteArray()))
    //    }

    private fun RoutingContext.failJson(status: Int, message: String) {
        log.warn("Signature failure: $message")
        response()
            .setStatusCode(status)
            .putHeader("Content-Type", "application/json")
            .end(JsonObject.mapFrom(mapOf("error" to message)).encode())
    }
}
