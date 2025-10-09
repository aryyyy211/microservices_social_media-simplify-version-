ConnectHub (Simple Version) – Microservices Social Media Platform

Overview
ConnectHub is a simple, modular social media platform built with a microservices architecture. It demonstrates core patterns for service discovery, API gateway routing, security, observability, and independent service deployment.

Monorepo modules
- eureka-server: Service discovery (Netflix Eureka)
- api-gateway: Edge service and routing (Spring Cloud Gateway, WebFlux, security)
- user-service: User accounts, profiles
- post-service: Creating and managing posts
- comment-service: Comments on posts
- media-service: Media handling (e.g., image/video metadata, upload integration)
- interaction-service: Likes/reactions and other interactions
- feed-service: User feed aggregation
- notification-service: Notifications delivery

Tech stack
- Language/Runtime: Java 21
- Framework: Spring Boot 3.5.5
- Cloud tooling: Spring Cloud 2025.0.0 BOM
- Service discovery: Eureka Server/Client
- API gateway: Spring Cloud Gateway (WebFlux)
- Security: Spring Security, OAuth2 Resource Server (JWT validation at gateway)
- Observability: Micrometer, Prometheus registry, Brave/Zipkin tracing
- Build: Maven (multi-module)
- Testing: Testcontainers BOM available for integration testing (module-level)

Prerequisites
- JDK 21
- Maven 3.9+
- Optional (for observability): Docker (to run Zipkin, Prometheus, Grafana)

Repository layout
connecthub/
  ├─ api-gateway/
  ├─ eureka-server/
  ├─ user-service/
  ├─ post-service/
  ├─ comment-service/
  ├─ media-service/
  ├─ interaction-service/
  ├─ feed-service/
  ├─ notification-service/
  └─ pom.xml (parent aggregator)

Quick start
1) Clone and build
- mvn -v  # verify Maven
- java -version  # verify Java 21
- mvn -q -DskipTests install  # from the connecthub directory or repo root

2) Start the Eureka Server (service discovery)
- mvn -pl eureka-server spring-boot:run
- Dashboard: http://localhost:8761

3) Start core services in separate terminals (order suggestion)
- mvn -pl user-service spring-boot:run
- mvn -pl post-service spring-boot:run
- mvn -pl comment-service spring-boot:run
- mvn -pl media-service spring-boot:run
- mvn -pl interaction-service spring-boot:run
- mvn -pl feed-service spring-boot:run
- mvn -pl notification-service spring-boot:run

4) Start the API Gateway (edge)
- mvn -pl api-gateway spring-boot:run
- Gateway typically on http://localhost:8080 (verify server.port if customized)

Configuration
- Discovery: All services should register with eureka-server at http://localhost:8761 (configure eureka.client.service-url.defaultZone in each service if needed).
- Security: The api-gateway includes Spring Security and OAuth2 Resource Server dependencies for JWT validation. Provide issuer/JWK configuration via application.properties or environment variables (e.g., spring.security.oauth2.resourceserver.jwt.issuer-uri or jwk-set-uri). If security is not yet wired, you can temporarily permitAll() during local development.
- Observability:
  - Micrometer Prometheus registry included. Expose metrics on /actuator/metrics and /actuator/prometheus (configure management.endpoints.web.exposure.include=health,info,metrics,prometheus,...).
  - Tracing via micrometer-tracing-bridge-brave and zipkin-reporter-brave. Configure Zipkin endpoint (e.g., management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans) and enable tracing sampling as needed.

Ports (suggested defaults)
- Eureka Server: 8761
- API Gateway: 8080
- Service ports: each microservice can use its own port; if using random ports, discovery+gateway routing will still work. Check each service's application.yml/properties.

API access through the gateway
Routes are typically discovered via Eureka and mapped by service ID. Example patterns (adjust to actual configuration):
- GET http://localhost:8080/user-service/api/users/{id}
- POST http://localhost:8080/post-service/api/posts
- GET http://localhost:8080/feed-service/api/feed/{userId}
- POST http://localhost:8080/interaction-service/api/likes
- POST http://localhost:8080/comment-service/api/comments
These assume the gateway route predicate uses lb://SERVICE-ID backed by discovery. Replace with your actual path predicates if you define custom ones.

Building and testing
- Build all: mvn clean install
- Build a single module: mvn -pl api-gateway -am clean package
- Run tests: mvn -DskipTests=false test
- Integration tests: You can leverage Testcontainers in individual services (add module-level dependencies) to run with ephemeral infra.

Observability quick start (optional)
- Zipkin: docker run -d -p 9411:9411 openzipkin/zipkin
  - Then set tracing endpoint (e.g., management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans)
- Prometheus/Grafana: Configure Prometheus to scrape gateway/services at /actuator/prometheus. Import the Micrometer JVM/HTTP dashboards into Grafana.

Dockerization
- The parent POM contains a commented Google Jib configuration example. To containerize a module:
  1) Uncomment and adapt the jib-maven-plugin in the module’s POM or bring a similar block into the target module.
  2) Set the target image repo (e.g., docker.io/<your-username>/<service-name>:<tag>).
  3) Build: mvn -pl <module> jib:dockerBuild (or jib:build for remote registry).

Development tips
- Start Eureka first so other services can register successfully.
- If gateway security blocks requests during early development, configure a permissive SecurityWebFilterChain and add explicit permitAll() matchers for health/info.
- Enable actuator endpoints only as needed in non-production environments.

Roadmap (suggested next steps)
- Add persistent storage per service (PostgreSQL/MongoDB) and use Testcontainers in tests.
- Implement authentication/authorization integration end-to-end (JWT issuing service or external IdP).
- Add API contracts (OpenAPI/Swagger) per service and expose via gateway.
- Introduce circuit breaking/rate limiting (Resilience4j) at gateway and/or per service.
- Provide docker-compose for local infra (DBs, Zipkin, Prometheus, Grafana, MinIO/S3 for media).

Contributing
- Fork the repository, create a feature branch, commit with clear messages, and open a pull request.
- Please include module-level README updates for any new service or endpoint.

License
- Add your preferred license (e.g., MIT, Apache-2.0) at the repository root.

Troubleshooting
- Services not showing in Eureka: Verify eureka.client settings and that services can reach http://localhost:8761.
- 401/403 from gateway: Confirm JWT config or temporarily disable auth during early development.
- Missing metrics: Ensure actuator endpoints are exposed and the Prometheus registry dependency is present in the module.
- No traces in Zipkin: Verify tracing endpoint and sampling settings.
