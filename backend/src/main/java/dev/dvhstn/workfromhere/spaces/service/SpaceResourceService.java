package dev.dvhstn.workfromhere.spaces.service;

import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceAlreadyExistsException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceNotFoundException;
import dev.dvhstn.workfromhere.spaces.mapper.SpaceResourceMapper;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import dev.dvhstn.workfromhere.spaces.repository.SpaceResourceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpaceResourceService {

    private static final Logger log = LoggerFactory.getLogger(SpaceResourceService.class);
    private final SpaceResourceMapper spaceResourceMapper;
    private final SpaceResourceRepository spaceResourceRepository;

    public Page<SpaceResponseDTO> getAllSpaces(Pageable pageable) {
        log.debug("Fetching all spaces, page: {}", pageable);
        return spaceResourceRepository.findAll(pageable)
                .map(spaceResourceMapper::toSpaceResponseDTO);
    }

    public SpaceResponseDTO getSpaceResourceById(Long id) {
        log.debug("Fetching space by id: {}", id);
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
        spaceResourceRepository.saveAndFlush(mappedSpaceResource);

        log.info("Created space '{}' with id {}", mappedSpaceResource.getName(), mappedSpaceResource.getId());
        return spaceResourceMapper.toSpaceResponseDTO(mappedSpaceResource);
    }

    @Transactional
    public SpaceResponseDTO updateSpaceResource(SpaceRequestDTO updatedSpaceResource, Long id) {
        SpaceResource originalSpaceResource = spaceResourceRepository.findById(id)
                .orElseThrow(() -> new SpaceResourceNotFoundException("Space with id " + id + " not found"));

        if (spaceResourceRepository.existsByNameAndIdNot(updatedSpaceResource.getName(), id)) {
            throw new SpaceResourceAlreadyExistsException("Space with name '" + updatedSpaceResource.getName() + "' already exists");
        }

        updateSpace(updatedSpaceResource, originalSpaceResource);
        spaceResourceRepository.saveAndFlush(originalSpaceResource);

        log.info("Updated space id {}", id);
        return spaceResourceMapper.toSpaceResponseDTO(originalSpaceResource);
    }

    @Transactional
    public void deleteSpaceResource(Long id) {
        SpaceResource spaceToDelete = spaceResourceRepository.findById(id)
                .orElseThrow(() -> new SpaceResourceNotFoundException("Space with id " + id + " not found"));
        spaceResourceRepository.delete(spaceToDelete);
        log.info("Deleted space '{}' with id {}", spaceToDelete.getName(), id);
    }

    private void updateSpace(SpaceRequestDTO updatedSpaceResource, SpaceResource originalSpaceResource) {
        originalSpaceResource.setName(updatedSpaceResource.getName());
        originalSpaceResource.setDescription(updatedSpaceResource.getDescription());
        originalSpaceResource.setType(SpaceTypeResource.getById(updatedSpaceResource.getTypeId()));
        originalSpaceResource.setWifiPassword(updatedSpaceResource.getWifiPassword());
    }
}
