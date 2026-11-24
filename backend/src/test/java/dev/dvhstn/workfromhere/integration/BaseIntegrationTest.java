package dev.dvhstn.workfromhere.integration;

import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base class for integration tests. Uses a single Postgres container shared by every
 * subclass for the whole test run: it is started once in a static initialiser and left
 * for Testcontainers' Ryuk to clean up, so the cached Spring context stays valid across
 * test classes. The application runs with the main application.yaml (Flyway migrations
 * plus ddl-auto: validate); only the datasource connection details come from the container.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public abstract class BaseIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    static {
        postgres.start();
    }

    @LocalServerPort
    protected int port;
}
