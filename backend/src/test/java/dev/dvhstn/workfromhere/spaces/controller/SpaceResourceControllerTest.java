package dev.dvhstn.workfromhere.spaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceAlreadyExistsException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceNotFoundException;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import dev.dvhstn.workfromhere.spaces.service.SpaceResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpaceResourceController.class)
class SpaceResourceControllerTest {

    private static final String BASE_URL = "/api/v1/spaces";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpaceResourceService spaceResourceService;

    // --- GET /api/v1/spaces ---

    @Test
    void getAllSpaces_Returns200WithPageOfSpaces() throws Exception {
        // Given
        SpaceResponseDTO space = buildResponseDTO(1L, "Blue Bottle Coffee", SpaceTypeResource.CAFE, true);
        when(spaceResourceService.getAllSpaces(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(space)));

        // When / Then
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Blue Bottle Coffee"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllSpaces_WhenEmpty_Returns200WithEmptyPage() throws Exception {
        // Given
        when(spaceResourceService.getAllSpaces(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        // When / Then
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    // --- GET /api/v1/spaces/{id} ---

    @Test
    void getSpaceById_WhenFound_Returns200() throws Exception {
        // Given
        SpaceResponseDTO space = buildResponseDTO(42L, "The Hub", SpaceTypeResource.HOT_DESK, false);
        when(spaceResourceService.getSpaceResourceById(42L)).thenReturn(space);

        // When / Then
        mockMvc.perform(get(BASE_URL + "/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42L))
                .andExpect(jsonPath("$.name").value("The Hub"))
                .andExpect(jsonPath("$.type").value("HOT_DESK"))
                .andExpect(jsonPath("$.wifiAvailable").value(false));
    }

    @Test
    void getSpaceById_WhenNotFound_Returns404() throws Exception {
        // Given
        when(spaceResourceService.getSpaceResourceById(99L))
                .thenThrow(new SpaceResourceNotFoundException("Space with id 99 not found"));

        // When / Then
        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Space with id 99 not found"));
    }

    // --- POST /api/v1/spaces ---

    @Test
    void createSpace_WhenValid_Returns201WithLocationHeader() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Monmouth Coffee", "Great coffee", 1, true, "wifi123");
        SpaceResponseDTO response = buildResponseDTO(10L, "Monmouth Coffee", SpaceTypeResource.CAFE, true);
        when(spaceResourceService.createSpaceResource(any(SpaceRequestDTO.class))).thenReturn(response);

        // When / Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/spaces/10")))
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Monmouth Coffee"));
    }

    @Test
    void createSpace_WhenNameBlank_Returns400() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("", "Some description", 1, false, null);

        // When / Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Name is required")));
    }

    @Test
    void createSpace_WhenDescriptionBlank_Returns400() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Valid Name", "", 1, false, null);

        // When / Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Description is required")));
    }

    @Test
    void createSpace_WhenTypeIdNull_Returns400() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Valid Name", "Valid description", null, false, null);

        // When / Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Type id is required")));
    }

    @Test
    void createSpace_WhenWifiAvailableButNoPassword_Returns400() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Valid Name", "Valid description", 1, true, null);

        // When / Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("WiFi password is required when WiFi is available")));
    }

    @Test
    void createSpace_WhenNameAlreadyExists_Returns409() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Duplicate Name", "Some description", 1, false, null);
        when(spaceResourceService.createSpaceResource(any(SpaceRequestDTO.class)))
                .thenThrow(new SpaceResourceAlreadyExistsException("Space with name 'Duplicate Name' already exists"));

        // When / Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Space with name 'Duplicate Name' already exists"));
    }

    @Test
    void createSpace_WhenNameExceedsMaxLength_Returns400() throws Exception {
        // Given
        String longName = "A".repeat(101);
        SpaceRequestDTO request = buildRequestDTO(longName, "Valid description", 1, false, null);

        // When / Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Name must be at most 100 characters")));
    }

    // --- PUT /api/v1/spaces/{id} ---

    @Test
    void updateSpace_WhenValid_Returns200() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Updated Name", "Updated description", 2, false, null);
        SpaceResponseDTO response = buildResponseDTO(5L, "Updated Name", SpaceTypeResource.HOT_DESK, false);
        when(spaceResourceService.updateSpaceResource(any(SpaceRequestDTO.class), eq(5L))).thenReturn(response);

        // When / Then
        mockMvc.perform(put(BASE_URL + "/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.type").value("HOT_DESK"));
    }

    @Test
    void updateSpace_WhenNotFound_Returns404() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Updated Name", "Updated description", 1, false, null);
        when(spaceResourceService.updateSpaceResource(any(SpaceRequestDTO.class), eq(99L)))
                .thenThrow(new SpaceResourceNotFoundException("Space with id 99 not found"));

        // When / Then
        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateSpace_WhenNameAlreadyExists_Returns409() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Taken Name", "Some description", 1, false, null);
        when(spaceResourceService.updateSpaceResource(any(SpaceRequestDTO.class), eq(5L)))
                .thenThrow(new SpaceResourceAlreadyExistsException("Space with name 'Taken Name' already exists"));

        // When / Then
        mockMvc.perform(put(BASE_URL + "/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Space with name 'Taken Name' already exists"));
    }

    @Test
    void updateSpace_WhenBodyInvalid_Returns400() throws Exception {
        // Given
        SpaceRequestDTO request = buildRequestDTO("", "", null, false, null);

        // When / Then
        mockMvc.perform(put(BASE_URL + "/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // --- DELETE /api/v1/spaces/{id} ---

    @Test
    void deleteSpace_WhenFound_Returns204() throws Exception {
        // Given
        doNothing().when(spaceResourceService).deleteSpaceResource(7L);

        // When / Then
        mockMvc.perform(delete(BASE_URL + "/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSpace_WhenNotFound_Returns404() throws Exception {
        // Given
        doThrow(new SpaceResourceNotFoundException("Space with id 99 not found"))
                .when(spaceResourceService).deleteSpaceResource(99L);

        // When / Then
        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Space with id 99 not found"));
    }

    // --- Helpers ---

    private SpaceResponseDTO buildResponseDTO(Long id, String name, SpaceTypeResource type, boolean wifiAvailable) {
        return SpaceResponseDTO.builder()
                .id(id)
                .name(name)
                .description("A nice place to work")
                .type(type)
                .wifiAvailable(wifiAvailable)
                .wifiPassword(wifiAvailable ? "password123" : null)
                .build();
    }

    private SpaceRequestDTO buildRequestDTO(
            String name, String description, Integer typeId, boolean wifiAvailable, String wifiPassword)
    {
        return SpaceRequestDTO.builder()
                .name(name)
                .description(description)
                .typeId(typeId)
                .wifiAvailable(wifiAvailable)
                .wifiPassword(wifiPassword)
                .build();
    }
}
