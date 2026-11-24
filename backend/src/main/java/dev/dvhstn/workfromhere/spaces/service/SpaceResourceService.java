package dev.dvhstn.workfromhere.spaces.service;

import dev.dvhstn.workfromhere.spaces.dto.SpaceFilterDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceAlreadyExistsException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceNotFoundException;
import dev.dvhstn.workfromhere.spaces.mapper.SpaceResourceMapper;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.repository.SpaceResourceRepository;
import dev.dvhstn.workfromhere.spaces.specification.SpaceResourceSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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

    public Page<SpaceResponseDTO> getAllSpaces(Pageable pageable, SpaceFilterDTO filter) {
        log.debug("Fetching spaces with filter: {}, page: {}", filter, pageable);
        return spaceResourceRepository.findAll(SpaceResourceSpecification.withFilter(filter), pageable)
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
        SpaceResource mappedSpaceResource = spaceResourceMapper.toSpaceResource(spaceResource);

        if (spaceResourceRepository.existsByNameIgnoreCase(mappedSpaceResource.getName())) {
            throw new SpaceResourceAlreadyExistsException("Space with name '" + mappedSpaceResource.getName() + "' already exists");
        }

        saveAndFlush(mappedSpaceResource);

        log.info("Created space '{}' with id {}", mappedSpaceResource.getName(), mappedSpaceResource.getId());
        return spaceResourceMapper.toSpaceResponseDTO(mappedSpaceResource);
    }

    @Transactional
    public SpaceResponseDTO updateSpaceResource(SpaceRequestDTO updatedSpaceResource, Long id) {
        SpaceResource originalSpaceResource = spaceResourceRepository.findById(id)
                .orElseThrow(() -> new SpaceResourceNotFoundException("Space with id " + id + " not found"));

        SpaceResource mappedSpaceResource = spaceResourceMapper.toSpaceResource(updatedSpaceResource);

        if (spaceResourceRepository.existsByNameIgnoreCaseAndIdNot(mappedSpaceResource.getName(), id)) {
            throw new SpaceResourceAlreadyExistsException("Space with name '" + mappedSpaceResource.getName() + "' already exists");
        }

        updateSpace(mappedSpaceResource, originalSpaceResource);
        saveAndFlush(originalSpaceResource);

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

    /**
     * The existsByNameIgnoreCase checks above don't stop two concurrent requests claiming the same name, so the
     * database unique constraint is the final guard; surface its violation as a conflict.
     */
    private void saveAndFlush(SpaceResource spaceResource) {
        try {
            spaceResourceRepository.saveAndFlush(spaceResource);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Integrity violation saving space '{}': {}", spaceResource.getName(), ex.getMostSpecificCause().getMessage());
            throw new SpaceResourceAlreadyExistsException("Space with name '" + spaceResource.getName() + "' already exists");
        }
    }

    private void updateSpace(SpaceResource updatedSpaceResource, SpaceResource originalSpaceResource) {
        originalSpaceResource.setName(updatedSpaceResource.getName());
        originalSpaceResource.setDescription(updatedSpaceResource.getDescription());
        originalSpaceResource.setType(updatedSpaceResource.getType());
        originalSpaceResource.setWifiPassword(updatedSpaceResource.getWifiPassword());
    }
}
