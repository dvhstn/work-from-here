package dev.dvhstn.workfromhere.spaces.mapper;

import dev.dvhstn.workfromhere.spaces.dto.SpaceImageDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.model.SpaceImage;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    void toSpaceResource_WhenFieldsHaveSurroundingWhitespace_TrimsThem() {
        // Given
        SpaceRequestDTO request = createMockSpaceRequestDTO("  TestSpace \t", "\n Not a real place  ", 2, "  Password123!  ");

        // When
        SpaceResource actual = mapper.toSpaceResource(request);

        // Then
        assertAll(
                () -> assertEquals("TestSpace", actual.getName()),
                () -> assertEquals("Not a real place", actual.getDescription()),
                () -> assertEquals(SpaceTypeResource.HOT_DESK, actual.getType()),
                () -> assertEquals("Password123!", actual.getWifiPassword())
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
                ()-> assertEquals(expected.getWifiPassword(), actual.getWifiPassword())
        );
    }

    @Test
    void toSpaceResponseDTO_WhenSpaceHasImage_MapsImage() {
        // Given
        SpaceResource space = createMockSpaceResource(12L, "Linen Hall", "Library", SpaceTypeResource.LIBRARY, null);
        space.setImage(SpaceImage.builder()
                .key("spaces/12/linen-hall-library.jpg")
                .alt("Reading room")
                .credit("Jane Doe")
                .license("CC BY-SA 4.0")
                .sourceUrl("https://commons.wikimedia.org/wiki/File:Linen_Hall.jpg")
                .build());

        // When
        SpaceImageDTO actual = mapper.toSpaceResponseDTO(space).getImage();

        // Then
        assertAll(
                () -> assertEquals("spaces/12/linen-hall-library.jpg", actual.getKey()),
                () -> assertEquals("Reading room", actual.getAlt()),
                () -> assertEquals("Jane Doe", actual.getCredit()),
                () -> assertEquals("CC BY-SA 4.0", actual.getLicense()),
                () -> assertEquals("https://commons.wikimedia.org/wiki/File:Linen_Hall.jpg", actual.getSourceUrl())
        );
    }

    @Test
    void toSpaceResponseDTO_WhenSpaceHasNoImage_ImageIsNull() {
        // Given
        SpaceResource space = createMockSpaceResource(1L, "TestSpace", "Not a real place", SpaceTypeResource.CAFE, null);

        // When
        SpaceResponseDTO actual = mapper.toSpaceResponseDTO(space);

        // Then
        assertNull(actual.getImage());
    }

    @Test
    void toSpaceResponseDTO_WhenImageHasNoKey_ImageIsNull() {
        // Given
        SpaceResource space = createMockSpaceResource(1L, "TestSpace", "Not a real place", SpaceTypeResource.CAFE, null);
        space.setImage(SpaceImage.builder().alt("Orphaned alt text").credit("Jane Doe").build());

        // When
        SpaceResponseDTO actual = mapper.toSpaceResponseDTO(space);

        // Then
        assertNull(actual.getImage());
    }

    @Test
    void toSpaceResource_NeverSetsImage() {
        // When
        SpaceResource actual = mapper.toSpaceResource(createMockSpaceRequestDTO("TestSpace", "Not a real place", 1, null));

        // Then
        assertNull(actual.getImage());
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
