package dev.dvhstn.workfromhere.spaces.service;

import dev.dvhstn.workfromhere.spaces.dto.SpaceFilterDTO;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
        SpaceResource space = buildSpaceResource(1L, "Blue Bottle Coffee", SpaceTypeResource.CAFE, "wifi123");
        SpaceResponseDTO responseDTO = buildResponseDTO(1L, "Blue Bottle Coffee", SpaceTypeResource.CAFE, "wifi123");
        when(spaceResourceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(space)));
        when(spaceResourceMapper.toSpaceResponseDTO(space)).thenReturn(responseDTO);

        // When
        Page<SpaceResponseDTO> result = spaceResourceService.getAllSpaces(Pageable.unpaged(), new SpaceFilterDTO());

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDTO, result.getContent().get(0));
    }

    @Test
    void getAllSpaces_WhenNoSpacesExist_ReturnsEmptyPage() {
        // Given
        when(spaceResourceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        // When
        Page<SpaceResponseDTO> result = spaceResourceService.getAllSpaces(Pageable.unpaged(), new SpaceFilterDTO());

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllSpaces_WithTypeFilter_ReturnsFilteredPage() {
        // Given
        SpaceResource cafe = buildSpaceResource(1L, "Blue Bottle Coffee", SpaceTypeResource.CAFE, null);
        SpaceResponseDTO responseDTO = buildResponseDTO(1L, "Blue Bottle Coffee", SpaceTypeResource.CAFE, null);
        when(spaceResourceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(cafe)));
        when(spaceResourceMapper.toSpaceResponseDTO(cafe)).thenReturn(responseDTO);

        // When
        SpaceFilterDTO filter = SpaceFilterDTO.builder().type("cafe").build();
        Page<SpaceResponseDTO> result = spaceResourceService.getAllSpaces(Pageable.unpaged(), filter);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(SpaceTypeResource.CAFE, result.getContent().get(0).getType());
    }

    // --- getSpaceResourceById ---

    @Test
    void getSpaceResourceById_WhenFound_ReturnsSpaceResponseDTO() {
        // Given
        SpaceResource space = buildSpaceResource(1L, "The Hub", SpaceTypeResource.HOT_DESK, null);
        SpaceResponseDTO responseDTO = buildResponseDTO(1L, "The Hub", SpaceTypeResource.HOT_DESK, null);
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
        SpaceRequestDTO request = buildRequestDTO("Monmouth Coffee", "Great coffee", 1, "wifi123");
        SpaceResource entity = buildSpaceResource(null, "Monmouth Coffee", SpaceTypeResource.CAFE, "wifi123");
        SpaceResponseDTO responseDTO = buildResponseDTO(10L, "Monmouth Coffee", SpaceTypeResource.CAFE, "wifi123");
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(entity);
        when(spaceResourceRepository.existsByNameIgnoreCase("Monmouth Coffee")).thenReturn(false);
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
        SpaceRequestDTO request = buildRequestDTO("Duplicate Name", "Some description", 1, null);
        SpaceResource entity = buildSpaceResource(null, "Duplicate Name", SpaceTypeResource.CAFE, null);
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(entity);
        when(spaceResourceRepository.existsByNameIgnoreCase("Duplicate Name")).thenReturn(true);

        // When / Then
        assertThrows(SpaceResourceAlreadyExistsException.class,
                () -> spaceResourceService.createSpaceResource(request));
        verify(spaceResourceRepository, never()).saveAndFlush(any());
    }

    @Test
    void createSpaceResource_WhenNameExistsWithDifferentCase_ThrowsSpaceResourceAlreadyExistsException() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("  monmouth COFFEE ", "Some description", 1, null);
        SpaceResource entity = buildSpaceResource(null, "monmouth COFFEE", SpaceTypeResource.CAFE, null);
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(entity);
        when(spaceResourceRepository.existsByNameIgnoreCase("monmouth COFFEE")).thenReturn(true);

        // When / Then
        SpaceResourceAlreadyExistsException ex = assertThrows(SpaceResourceAlreadyExistsException.class,
                () -> spaceResourceService.createSpaceResource(request));
        assertEquals("Space with name 'monmouth COFFEE' already exists", ex.getMessage());
        verify(spaceResourceRepository, never()).saveAndFlush(any());
    }

    @Test
    void createSpaceResource_WhenSaveViolatesUniqueConstraint_ThrowsSpaceResourceAlreadyExistsException() {
        // Given: a concurrent request inserted the same name after the existsByNameIgnoreCase check
        SpaceRequestDTO request = buildRequestDTO("Race Cafe", "Some description", 1, null);
        SpaceResource entity = buildSpaceResource(null, "Race Cafe", SpaceTypeResource.CAFE, null);
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(entity);
        when(spaceResourceRepository.existsByNameIgnoreCase("Race Cafe")).thenReturn(false);
        when(spaceResourceRepository.saveAndFlush(entity))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

        // When / Then
        SpaceResourceAlreadyExistsException ex = assertThrows(SpaceResourceAlreadyExistsException.class,
                () -> spaceResourceService.createSpaceResource(request));
        assertEquals("Space with name 'Race Cafe' already exists", ex.getMessage());
        verify(spaceResourceMapper, never()).toSpaceResponseDTO(any());
    }

    // --- updateSpaceResource ---

    @Test
    void updateSpaceResource_WhenValidAndNameNotTaken_ReturnsUpdatedSpace() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Updated Name", "Updated description", 2, null);
        SpaceResource existing = buildSpaceResource(5L, "Old Name", SpaceTypeResource.CAFE, "oldwifi");
        SpaceResource mapped = buildSpaceResource(null, "Updated Name", SpaceTypeResource.HOT_DESK, null);
        SpaceResponseDTO responseDTO = buildResponseDTO(5L, "Updated Name", SpaceTypeResource.HOT_DESK, null);
        when(spaceResourceRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(mapped);
        when(spaceResourceRepository.existsByNameIgnoreCaseAndIdNot("Updated Name", 5L)).thenReturn(false);
        when(spaceResourceMapper.toSpaceResponseDTO(existing)).thenReturn(responseDTO);

        // When
        SpaceResponseDTO result = spaceResourceService.updateSpaceResource(request, 5L);

        // Then
        assertEquals(responseDTO, result);
        assertEquals("Updated Name", existing.getName());
        assertEquals(SpaceTypeResource.HOT_DESK, existing.getType());
        assertNull(existing.getWifiPassword());
        verify(spaceResourceRepository).saveAndFlush(existing);
    }

    @Test
    void updateSpaceResource_WhenNotFound_ThrowsSpaceResourceNotFoundException() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Updated Name", "Updated description", 1, null);
        when(spaceResourceRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(SpaceResourceNotFoundException.class,
                () -> spaceResourceService.updateSpaceResource(request, 99L));
        verify(spaceResourceRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateSpaceResource_WhenNameTakenByAnotherSpace_ThrowsSpaceResourceAlreadyExistsException() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("Taken Name", "Some description", 1, null);
        SpaceResource existing = buildSpaceResource(5L, "Old Name", SpaceTypeResource.CAFE, null);
        SpaceResource mapped = buildSpaceResource(null, "Taken Name", SpaceTypeResource.CAFE, null);
        when(spaceResourceRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(mapped);
        when(spaceResourceRepository.existsByNameIgnoreCaseAndIdNot("Taken Name", 5L)).thenReturn(true);

        // When / Then
        assertThrows(SpaceResourceAlreadyExistsException.class,
                () -> spaceResourceService.updateSpaceResource(request, 5L));
        assertEquals("Old Name", existing.getName());
        verify(spaceResourceRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateSpaceResource_WhenNameTakenByAnotherSpaceWithDifferentCase_ThrowsSpaceResourceAlreadyExistsException() {
        // Given
        SpaceRequestDTO request = buildRequestDTO("TAKEN name", "Some description", 1, null);
        SpaceResource existing = buildSpaceResource(5L, "Old Name", SpaceTypeResource.CAFE, null);
        SpaceResource mapped = buildSpaceResource(null, "TAKEN name", SpaceTypeResource.CAFE, null);
        when(spaceResourceRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(mapped);
        when(spaceResourceRepository.existsByNameIgnoreCaseAndIdNot("TAKEN name", 5L)).thenReturn(true);

        // When / Then
        assertThrows(SpaceResourceAlreadyExistsException.class,
                () -> spaceResourceService.updateSpaceResource(request, 5L));
        verify(spaceResourceRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateSpaceResource_WhenSaveViolatesUniqueConstraint_ThrowsSpaceResourceAlreadyExistsException() {
        // Given: a concurrent request took the name after the existsByNameIgnoreCaseAndIdNot check
        SpaceRequestDTO request = buildRequestDTO("Race Cafe", "Some description", 1, null);
        SpaceResource existing = buildSpaceResource(5L, "Old Name", SpaceTypeResource.CAFE, null);
        SpaceResource mapped = buildSpaceResource(null, "Race Cafe", SpaceTypeResource.CAFE, null);
        when(spaceResourceRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(spaceResourceMapper.toSpaceResource(request)).thenReturn(mapped);
        when(spaceResourceRepository.existsByNameIgnoreCaseAndIdNot("Race Cafe", 5L)).thenReturn(false);
        when(spaceResourceRepository.saveAndFlush(existing))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

        // When / Then
        SpaceResourceAlreadyExistsException ex = assertThrows(SpaceResourceAlreadyExistsException.class,
                () -> spaceResourceService.updateSpaceResource(request, 5L));
        assertEquals("Space with name 'Race Cafe' already exists", ex.getMessage());
        verify(spaceResourceMapper, never()).toSpaceResponseDTO(any());
    }

    // --- deleteSpaceResource ---

    @Test
    void deleteSpaceResource_WhenFound_DeletesSpace() {
        // Given
        SpaceResource space = buildSpaceResource(7L, "The Hub", SpaceTypeResource.HOT_DESK, null);
        when(spaceResourceRepository.findById(7L)).thenReturn(Optional.of(space));

        // When
        spaceResourceService.deleteSpaceResource(7L);

        // Then
        verify(spaceResourceRepository).delete((SpaceResource) space);
    }

    @Test
    void deleteSpaceResource_WhenNotFound_ThrowsSpaceResourceNotFoundException() {
        // Given
        when(spaceResourceRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(SpaceResourceNotFoundException.class,
                () -> spaceResourceService.deleteSpaceResource(99L));
        verify(spaceResourceRepository, never()).delete(any(SpaceResource.class));
    }

    // --- Helpers ---

    private SpaceResource buildSpaceResource(
            Long id, String name, SpaceTypeResource type, String wifiPassword)
    {
        return SpaceResource.builder()
                .id(id)
                .name(name)
                .description("A nice place to work")
                .type(type)
                .wifiPassword(wifiPassword)
                .build();
    }

    private SpaceResponseDTO buildResponseDTO(Long id, String name, SpaceTypeResource type, String wifiPassword) {
        return SpaceResponseDTO.builder()
                .id(id)
                .name(name)
                .description("A nice place to work")
                .type(type)
                .wifiPassword(wifiPassword)
                .build();
    }

    private SpaceRequestDTO buildRequestDTO(
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
