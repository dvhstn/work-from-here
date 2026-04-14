package dev.dvhstn.workfromhere.integration.spaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.dvhstn.workfromhere.integration.BaseIntegrationTest;
import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.repository.SpaceResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SpaceResourceControllerIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/spaces";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SpaceResourceRepository spaceResourceRepository;

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
        restTemplate.postForEntity(BASE_URL, buildRequest("Cafe One", "First cafe", 1, false, null), SpaceResponseDTO.class);
        restTemplate.postForEntity(BASE_URL, buildRequest("Cafe Two", "Second cafe", 1, true, "pass123"), SpaceResponseDTO.class);

        ResponseEntity<RestPage<SpaceResponseDTO>> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, PAGE_TYPE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getContent().size());
    }

    @Test
    void getAllSpaces_WithPageSize_ReturnsCorrectPage() {
        restTemplate.postForEntity(BASE_URL, buildRequest("Space A", "Desc A", 1, false, null), SpaceResponseDTO.class);
        restTemplate.postForEntity(BASE_URL, buildRequest("Space B", "Desc B", 1, false, null), SpaceResponseDTO.class);
        restTemplate.postForEntity(BASE_URL, buildRequest("Space C", "Desc C", 1, false, null), SpaceResponseDTO.class);

        ResponseEntity<RestPage<SpaceResponseDTO>> response = restTemplate.exchange(
                BASE_URL + "?page=0&size=2", HttpMethod.GET, null, PAGE_TYPE
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(2, response.getBody().getTotalPages());
    }

    // --- POST /api/v1/spaces ---

    @Test
    void createSpace_WhenValid_Returns201AndPersists() {
        SpaceRequestDTO request = buildRequest("Blue Bottle Coffee", "Great coffee spot", 1, false, null);

        ResponseEntity<SpaceResponseDTO> response = restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Blue Bottle Coffee", response.getBody().getName());
        assertEquals(1, spaceResourceRepository.count());
    }

    @Test
    void createSpace_WhenWifiAvailable_PersistsPassword() {
        SpaceRequestDTO request = buildRequest("The Hub", "Co-working space", 2, true, "wifi-pass-123");

        ResponseEntity<SpaceResponseDTO> response = restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("wifi-pass-123", response.getBody().getWifiPassword());
    }

    @Test
    void createSpace_WhenWifiNotAvailable_PasswordIsNull() {
        SpaceRequestDTO request = buildRequest("Silent Library", "Quiet work zone", 2, false, null);

        ResponseEntity<SpaceResponseDTO> response = restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody().getWifiPassword());
    }

    @Test
    void createSpace_WhenDuplicateName_Returns409() {
        SpaceRequestDTO request = buildRequest("Duplicate Cafe", "First one", 1, false, null);
        restTemplate.postForEntity(BASE_URL, request, SpaceResponseDTO.class);

        ResponseEntity<String> duplicate = restTemplate.postForEntity(BASE_URL, request, String.class);

        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());
        assertEquals(1, spaceResourceRepository.count());
    }

    // --- GET /api/v1/spaces/{id} ---

    @Test
    void getSpaceById_WhenExists_Returns200() {
        SpaceRequestDTO request = buildRequest("Monmouth Coffee", "Great espresso", 1, false, null);
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
                BASE_URL, buildRequest("Original Name", "Original desc", 1, false, null), SpaceResponseDTO.class
        ).getBody();

        SpaceRequestDTO update = buildRequest("Updated Name", "Updated desc", 2, true, "newpassword");
        ResponseEntity<SpaceResponseDTO> response = restTemplate.exchange(
                BASE_URL + "/" + created.getId(), HttpMethod.PUT, new HttpEntity<>(update), SpaceResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals("newpassword", response.getBody().getWifiPassword());
    }

    @Test
    void updateSpace_WhenNotFound_Returns404() {
        SpaceRequestDTO update = buildRequest("Any Name", "Any desc", 1, false, null);
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/999", HttpMethod.PUT, new HttpEntity<>(update), String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // --- DELETE /api/v1/spaces/{id} ---

    @Test
    void deleteSpace_WhenExists_Returns204AndRemovesFromDb() {
        SpaceResponseDTO created = restTemplate.postForEntity(
                BASE_URL, buildRequest("To Be Deleted", "Going away", 1, false, null), SpaceResponseDTO.class
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

    private SpaceRequestDTO buildRequest(String name, String description, int typeId, boolean wifiAvailable, String wifiPassword) {
        return SpaceRequestDTO.builder()
                .name(name)
                .description(description)
                .typeId(typeId)
                .wifiAvailable(wifiAvailable)
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
