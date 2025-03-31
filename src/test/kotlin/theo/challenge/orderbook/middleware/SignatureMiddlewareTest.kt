package theo.challenge.orderbook.middleware

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RequestBody
import io.vertx.ext.web.RoutingContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class SignatureMiddlewareTest {

    private lateinit var ctx: RoutingContext
    private lateinit var request: HttpServerRequest
    private lateinit var response: HttpServerResponse
    private lateinit var requestBody: RequestBody

    @BeforeEach
    fun setup() {
        ctx = mock(RoutingContext::class.java)
        request = mock(HttpServerRequest::class.java)
        response = mock(HttpServerResponse::class.java)
        requestBody = mock(RequestBody::class.java)

        `when`(ctx.request()).thenReturn(request)
        `when`(ctx.response()).thenReturn(response)
        `when`(ctx.body()).thenReturn(requestBody)

        `when`(response.setStatusCode(any())).thenReturn(response)
        `when`(response.putHeader(any<String>(), any<String>())).thenReturn(response)
        `when`(response.end(any<String>())).thenReturn(mock())
    }

    @Test
    fun `should allow valid signature`() {
        val body = JsonObject()
            .put("price", 123.0)
            .put("quantity", 0.5)
            .put("currencyPair", "BTCZAR")
        val signature = "123.0BTCZAR0.5"

        `when`(request.getHeader("X-Signature")).thenReturn(signature)
        `when`(requestBody.asJsonObject()).thenReturn(body)

        SignatureMiddleware.handle(ctx)

        verify(ctx).next()
    }

    @Test
    fun `should fail if signature missing`() {
        val body = JsonObject()
            .put("price", 123.0)
            .put("quantity", 0.5)
            .put("currencyPair", "BTCZAR")

        `when`(request.getHeader("X-Signature")).thenReturn(null)
        `when`(requestBody.asJsonObject()).thenReturn(body)

        SignatureMiddleware.handle(ctx)

        verify(response).setStatusCode(401)
        verify(response).end(org.mockito.kotlin.argThat<String> { payload ->
            payload.contains("Missing X-Signature")
        })
    }

    @Test
    fun `should fail if signature is wrong`() {
        val body = JsonObject()
            .put("price", 123.0)
            .put("quantity", 0.5)
            .put("currencyPair", "BTCZAR")

        `when`(request.getHeader("X-Signature")).thenReturn("WRONG")
        `when`(requestBody.asJsonObject()).thenReturn(body)

        SignatureMiddleware.handle(ctx)

        verify(response).setStatusCode(401)
        verify(response).end(org.mockito.kotlin.argThat<String> { payload ->
            payload.contains("Invalid signature")
        })
    }

    @Test
    fun `should fail if required fields missing`() {
        val body = JsonObject() // missing everything

        `when`(request.getHeader("X-Signature")).thenReturn("anything")
        `when`(requestBody.asJsonObject()).thenReturn(body)

        SignatureMiddleware.handle(ctx)

        verify(response).setStatusCode(400)
        verify(response).end(org.mockito.kotlin.argThat<String> { payload ->
            payload.contains("Missing required fields")
        })
    }
}
