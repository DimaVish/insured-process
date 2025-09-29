# API Testing Collection

This directory contains various formats for testing the Insured Process API endpoints.

## Available Formats

### 1. Postman Collection
- **File**: `Insured-Process-API.postman_collection.json`
- **Usage**: Import into Postman application
- **Features**: 
  - Organized into folders (Client Management, Product Management, Test Scenarios)
  - Environment variable for base URL
  - Ready-to-use request examples
  - Error scenario testing

### 2. REST Client (HTTP)
- **File**: `api-requests.http`
- **Usage**: Compatible with IntelliJ IDEA and VS Code REST Client extension
- **Features**:
  - Numbered test sequence
  - Inline comments
  - Variable definitions
  - Complete test flow

## Prerequisites

1. **Start the application**:
   ```bash
   ./gradlew bootRun
   ```

2. **Verify application is running**:
   ```bash
   curl http://localhost:8080/api/clients/authenticate -H "Content-Type: application/json" -d '{"clientId":"C001","contactType":"email","contactValue":"john@example.com"}'
   ```

## Sample Data

The application initializes with the following test data:

**Clients:**
- `C001` - authenticated via email: `john@example.com`
- `C002` - authenticated via phone: `555-1234`

**Products:**
- `P001` - Health Insurance
- `P002` - Auto Insurance
- `P003` - Life Insurance

## Test Scenarios

### Happy Path
1. Authenticate existing client
2. Buy a product
3. Get client products
4. Update owned product

### Error Scenarios
1. Invalid authentication
2. Buy product twice (duplicate)
3. Update product not owned
4. Access with non-existent client

### Business Flow Testing
1. **New Client Connection → Create Client**
   - POST `/api/clients`

2. **Existing Client Connection → Authentication**
   - POST `/api/clients/authenticate`

3. **Authorized → Get Product List**
   - GET `/api/clients/{clientId}/products`

4. **Buy New Product**
   - POST `/api/products/{productId}/buy`

5. **Update Product**
   - PUT `/api/products/{productId}`

## Quick Start

### Using Postman
1. Import `Insured-Process-API.postman_collection.json`
2. Set environment variable `baseUrl` to `http://localhost:8080`
3. Run requests in order

### Using REST Client (VS Code/IntelliJ)
1. Install REST Client extension
2. Open `api-requests.http`
3. Click "Send Request" for each test

## Response Examples

### Successful Authentication
```json
"Client authenticated successfully"
```

### Client Products
```json
[
  {
    "id": "P001",
    "name": "Health Insurance",
    "description": "Comprehensive health coverage"
  }
]
```

### Error Response
```json
{
  "error": "BAD_REQUEST",
  "message": "Client already owns this product"
}
```