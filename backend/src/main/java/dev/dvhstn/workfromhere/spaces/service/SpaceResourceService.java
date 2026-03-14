package dev.dvhstn.workfromhere.spaces.service;

import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceAlreadyExistsException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceNotFoundException;
import dev.dvhstn.workfromhere.spaces.mapper.SpaceResourceMapper;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import dev.dvhstn.workfromhere.spaces.repository.SpaceResourceRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class SpaceResourceService {
    private final SpaceResourceMapper spaceResourceMapper;
    private final SpaceResourceRepository spaceResourceRepository;

    public SpaceResourceService(SpaceResourceMapper spaceResourceMapper, SpaceResourceRepository spaceResourceRepository) {
        this.spaceResourceMapper = spaceResourceMapper;
        this.spaceResourceRepository = spaceResourceRepository;
    }

    public Page<SpaceResponseDTO> getAllSpaces(Pageable pageable) {
        return spaceResourceRepository.findAll(pageable)
                .map(spaceResourceMapper::toSpaceResponseDTO);
    }

    public SpaceResponseDTO getSpaceResourceById(Long id) {
        SpaceResource resource = spaceResourceRepository.findById(id)
                .orElseThrow(() -> new SpaceResourceNotFoundException("Space with id " + id + " not found"));
        return spaceResourceMapper.toSpaceResponseDTO(resource);
    }

    @Transactional
    public SpaceResponseDTO createSpaceResource(SpaceRequestDTO spaceResource) {
        if (spaceResourceRepository.existsByName(spaceResource.getName())) {
            throw new SpaceResourceAlreadyExistsException("Space with name '" + spaceResource.getName() + "' already exists");
        }

        SpaceResource mappedSpaceResource = spaceResourceMapper.toSpaceResource(spaceResource);
        try {
            spaceResourceRepository.saveAndFlush(mappedSpaceResource);
        } catch (DataIntegrityViolationException e) {
            throw new SpaceResourceAlreadyExistsException("Space with name '" + spaceResource.getName() + "' already exists");
        }

        return spaceResourceMapper.toSpaceResponseDTO(mappedSpaceResource);
    }

    @Transactional
    public void updateSpaceResource(SpaceRequestDTO updatedSpaceResource, Long id) {
        SpaceResource originalSpaceResource = spaceResourceRepository.findById(id)
                .orElseThrow(() -> new SpaceResourceNotFoundException("Space with id " + id + " not found"));

        if (spaceResourceRepository.existsByNameAndIdNot(updatedSpaceResource.getName(), id)) {
            throw new SpaceResourceAlreadyExistsException("Space with name '" + updatedSpaceResource.getName() + "' already exists");
        }

        updateSpace(updatedSpaceResource, originalSpaceResource);

        try {
            spaceResourceRepository.saveAndFlush(originalSpaceResource);
        } catch (DataIntegrityViolationException e) {
            throw new SpaceResourceAlreadyExistsException("Space with name '" + updatedSpaceResource.getName() + "' already exists");
        }
    }

    @Transactional
    public void deleteSpaceResource(Long id) {
        SpaceResource spaceToDelete = spaceResourceRepository.findById(id)
                .orElseThrow(() -> new SpaceResourceNotFoundException("Space with id " + id + " not found"));
        spaceResourceRepository.delete(spaceToDelete);
    }

    public void updateSpace(SpaceRequestDTO updatedSpaceResource, SpaceResource originalSpaceResource) {
        if (Objects.isNull(updatedSpaceResource)) {
            throw new SpaceResourceNotFoundException("Space resource not found");
        }

        originalSpaceResource.setName(updatedSpaceResource.getName());
        originalSpaceResource.setDescription(updatedSpaceResource.getDescription());
        originalSpaceResource.setType(SpaceTypeResource.getById(updatedSpaceResource.getTypeId()));
        originalSpaceResource.setWifiAvailable(updatedSpaceResource.isWifiAvailable());
        originalSpaceResource.setWifiPassword(updatedSpaceResource.getWifiPassword());
    }
}
