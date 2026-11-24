package dev.dvhstn.workfromhere.integration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaValidationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM spaces");
    }

    @Test
    void contextStarts_WithHibernateValidatingMigratedSchema() {
        // The context only starts if Hibernate's validation of the entities against the
        // Flyway-migrated schema succeeds, so reaching this point proves the schema matches.
        assertEquals("validate", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("false", environment.getProperty("spring.jpa.open-in-view"));
    }

    @Test
    void flyway_AppliesAllMigrationsSuccessfully() {
        MigrationInfo[] applied = flyway.info().applied();

        assertTrue(applied.length >= 2);
        assertTrue(Arrays.stream(applied).allMatch(m -> m.getState().isApplied() && !m.getState().isFailed()));
        assertTrue(Arrays.stream(applied).anyMatch(m -> "2".equals(m.getVersion().getVersion())));
        assertEquals(0, flyway.info().pending().length);
    }

    @Test
    void spacesTable_RejectsUnknownType() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO spaces (name, description, type) VALUES ('Bad Type', 'desc', 'OFFICE')"));
    }

    @Test
    void spacesTable_RejectsCaseInsensitiveDuplicateName() {
        jdbcTemplate.update("INSERT INTO spaces (name, description, type) VALUES ('Blue Bottle', 'desc', 'CAFE')");

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO spaces (name, description, type) VALUES ('BLUE BOTTLE', 'desc', 'CAFE')"));
    }
}
