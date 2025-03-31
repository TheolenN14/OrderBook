# VALR Backend Engineering Challenge — README

This is a Kotlin + Vert.x implementation of an in-memory order book backend service. It supports placing and matching limit orders, viewing open orders, and retrieving trade history.

### ⚙ How to Run
1. Ensure you have **JDK 21** or higher.
2. Clone the project:
```bash
git clone <your-repo-url>
cd orderbookservice
```
3. Import into IntelliJ IDEA:
   - Open IntelliJ
   - Select **File → Open**
   - Choose the root folder (e.g., `orderbook/`)
   - IntelliJ will detect and import it as a **Gradle Kotlin Project**
4. Run the application:
```bash
./gradlew run
```
5. Server starts on port **8080**.

### 📡 API Endpoints
| Method | Endpoint              | Description                      |
|--------|------------------------|----------------------------------|
| POST   | `/v1/orders/limit`     | Submit a limit order             |
| GET    | `/v1/orderbook`        | View open orders                 |
| GET    | `/v1/trades`           | View recent trade history        |

> All endpoints require an `Authorization: Bearer secret-token` header.
> POST endpoints require an `X-Signature: HMAC256`
> GET endpoints requires 'currencyPair' parameter ..?currencyPar=..

---

## ✅ UAT Checklist

### UAT Scenarios

| Scenario                              | Result         |
|---------------------------------------|----------------|
| Submit valid BUY order                | ✅ Pass         |
| Submit valid SELL order               | ✅ Pass         |
| Partial order match                   | ✅ Pass         |
| Full order match                      | ✅ Pass         |
| View order book with active orders    | ✅ Pass         |
| View trade history                    | ✅ Pass         |
| Auth failure (no token)               | ✅ Pass         |
| Auth success (valid token)            | ✅ Pass         |
