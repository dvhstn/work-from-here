package dev.dvhstn.workfromhere.spaces.service;

import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.mapper.SpaceResourceMapper;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import dev.dvhstn.workfromhere.spaces.repository.SpaceResourceRepository;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;

@Service
public class SpaceResourceService {
    private final SpaceResourceMapper spaceResourceMapper;
    private final SpaceResourceRepository spaceResourceRepository;

    public SpaceResourceService(SpaceResourceMapper spaceResourceMapper, SpaceResourceRepository spaceResourceRepository) {
        this.spaceResourceMapper = spaceResourceMapper;
        this.spaceResourceRepository = spaceResourceRepository;
    }

    public List<SpaceResponseDTO> getAllSpaces() {

        return spaceResourceRepository.findAll().stream()
                .map(spaceResourceMapper::toSpaceResponseDTO)
                .toList();
    }

    public SpaceResponseDTO getSpaceResourceById(Long id) {
        return spaceResourceMapper.toSpaceResponseDTO(spaceResourceRepository.findSpaceById(id));
    }

    public SpaceResponseDTO createSpaceResource(SpaceRequestDTO spaceResource) {
        SpaceResource mappedSpaceResource = spaceResourceMapper.toSpaceResource(spaceResource);
        spaceResourceRepository.save(mappedSpaceResource);

        return spaceResourceMapper.toSpaceResponseDTO(mappedSpaceResource);
    }

    public void updateSpaceResource(SpaceRequestDTO updatedSpaceResource, Long id) {
        SpaceResource originalSpaceResource = spaceResourceRepository.findSpaceById(id);
        updateSpace(updatedSpaceResource, originalSpaceResource);

        spaceResourceRepository.save(originalSpaceResource);
    }

    public void deleteSpaceResource(Long id) {
        SpaceResource spaceToDelete = spaceResourceRepository.findSpaceById(id);

        if  (spaceToDelete != null) {
            spaceResourceRepository.delete(spaceToDelete);
        }
    }

    public static void updateSpace(SpaceRequestDTO updatedSpaceResource, SpaceResource originalSpaceResource) {
        originalSpaceResource.setName(updatedSpaceResource.getName());
        originalSpaceResource.setDescription(updatedSpaceResource.getDescription());
        originalSpaceResource.setType(SpaceTypeResource.getById(updatedSpaceResource.getTypeId()));
        originalSpaceResource.setWifiAvailable(updatedSpaceResource.isWifiAvailable());
        originalSpaceResource.setWifiPassword(updatedSpaceResource.getWifiPassword());
    }
}
