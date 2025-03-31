# VALR Backend Engineering Challenge — README

This is a Kotlin + Vert.x implementation of an in-memory order book backend service. It supports placing and matching limit orders, viewing open orders, and retrieving trade history.

### ⚙ How to Run
1. Ensure you have **JDK 17** or higher.
2. Clone the project:
```bash
git clone <valr-assessment-url>
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
| Method | Endpoint           | Description                |
|--------|--------------------|----------------------------|
| POST   | `/v1/orders/limit` | Submit a limit order       |
| GET    | `/v1/orderbook`    | View open orders           |
| GET    | `/v1/trades`       | View recent trade history  |
| GET    | `healthz`          | View monitoring or metrics |

- All endpoints require an `Authorization: Bearer secret-token` header.
- POST endpoints require an `X-Signature: HMAC256` as ${price+currencyPair+quantity}
- GET endpoints requires 'currencyPair' parameter ..?currencyPar=..

### 🟩  Trade Websocket

This service supports **real-time streaming of matched trades**

| Endpoint              | Method | Description                       |
|-----------------------|--------|-----------------------------------|
| `/ws/trades`          | WS     | Stream trades in real-time       |
---

## ✅ UAT Checklist

### UAT Scenarios

| Scenario                           | Result     |
|------------------------------------|------------|
| Submit valid BUY order             | ✅ Pass     |
| Submit valid SELL order            | ✅ Pass     |
| Partial order match                | ✅ Pass     |
| Full order match                   | ✅ Pass     |
| View order book with active orders | ✅ Pass     |
| View trade history                 | ✅ Pass     |
| Auth and signature success         | ✅ Pass     |
| Server error handling              | ✅ Pass     |
| Websocket updates                  | ✅ Pass     |                