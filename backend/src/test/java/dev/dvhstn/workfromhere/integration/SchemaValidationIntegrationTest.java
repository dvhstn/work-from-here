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
import java.util.List;
import java.util.Map;

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
    void flyway_AppliesSpaceImagesMigration() {
        assertTrue(Arrays.stream(flyway.info().applied()).anyMatch(m -> "4".equals(m.getVersion().getVersion())));
    }

    @Test
    void spacesTable_HasNullableImageColumns() {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, character_maximum_length, is_nullable FROM information_schema.columns "
                        + "WHERE table_name = 'spaces' AND column_name LIKE 'image\\_%' ORDER BY column_name");

        assertEquals(List.of(
                imageColumn("image_alt", 255),
                imageColumn("image_credit", 255),
                imageColumn("image_key", 255),
                imageColumn("image_license", 100),
                imageColumn("image_source_url", 500)
        ), columns);
    }

    @Test
    void spacesTable_AcceptsSpaceWithoutImage() {
        jdbcTemplate.update("INSERT INTO spaces (name, description, type) VALUES ('No Image', 'desc', 'CAFE')");

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT count(*) FROM spaces WHERE name = 'No Image' AND image_key IS NULL", Integer.class));
    }

    private static Map<String, Object> imageColumn(String name, int length) {
        return Map.of("column_name", name, "data_type", "character varying",
                "character_maximum_length", length, "is_nullable", "YES");
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
