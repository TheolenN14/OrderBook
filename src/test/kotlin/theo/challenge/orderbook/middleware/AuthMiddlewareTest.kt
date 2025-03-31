package theo.challenge.orderbook.middleware

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class AuthMiddlewareTest {

    private lateinit var ctx: RoutingContext
    private lateinit var request: HttpServerRequest
    private lateinit var response: HttpServerResponse

    @BeforeEach
    fun setup() {
        ctx = mock()
        request = mock()
        response = mock()

        whenever(ctx.request()).thenReturn(request)
        whenever(ctx.response()).thenReturn(response)

        whenever(response.setStatusCode(any())).thenReturn(response)
        whenever(response.putHeader(any<String>(), any<String>())).thenReturn(response)
        whenever(response.end(any<String>())).thenReturn(mock()) // simulate Future<Void>
    }

    @Test
    fun `should reject request with invalid token`() {
        whenever(request.getHeader("Authorization")).thenReturn("Bearer wrong-token")

        AuthMiddleware.handle(ctx)

        verify(ctx, never()).next()
        verify(response).setStatusCode(401)
        verify(response).putHeader(eq("Content-Type"), eq("application/json"))
        verify(response).end(argThat<String> { payload ->
            payload.contains("Unauthorized")
        })
    }

    @Test
    fun `should accept request with valid token`() {
        whenever(request.getHeader("Authorization")).thenReturn("Bearer secret-token")

        AuthMiddleware.handle(ctx)

        verify(ctx).next()
        verify(response, never()).setStatusCode(any())
        verify(response, never()).end(any<String>())
    }
}
