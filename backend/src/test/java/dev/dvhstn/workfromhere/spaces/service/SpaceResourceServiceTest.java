package dev.dvhstn.workfromhere.spaces.service;

import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceAlreadyExistsException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceNotFoundException;
import dev.dvhstn.workfromhere.spaces.mapper.SpaceResourceMapper;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import dev.dvhstn.workfromhere.spaces.repository.SpaceResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaceResourceServiceTest {

    @Mock
    private SpaceResourceMapper spaceResourceMapper;

    @Mock
    private SpaceResourceRepository spaceResourceRepository;

    @InjectMocks
    private SpaceResourceService spaceResourceService;

    // --- getAllSpaces ---

    @Test
    void getAllSpaces_WhenSpacesExist_ReturnsPageOfSpaces() {
        // Given
        SpaceResource space = buildSpaceResource(1L, "Blue Bottle Coffee", SpaceTypeResource.CAFE, true, "wifi123");
        SpaceResponseDTO responseDTO = buildResponseDTO(1L, "Blue Bottle Coffee", SpaceTypeResource.CAFE, true);
        when(spaceResourceRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(space)));
        when(spaceResourceMapper.toSpaceResponseDTO(space)).thenReturn(responseDTO);

        // When
        Page<SpaceResponseDTO> result = spaceResourceService.getAllSpaces(Pageable.unpaged());

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDTO, result.getContent().get(0));
    }

    @Test
    void getAllSpaces_WhenNoSpacesExist_ReturnsEmptyPage() {
        // Given
        when(spaceResourceRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        // When
        Page<SpaceResponseDTO> result = spaceResourceService.getAllSpaces(Pageable.unpaged());

        // Then
        assertTrue(result.isEmpty());
    }

    // --- getSpaceResourceById ---

    @Test
    void getSpaceResourceById_WhenFound_ReturnsSpaceResponseDTO() {
        // Given
        SpaceResource space = buildSpaceResource(1L, "The Hub", SpaceTypeResource.HOT_DESK, false, null);
        SpaceResponseDTO responseDTO = buildResponseDTO(1L, "The Hub", SpaceTypeResource.HOT_DESK, false);
        when(spaceResourceRepository.findById(1L)).thenReturn(Optional.of(space));
        when(spaceResourceMapper.toSpaceResponseDTO(space)).thenReturn(responseDTO);

        // When
        SpaceResponseDTO result = spaceResourceService.getSpaceResourceById(1L);

        // Then
        assertEquals(responseDTO, result);
    }

    @Test
    void getSpaceResourceById_WhenNotFound_ThrowsSpaceResourceNotFoundException() {
        // Given
        when(spaceResourceRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(SpaceResourceNotFoundException.class,
                () -> spaceResourceService.getSpaceResourceById(99L));
    }

    // --- createSpaceResource ---

    @Test
    void createSpaceResource_WhenNameIsUnique_ReturnsCreatedSpace() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Monmouth Coffee", "Great coffee", 1, true, "wifi123");
        SpaceResource entity = buildSpaceResource(null, "Monmouth Coffee", SpaceTypeResource.CAFE, true, "wifi123");
        SpaceResponseDTO responseDTO = buildResponseDTO(10L, "Monmouth Coffee", SpaceTypeResource.CAFE, true);
        when(spaceResourceRepository.existsByName("Monmouth Coffee")).thenReturn(false);
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(entity);
        when(spaceResourceMapper.toSpaceResponseDTO(entity)).thenReturn(responseDTO);

        // When
        SpaceResponseDTO result = spaceResourceService.createSpaceResource(request);

        // Then
        assertEquals(responseDTO, result);
        verify(spaceResourceRepository).saveAndFlush(entity);
    }

    @Test
    void createSpaceResource_WhenNameAlreadyExists_ThrowsSpaceResourceAlreadyExistsException() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Duplicate Name", "Some description", 1, false, null);
        when(spaceResourceRepository.existsByName("Duplicate Name")).thenReturn(true);

        // When / Then
        assertThrows(SpaceResourceAlreadyExistsException.class,
                () -> spaceResourceService.createSpaceResource(request));
        verify(spaceResourceRepository, never()).saveAndFlush(any());
    }

    // --- updateSpaceResource ---

    @Test
    void updateSpaceResource_WhenValidAndNameNotTaken_ReturnsUpdatedSpace() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Updated Name", "Updated description", 2, false, null);
        SpaceResource existing = buildSpaceResource(5L, "Old Name", SpaceTypeResource.CAFE, true, "oldwifi");
        SpaceResponseDTO responseDTO = buildResponseDTO(5L, "Updated Name", SpaceTypeResource.HOT_DESK, false);
        when(spaceResourceRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(spaceResourceRepository.existsByNameAndIdNot("Updated Name", 5L)).thenReturn(false);
        when(spaceResourceMapper.toSpaceResponseDTO(existing)).thenReturn(responseDTO);

        // When
        SpaceResponseDTO result = spaceResourceService.updateSpaceResource(request, 5L);

        // Then
        assertEquals(responseDTO, result);
        verify(spaceResourceRepository).saveAndFlush(existing);
    }

    @Test
    void updateSpaceResource_WhenNotFound_ThrowsSpaceResourceNotFoundException() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Updated Name", "Updated description", 1, false, null);
        when(spaceResourceRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(SpaceResourceNotFoundException.class,
                () -> spaceResourceService.updateSpaceResource(request, 99L));
        verify(spaceResourceRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateSpaceResource_WhenNameTakenByAnotherSpace_ThrowsSpaceResourceAlreadyExistsException() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Taken Name", "Some description", 1, false, null);
        SpaceResource existing = buildSpaceResource(5L, "Old Name", SpaceTypeResource.CAFE, false, null);
        when(spaceResourceRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(spaceResourceRepository.existsByNameAndIdNot("Taken Name", 5L)).thenReturn(true);

        // When / Then
        assertThrows(SpaceResourceAlreadyExistsException.class,
                () -> spaceResourceService.updateSpaceResource(request, 5L));
        verify(spaceResourceRepository, never()).saveAndFlush(any());
    }

    // --- deleteSpaceResource ---

    @Test
    void deleteSpaceResource_WhenFound_DeletesSpace() {
        // Given
        SpaceResource space = buildSpaceResource(7L, "The Hub", SpaceTypeResource.HOT_DESK, false, null);
        when(spaceResourceRepository.findById(7L)).thenReturn(Optional.of(space));

        // When
        spaceResourceService.deleteSpaceResource(7L);

        // Then
        verify(spaceResourceRepository).delete(space);
    }

    @Test
    void deleteSpaceResource_WhenNotFound_ThrowsSpaceResourceNotFoundException() {
        // Given
        when(spaceResourceRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(SpaceResourceNotFoundException.class,
                () -> spaceResourceService.deleteSpaceResource(99L));
        verify(spaceResourceRepository, never()).delete(any());
    }

    // --- Helpers ---

    private SpaceResource buildSpaceResource(
            Long id, String name, SpaceTypeResource type, boolean wifiAvailable, String wifiPassword)
    {
        return SpaceResource.builder()
                .id(id)
                .name(name)
                .description("A nice place to work")
                .type(type)
                .wifiAvailable(wifiAvailable)
                .wifiPassword(wifiPassword)
                .build();
    }

    private SpaceResponseDTO buildResponseDTO(Long id, String name, SpaceTypeResource type, boolean wifiAvailable) {
        return SpaceResponseDTO.builder()
                .id(id)
                .name(name)
                .description("A nice place to work")
                .type(type)
                .wifiAvailable(wifiAvailable)
                .wifiPassword(wifiAvailable ? "wifi123" : null)
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
