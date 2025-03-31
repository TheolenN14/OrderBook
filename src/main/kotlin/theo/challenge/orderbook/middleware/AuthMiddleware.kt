package theo.challenge.orderbook.middleware

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

object AuthMiddleware {

    private const val VALID_TOKEN = "secret-token"

    fun handle(ctx: RoutingContext) {
        val header = ctx.request().getHeader("Authorization")
        val token = header?.removePrefix("Bearer ")?.trim()

        if (token != VALID_TOKEN) {
            ctx.response()
                .setStatusCode(401)
                .putHeader("Content-Type", "application/json")
                .end(
                    JsonObject(
                        mapOf(
                            "status" to 401,
                            "error" to "Unauthorized",
                            "message" to "You must provide a valid token."))
                        .encode())
        } else {
            ctx.next()
        }
    }

    //    JWT validation
    //
    //    private val jwtVerifier = JWT
    //        .require(Algorithm.HMAC256("super-secret-jwt-key"))
    //        .build()
    //
    //    private fun verifyJwt(token: String): DecodedJWT =
    //        jwtVerifier.verify(token)
}