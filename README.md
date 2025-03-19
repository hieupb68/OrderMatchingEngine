# High-Frequency Trading Order Matching Engine

A high-performance order matching engine system built with Java 21, Spring Boot 3, and modern low-latency technologies.

## System Architecture

The system consists of three main modules:

1. **Order API (Spring Boot)**
   - REST API for order submission and management
   - Handles client requests and order validation
   - Publishes orders to Kafka for processing

2. **Matching Engine (Java, Low Latency)**
   - High-performance order matching implementation
   - Uses Disruptor for lock-free event processing
   - Implements Price-Time Priority matching algorithm
   - Uses Chronicle Queue for zero-copy IPC

3. **Messaging & Storage**
   - Handles trade execution logging
   - Stores order history in PostgreSQL
   - Manages Kafka message processing

## Technology Stack

| Component | Technology |
|-----------|------------|
| Backend API | Spring Boot 3, REST API |
| Order Matching Engine | Java 21, Disruptor, Chronicle Queue |
| Concurrency | Lock-Free Data Structures, Multithreading |
| Messaging | Kafka (Pub-Sub), Chronicle Queue (Zero-Copy IPC) |
| Database | PostgreSQL (Order History) |
| Benchmark | JMH, wrk2 (Load Test) |

## Prerequisites

- Java 21 JDK
- Maven 3.8+
- PostgreSQL 15+
- Apache Kafka 3.6+
- Docker (optional, for running dependencies)

## Project Structure

```
order-matching-engine/
├── order-api/              # REST API module
├── matching-engine/        # Core matching engine
└── messaging-storage/      # Message processing & storage
```

## Building the Project

```bash
# Build all modules
mvn clean install

# Build individual modules
cd order-api && mvn clean install
cd matching-engine && mvn clean install
cd messaging-storage && mvn clean install
```

## Running the Services

1. Start PostgreSQL and Kafka (using Docker Compose):
```bash
docker-compose up -d
```

2. Start the Order API:
```bash
cd order-api
mvn spring-boot:run
```

3. Start the Matching Engine:
```bash
cd matching-engine
mvn spring-boot:run
```

4. Start the Messaging & Storage service:
```bash
cd messaging-storage
mvn spring-boot:run
```

## API Endpoints

### Order API

- `POST /api/v1/orders` - Submit new order
- `GET /api/v1/orders/{orderId}` - Get order status
- `GET /api/v1/orders` - List orders
- `DELETE /api/v1/orders/{orderId}` - Cancel order

## Performance Testing

Run JMH benchmarks:
```bash
cd matching-engine
mvn clean install
java -jar target/benchmarks.jar
```

Load testing with wrk2:
```bash
wrk -t12 -c400 -d30s http://localhost:8080/api/v1/orders
```

## Configuration

Each module has its own `application.yml` configuration file:

- `order-api/src/main/resources/application.yml`
- `matching-engine/src/main/resources/application.yml`
- `messaging-storage/src/main/resources/application.yml`

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. "# OrderMatchingEngine" 
