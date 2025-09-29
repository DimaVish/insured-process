# Insured Identification Process

A Spring Boot REST API application that manages client identification and product purchasing processes for an insurance company.

## Requirements

### Business Requirements
- Client identification and authentication system
- Product management and purchasing
- Client-product association tracking
- No database required - runtime objects only
- No GUI needed

## Entity Relationship Diagram

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│     CLIENT      │────▷│ CONTACT_METHOD  │     │    PRODUCT      │
├─────────────────┤ 1:N ├─────────────────┤     ├─────────────────┤
│ id (PK)         │     │ type            │     │ id (PK)         │
│ created_at      │     │ value           │     │ name            │
│ updated_at      │     │ client_id (FK)  │     │ description     │
└─────────────────┘     └─────────────────┘     │ created_at      │
         │                                      │ updated_at      │
         │               ┌─────────────────┐    └─────────────────┘
         └──────────────▷│ CLIENT_PRODUCT  │◁─────────────┘
                    1:N  ├─────────────────┤ N:1
                         │ client_id (FK)  │
                         │ product_id (FK) │
                         │ purchase_date   │
                         │ status          │
                         │ created_at      │
                         │ updated_at      │
                         └─────────────────┘
```

### Key Relationships
- **CLIENT** has multiple **CONTACT_METHOD**s for authentication
- **CLIENT** can own multiple **PRODUCT**s through **CLIENT_PRODUCT**
- **CLIENT_PRODUCT** tracks purchase date and prevents duplicate ownership. In the case we are using DB it should be a JOIN table

### Supported Operations
- **Client Authentication**: Create new clients or authenticate existing ones via contact methods
- **Product Purchase**: Buy insurance products (prevents duplicate purchases per client)
- **Product Updates**: Modify product details (only for products owned by the client)
- **Ownership Tracking**: View client's purchased products with purchase history

### Technical Requirements
- Java 21 (developed with Java 21)
- Gradle 8.14.3
- Spring Boot 3.5.6

## Running the Application

### Prerequisites
- Java 21 installed
- Git installed

### Setup and Run
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd insured-process
   ```

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```
   
   On Windows:
   ```cmd
   gradlew.bat bootRun
   ```

3. The API will be available at `http://localhost:8080`

### Building and Running JAR

To create an executable JAR file:

1. Build the JAR:
   ```bash
   ./gradlew build
   ```
   
   On Windows:
   ```cmd
   gradlew.bat build
   ```

2. The JAR file will be created in `build/libs/insured-process-0.0.1-SNAPSHOT.jar`

3. Run the JAR:
   ```bash
   java -jar build/libs/insured-process-0.0.1-SNAPSHOT.jar
   ```

4. The application will start on `http://localhost:8080`

### Verify Installation
Test the application with a simple request:
```bash
curl -X POST http://localhost:8080/api/clients/authenticate \
  -H "Content-Type: application/json" \
  -d '{"clientId":"C001","contactType":"email","contactValue":"john@example.com"}'
```

## API Testing

The `api-collection/` directory contains comprehensive API testing resources:

- **Postman Collection**: `Insured-Process-API.postman_collection.json` - Import into Postman for GUI testing
- **REST Client**: `api-requests.http` - Use with VS Code/IntelliJ REST Client extension

See [api-collection/README.md](api-collection/README.md) for detailed usage instructions and test scenarios.

## Testing

### Running Tests

Run all tests:
```bash
./gradlew test
```

Run specific test types:
```bash
# Unit tests only
./gradlew test --tests "*ServiceTest"

# Controller tests only  
./gradlew test --tests "*ControllerTest"

# Integration tests only
./gradlew test --tests "*IntegrationTest"
```

### Test Structure

- **Unit Tests**: Service layer business logic (`src/test/java/.../service/`)
- **Controller Tests**: REST endpoint behavior (`src/test/java/.../controller/`)
- **Integration Tests**: End-to-end API workflows (`src/test/java/.../integration/`)

### Test Coverage

Tests cover:
-  Client authentication and creation
-  Product purchase workflows  
-  Product update authorization
-  Error handling and edge cases
-  Complete business scenarios