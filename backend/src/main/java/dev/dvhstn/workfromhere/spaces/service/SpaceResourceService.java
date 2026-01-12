package dev.dvhstn.workfromhere.spaces.service;

import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.repository.SpaceResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpaceResourceService {
    private final SpaceResourceRepository spaceResourceRepository;

    public SpaceResourceService(SpaceResourceRepository spaceResourceRepository) {
        this.spaceResourceRepository = spaceResourceRepository;
    }

    public List<SpaceResource> getAllSpaces() {
        return spaceResourceRepository.findAll();
    }

    public SpaceResource getSpaceResourceById(Long id) {
        return spaceResourceRepository.findSpaceById(id);
    }

    public void createSpaceResource(SpaceResource spaceResource) {
        spaceResourceRepository.save(spaceResource);
    }

    public SpaceResource updateSpaceResource(SpaceResource updatedSpaceResource, Long id) {
        SpaceResource originalSpaceResource = spaceResourceRepository.findSpaceById(id);
        updateSpace(updatedSpaceResource, originalSpaceResource);

        return  spaceResourceRepository.save(originalSpaceResource);
    }

    public void deleteSpaceResource(Long id) {
        SpaceResource spaceToDelete = spaceResourceRepository.findSpaceById(id);

        if  (spaceToDelete != null) {
            spaceResourceRepository.delete(spaceToDelete);
        }
    }

    private static void updateSpace(SpaceResource updatedSpaceResource, SpaceResource originalSpaceResource) {
        originalSpaceResource.setName(updatedSpaceResource.getName());
        originalSpaceResource.setDescription(updatedSpaceResource.getDescription());
        originalSpaceResource.setType(updatedSpaceResource.getType());
        originalSpaceResource.setWifiAvailable(updatedSpaceResource.isWifiAvailable());
        originalSpaceResource.setWifiPassword(updatedSpaceResource.getWifiPassword());
    }
}
