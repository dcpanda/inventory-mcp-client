# Production Readiness TODO
*Enterprise-grade improvements required to deploy this example to production*

## 🔴 Critical (Must Fix Before Production)

### 1. Migrate Chat Endpoint to POST
- **File**: `ChatController.java`
- **Issue**: `GET /chat` exposes messages in URL query parameters (logged in access logs, limited length).
- **Fix**:
  ```java
  @PostMapping("/chat")
  public Mono<String> chat(@RequestBody ChatRequest request) { ... }
  public record ChatRequest(String message) {}
  ```

### 2. Add Input Validation
- **File**: `ChatController.java`, `McpController.java`
- **Issue**: No validation for `message` (size, content) or `warehouseCode` (format).
- **Fix**: Add `@Validated`, `@Size(max=500)` for messages, `@Pattern(regexp="^WH-\\d+$")` for warehouse codes.

## 🟠 High Priority

### 3. Externalize Hardcoded Configuration
- **File**: `ChatController.java`, `application.yml`
- **Issue**: System prompt and SafeGuard keywords are hardcoded.
- **Fix**: Create `ChatProperties.java` with `@ConfigurationProperties("chat")`, bind to `application.yml`.

### 4. Add Security Controls
- **Issue**: No authentication, rate limiting, or CORS.
- **Fix**: Add Spring Security dependency, configure basic auth/API keys, add Bucket4j for rate limiting.

### 5. Add Resilience Patterns
- **File**: `McpServiceImpl.java`
- **Fix**: Add Resilience4j retry (3 attempts, exponential backoff) and circuit breaker for MCP calls.

## 🟡 Medium Priority

### 6. Add Observability
- **Fix**: Add SLF4J logging to all services, enable actuator metrics, create `McpHealthIndicator` to check MCP connection status.

### 7. Improve API Design
- **Fix**: Prefix endpoints with `/api/v1`, add `springdoc-openapi-starter-webflux-api` for OpenAPI documentation.

### 8. Code Quality Improvements
- **Fix**: Extract magic strings to `McpConstants.java`, add JavaDoc to all public methods, log warnings if multiple MCP clients are detected.

## 🟢 Low Priority

### 9. Configuration Management
- Add `spring-boot-configuration-processor` for IDE support, use environment-specific profiles.

### 10. Dependency Cleanup
- Add Lombok for boilerplate reduction, review unused dependencies.

### 11. Expand Test Coverage
- Add tests for input validation, security, and resilience patterns.



