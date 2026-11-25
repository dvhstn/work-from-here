package dev.dvhstn.workfromhere.integration.spaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.dvhstn.workfromhere.integration.BaseIntegrationTest;
import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.repository.SpaceResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceResourceControllerIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/spaces";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SpaceResourceRepository spaceResourceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        spaceResourceRepository.deleteAll();
    }

    private static final ParameterizedTypeReference<RestPage<SpaceResponseDTO>> PAGE_TYPE =
            new ParameterizedTypeReference<>() {};

    // --- GET /api/v1/spaces ---

    @Test
    void getAllSpaces_WhenEmpty_Returns200WithEmptyPage() {
        ResponseEntity<RestPage<SpaceResponseDTO>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, PAGE_TYPE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getTotalElements());
        assertEquals(0, response.getBody().getContent().size());
    }

    @Test
    void getAllSpaces_WhenPopulated_ReturnsAllSpaces() {
        restTemplate.postForEntity(BASE_URL, buildRequest("Cafe One", "First cafe", 1, null), SpaceResponseDTO.class);
        restTemplate.postForEntity(BASE_URL, buildRequest("Cafe Two", "Second cafe", 1, "pass123"), SpaceResponseDTO.class);

        ResponseEntity<RestPage<SpaceResponseDTO>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, PAGE_TYPE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getContent().size());
    }

    @Test
    void getAllSpaces_WithPageSize_ReturnsCorrectPage() {
        restTemplate.postForEntity(BASE_URL, buildRequest("Space A", "Desc A", 1, null), SpaceResponseDTO.class);
        restTemplate.postForEntity(BASE_URL, buildRequest("Space B", "Desc B", 1, null), SpaceResponseDTO.class);
        restTemplate.postForEntity(BASE_URL, buildRequest("Space C", "Desc C", 1, null), SpaceResponseDTO.class);

        ResponseEntity<RestPage<SpaceResponseDTO>> response = restTemplate.exchange(
                BASE_URL + "?page=0&size=2", HttpMethod.GET, null, PAGE_TYPE
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(2, response.getBody().getTotalPages());
    }

    // --- GET /api/v1/spaces (filtered) ---

    @Test
    void getAllSpaces_WithTypeFilter_ReturnsOnlyMatchingSpaces() {
        restTemplate.postForEntity(BASE_URL, buildRequest("Cafe One", "A cafe", 1, null), SpaceResponseDTO.class);
        restTemplate.postForEntity(BASE_URL, buildRequest("Hot Desk One", "A hot desk", 2, null), SpaceResponseDTO.class);

        ResponseEntity<RestPage<SpaceResponseDTO>> response = restTemplate.exchange(
                BASE_URL + "?type=cafe", HttpMethod.GET, null, PAGE_TYPE
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("Cafe One", response.getBody().getContent().get(0).getName());
    }

    @Test
    void createSpace_WithEachNewSpaceType_PersistsTypeAndFiltersByIt() {
        String[][] newTypes = {{"3", "BAR"}, {"4", "HOTEL"}, {"5", "LIBRARY"}, {"6", "COWORKING"}};

        for (String[] newType : newTypes) {
            ResponseEntity<SpaceResponseDTO> created = restTemplate.postForEntity(BASE_URL,
                    buildRequest(newType[1] + " Space", "A " + newType[1].toLowerCase() + " space", Integer.valueOf(newType[0]), null),
                    SpaceResponseDTO.class);

            assertEquals(HttpStatus.CREATED, created.getStatusCode());
            assertEquals(newType[1], created.getBody().getType().name());

            ResponseEntity<RestPage<SpaceResponseDTO>> filtered = restTemplate.exchange(
                    BASE_URL + "?type=" + newType[1].toLowerCase(), HttpMethod.GET, null, PAGE_TYPE
            );

            assertEquals(HttpStatus.OK, filtered.getStatusCode());
            assertEquals(1, filtered.getBody().getTotalElements());
            assertEquals(newType[1] + " Space", filtered.getBody().getContent().get(0).getName());
        }
    }

    @Test
    void getAllSpaces_WithHasWifiFilter_ReturnsOnlyWifiSpaces() {
        restTemplate.postForEntity(BASE_URL, buildRequest("Wifi Cafe", "Has wifi", 1, "pass123"), SpaceResponseDTO.class);
        restTemplate.postForEntity(BASE_URL, buildRequest("No Wifi Cafe", "No wifi", 1, null), SpaceResponseDTO.class);

        ResponseEntity<RestPage<SpaceResponseDTO>> response = restTemplate.exchange(
                BASE_URL + "?hasWifi=true", HttpMethod.GET, null, PAGE_TYPE
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("Wifi Cafe", response.getBody().getContent().get(0).getName());
    }

    @Test
    void getAllSpaces_WithInvalidType_Returns400() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "?type=invalid", HttpMethod.GET, null, String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // --- POST /api/v1/spaces ---

    @Test
    void createSpace_WhenValid_Returns201AndPersists() {
        SpaceRequestDTO request = buildRequest("Blue Bottle Coffee", "Great coffee spot", 1, null);

        ResponseEntity<SpaceResponseDTO> response = restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Blue Bottle Coffee", response.getBody().getName());
        assertEquals(1, spaceResourceRepository.count());
    }

    @Test
    void createSpace_WhenWifiAvailable_PersistsPassword() {
        SpaceRequestDTO request = buildRequest("The Hub", "Co-working space", 2, "wifi-pass-123");

        ResponseEntity<SpaceResponseDTO> response = restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("wifi-pass-123", response.getBody().getWifiPassword());
    }

    @Test
    void createSpace_WhenWifiNotAvailable_PasswordIsNull() {
        SpaceRequestDTO request = buildRequest("Silent Library", "Quiet work zone", 2, null);

        ResponseEntity<SpaceResponseDTO> response = restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody().getWifiPassword());
    }

    @Test
    void createSpace_WhenDuplicateName_Returns409() {
        SpaceRequestDTO request = buildRequest("Duplicate Cafe", "First one", 1, null);
        restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        ResponseEntity<String> duplicate = restTemplate.postForEntity(BASE_URL, request, String.class);

        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());
        assertEquals(1, spaceResourceRepository.count());
    }

    @Test
    void createSpace_WhenDuplicateNameDiffersOnlyByCaseAndWhitespace_Returns409() {
        restTemplate.postForEntity(BASE_URL, buildRequest("Duplicate Cafe", "First one", 1, null), SpaceResponseDTO.class);

        ResponseEntity<String> duplicate = restTemplate.postForEntity(
                BASE_URL, buildRequest("  duplicate CAFE ", "Second one", 1, null), String.class);

        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());
        assertEquals(1, spaceResourceRepository.count());
    }

    @Test
    void createSpace_WhenFieldsHaveSurroundingWhitespace_PersistsTrimmedValues() {
        SpaceRequestDTO request = buildRequest("  Padded Cafe  ", "  Padded desc ", 1, " secret ");

        ResponseEntity<SpaceResponseDTO> response = restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Padded Cafe", response.getBody().getName());
        assertEquals("Padded desc", response.getBody().getDescription());
        assertEquals("secret", response.getBody().getWifiPassword());
    }

    @Test
    void createSpace_WhenWifiPasswordBlank_Returns400() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL, buildRequest("Blank Wifi", "Desc", 1, "   "), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, spaceResourceRepository.count());
    }

    // --- GET /api/v1/spaces/{id} ---

    @Test
    void getSpaceById_WhenExists_Returns200() {
        SpaceRequestDTO request = buildRequest("Monmouth Coffee", "Great espresso", 1, null);
        SpaceResponseDTO created = restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class).getBody();

        ResponseEntity<SpaceResponseDTO> response = restTemplate.getForEntity(BASE_URL + "/" + created.getId(), SpaceResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Monmouth Coffee", response.getBody().getName());
    }

    @Test
    void getSpaceById_WhenNotFound_Returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(BASE_URL + "/999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- PUT /api/v1/spaces/{id} ---

    @Test
    void updateSpace_WhenValid_Returns200AndUpdatesDb() {
        SpaceResponseDTO created = restTemplate.postForEntity(
                BASE_URL, buildRequest("Original Name", "Original desc", 1, null), SpaceResponseDTO.class
        ).getBody();

        SpaceRequestDTO update = buildRequest("Updated Name", "Updated desc", 2, "newpassword");
        ResponseEntity<SpaceResponseDTO> response = restTemplate.exchange(
                BASE_URL + "/" + created.getId(), HttpMethod.PUT, new HttpEntity<>(update), SpaceResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("newpassword", response.getBody().getWifiPassword());
    }

    @Test
    void updateSpace_WhenNameTakenByAnotherSpaceIgnoringCase_Returns409() {
        restTemplate.postForEntity(BASE_URL, buildRequest("Taken Name", "Desc", 1, null), SpaceResponseDTO.class);
        SpaceResponseDTO other = restTemplate.postForEntity(
                BASE_URL, buildRequest("Other Name", "Desc", 1, null), SpaceResponseDTO.class
        ).getBody();

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/" + other.getId(), HttpMethod.PUT,
                new HttpEntity<>(buildRequest("TAKEN name", "Desc", 1, null)), String.class
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void updateSpace_WhenChangingCaseOfOwnName_Returns200() {
        SpaceResponseDTO created = restTemplate.postForEntity(
                BASE_URL, buildRequest("My Space", "Desc", 1, null), SpaceResponseDTO.class
        ).getBody();

        ResponseEntity<SpaceResponseDTO> response = restTemplate.exchange(
                BASE_URL + "/" + created.getId(), HttpMethod.PUT,
                new HttpEntity<>(buildRequest("MY SPACE", "Desc", 1, null)), SpaceResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("MY SPACE", response.getBody().getName());
    }

    @Test
    void updateSpace_WhenNotFound_Returns404() {
        SpaceRequestDTO update = buildRequest("Any Name", "Any desc", 1, null);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/999", HttpMethod.PUT, new HttpEntity<>(update), String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- DELETE /api/v1/spaces/{id} ---

    @Test
    void deleteSpace_WhenExists_Returns204AndRemovesFromDb() {
        SpaceResponseDTO created = restTemplate.postForEntity(
                BASE_URL, buildRequest("To Be Deleted", "Going away", 1, null), SpaceResponseDTO.class
        ).getBody();

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/" + created.getId(), HttpMethod.DELETE, null, Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(0, spaceResourceRepository.count());
    }

    @Test
    void deleteSpace_WhenNotFound_Returns404() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/999", HttpMethod.DELETE, null, String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- Images ---

    private static final ParameterizedTypeReference<Map<String, Object>> JSON_TYPE =
            new ParameterizedTypeReference<>() {};

    @Test
    void getSpaceById_WhenImageColumnsSet_ReturnsImage() {
        Long id = createSpace("Linen Hall Library", "Quiet reading room", 5);
        setImage(id, "spaces/" + id + "/linen-hall-library.jpg");

        ResponseEntity<Map<String, Object>> response = getJson(BASE_URL + "/" + id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedImage(id), response.getBody().get("image"));
    }

    @Test
    void getSpaceById_WhenNoImage_ReturnsImageNull() {
        Long id = createSpace("Plain Cafe", "No photo yet", 1);

        ResponseEntity<Map<String, Object>> response = getJson(BASE_URL + "/" + id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("image"));
        assertNull(response.getBody().get("image"));
    }

    @Test
    void getAllSpaces_IncludesImageOrNullPerSpace() {
        Long withImage = createSpace("Linen Hall Library", "Quiet reading room", 5);
        Long withoutImage = createSpace("Plain Cafe", "No photo yet", 1);
        setImage(withImage, "spaces/" + withImage + "/linen-hall-library.jpg");

        ResponseEntity<Map<String, Object>> response = getJson(BASE_URL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
        Map<Long, Object> imagesById = new HashMap<>();
        content.forEach(space -> imagesById.put(((Number) space.get("id")).longValue(), space.get("image")));
        assertEquals(expectedImage(withImage), imagesById.get(withImage));
        assertTrue(imagesById.containsKey(withoutImage));
        assertNull(imagesById.get(withoutImage));
    }

    @Test
    void updateSpace_WhenSpaceHasImage_KeepsImage() {
        Long id = createSpace("Linen Hall Library", "Quiet reading room", 5);
        setImage(id, "spaces/" + id + "/linen-hall-library.jpg");

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                BASE_URL + "/" + id, HttpMethod.PUT,
                new HttpEntity<>(buildRequest("Linen Hall", "Renamed", 5, "books")), JSON_TYPE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Linen Hall", response.getBody().get("name"));
        assertEquals(expectedImage(id), response.getBody().get("image"));
        assertEquals(expectedImage(id), getJson(BASE_URL + "/" + id).getBody().get("image"));
        assertEquals("spaces/" + id + "/linen-hall-library.jpg",
                jdbcTemplate.queryForObject("SELECT image_key FROM spaces WHERE id = ?", String.class, id));
    }

    private Long createSpace(String name, String description, int typeId) {
        return restTemplate.postForEntity(BASE_URL, buildRequest(name, description, typeId, null), SpaceResponseDTO.class)
                .getBody().getId();
    }

    private void setImage(Long id, String key) {
        jdbcTemplate.update(
                "UPDATE spaces SET image_key = ?, image_alt = ?, image_credit = ?, image_license = ?, image_source_url = ? "
                        + "WHERE id = ?",
                key, "Reading room with tall bookshelves", "Jane Doe", "CC BY-SA 4.0",
                "https://commons.wikimedia.org/wiki/File:Linen_Hall.jpg", id);
    }

    private static Map<String, Object> expectedImage(Long id) {
        return Map.of(
                "key", "spaces/" + id + "/linen-hall-library.jpg",
                "alt", "Reading room with tall bookshelves",
                "credit", "Jane Doe",
                "license", "CC BY-SA 4.0",
                "sourceUrl", "https://commons.wikimedia.org/wiki/File:Linen_Hall.jpg");
    }

    private ResponseEntity<Map<String, Object>> getJson(String url) {
        return restTemplate.exchange(url, HttpMethod.GET, null, JSON_TYPE);
    }

    // --- Client errors ---

    @Test
    void getSpaceById_WhenIdNotNumeric_Returns400() {
        ResponseEntity<Map<String, Object>> response = exchangeForError(BASE_URL + "/abc", HttpMethod.GET, null);

        assertError(response, HttpStatus.BAD_REQUEST, "Invalid value for 'id'", BASE_URL + "/abc");
    }

    @Test
    void createSpace_WhenJsonMalformed_Returns400() {
        ResponseEntity<Map<String, Object>> response = exchangeForError(
                BASE_URL, HttpMethod.POST, jsonEntity("{\"name\": \"Broken\","));

        assertError(response, HttpStatus.BAD_REQUEST, "Malformed or unreadable request body", BASE_URL);
        assertEquals(0, spaceResourceRepository.count());
    }

    @Test
    void createSpace_WhenTypeIdUnknown_Returns400() {
        ResponseEntity<Map<String, Object>> response = exchangeForError(
                BASE_URL, HttpMethod.POST, new HttpEntity<>(buildRequest("Mystery Space", "Unknown type", 99, null)));

        assertError(response, HttpStatus.BAD_REQUEST, "Unknown space type id: 99", BASE_URL);
        assertEquals(0, spaceResourceRepository.count());
    }

    @Test
    void createSpace_WhenContentTypeUnsupported_Returns415() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        ResponseEntity<Map<String, Object>> response = exchangeForError(
                BASE_URL, HttpMethod.POST, new HttpEntity<>("name=Cafe", headers));

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertEquals(415, response.getBody().get("status"));
        assertEquals(BASE_URL, response.getBody().get("path"));
        assertTrue(((String) response.getBody().get("message")).startsWith("Content-Type 'text/plain"));
    }

    @Test
    void getAllSpaces_WithInvalidSortProperty_Returns400() {
        ResponseEntity<Map<String, Object>> response = exchangeForError(
                BASE_URL + "?sort=bogus,asc", HttpMethod.GET, null);

        assertError(response, HttpStatus.BAD_REQUEST, "Invalid sort property 'bogus'", BASE_URL);
    }

    @Test
    void getAllSpaces_WithNonBooleanHasWifi_Returns400() {
        ResponseEntity<Map<String, Object>> response = exchangeForError(
                BASE_URL + "?hasWifi=maybe", HttpMethod.GET, null);

        assertError(response, HttpStatus.BAD_REQUEST, "Invalid value for 'hasWifi'", BASE_URL);
    }

    @Test
    void deleteCollection_WhenMethodNotSupported_Returns405() {
        ResponseEntity<Map<String, Object>> response = exchangeForError(BASE_URL, HttpMethod.DELETE, null);

        assertError(response, HttpStatus.METHOD_NOT_ALLOWED, "Method 'DELETE' is not supported.", BASE_URL);
        assertNotNull(response.getHeaders().getAllow());
        assertTrue(response.getHeaders().getAllow().contains(HttpMethod.GET));
    }

    @Test
    void unknownRoute_Returns404() {
        ResponseEntity<Map<String, Object>> response = exchangeForError("/api/v1/does-not-exist", HttpMethod.GET, null);

        assertError(response, HttpStatus.NOT_FOUND, "Resource not found", "/api/v1/does-not-exist");
    }

    private static final ParameterizedTypeReference<Map<String, Object>> ERROR_TYPE =
            new ParameterizedTypeReference<>() {};

    private ResponseEntity<Map<String, Object>> exchangeForError(String url, HttpMethod method, HttpEntity<?> entity) {
        return restTemplate.exchange(url, method, entity, ERROR_TYPE);
    }

    private static HttpEntity<String> jsonEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private static void assertError(
            ResponseEntity<Map<String, Object>> response, HttpStatus status, String message, String path)
    {
        assertEquals(status, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(status.value(), response.getBody().get("status"));
        assertEquals(message, response.getBody().get("message"));
        assertEquals(path, response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    private SpaceRequestDTO buildRequest(String name, String description, int typeId, String wifiPassword) {
        return SpaceRequestDTO.builder()
                .name(name)
                .description(description)
                .typeId(typeId)
                .wifiPassword(wifiPassword)
                .build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class RestPage<T> {
        private List<T> content;
        private long totalElements;
        private int totalPages;

        public List<T> getContent() { return content; }
        public void setContent(List<T> content) { this.content = content; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}
