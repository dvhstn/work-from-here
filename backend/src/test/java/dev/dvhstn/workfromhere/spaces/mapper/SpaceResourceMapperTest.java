package dev.dvhstn.workfromhere.spaces.mapper;

import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpaceResourceMapperTest {

    private final SpaceResourceMapper mapper = new SpaceResourceMapper();

    @Test
    void toSpaceResource_WhenSpaceRequestIsPassedIn_MapCorrectly() {
        // Given
        SpaceRequestDTO expected = createMockSpaceRequestDTO("TestSpace", "Not a real place", 1, "Password123!");

        // When
        SpaceResource actual = mapper.toSpaceResource(expected);

        // Then
        assertAll(
                ()-> assertEquals(expected.getName(), actual.getName()),
                ()-> assertEquals(expected.getDescription(), actual.getDescription()),
                ()-> assertEquals(expected.getTypeId(), actual.getType().getId()),
                ()-> assertEquals(expected.getWifiPassword(), actual.getWifiPassword())
        );
    }

    @Test
    void toSpaceResource_WhenNoWifiPassword_WifiNotAvailable() {
        // Given
        SpaceRequestDTO request = createMockSpaceRequestDTO("TestSpace", "Not a real place", 1, null);

        // When
        SpaceResource actual = mapper.toSpaceResource(request);

        // Then
        assertAll(
                () -> assertEquals(false, actual.isWifiAvailable()),
                () -> assertEquals(null, actual.getWifiPassword())
        );
    }

    @Test
    void toSpaceResponseDTO_whenSpaceIsPassedIn_MapCorrectly() {
        // Given
        SpaceResource expected = createMockSpaceResource(
                123L,"TestSpace", "Not a real place", SpaceTypeResource.CAFE, "Password123!");

        // When
        SpaceResponseDTO actual = mapper.toSpaceResponseDTO(expected);

        // Then
        assertAll(
                ()-> assertEquals(expected.getId(), actual.getId()),
                ()-> assertEquals(expected.getName(), actual.getName()),
                ()-> assertEquals(expected.getDescription(), actual.getDescription()),
                ()-> assertEquals(expected.getType(), actual.getType()),
                ()-> assertEquals(expected.isWifiAvailable(), actual.isWifiAvailable()),
                ()-> assertEquals(expected.getWifiPassword(), actual.getWifiPassword())
        );
    }

    private SpaceResource createMockSpaceResource(
            Long id, String name, String description, SpaceTypeResource type, String wifiPassword)
    {
        return SpaceResource.builder()
                .id(id)
                .name(name)
                .description(description)
                .type(type)
                .wifiPassword(wifiPassword)
                .build();
    }

    private SpaceRequestDTO createMockSpaceRequestDTO(
            String name, String description, Integer typeId, String wifiPassword)
    {
        return SpaceRequestDTO.builder()
                .name(name)
                .description(description)
                .typeId(typeId)
                .wifiPassword(wifiPassword)
                .build();
    }
}
