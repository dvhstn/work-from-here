package dev.dvhstn.workfromhere.spaces.mapper;

import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import org.springframework.stereotype.Component;

@Component
public class SpaceResourceMapper {
    public SpaceResponseDTO toSpaceResponseDTO(SpaceResource space) {
        return SpaceResponseDTO.builder()
                .id(space.getId())
                .name(space.getName())
                .description(space.getDescription())
                .type(space.getType())
                .wifiPassword(space.getWifiPassword())
                .build();
    }

    public SpaceResource toSpaceResource(SpaceRequestDTO spaceRequestDTO) {
        return SpaceResource.builder()
                .name(trim(spaceRequestDTO.getName()))
                .description(trim(spaceRequestDTO.getDescription()))
                .type(SpaceTypeResource.getById(spaceRequestDTO.getTypeId()))
                .wifiPassword(trim(spaceRequestDTO.getWifiPassword()))
                .build();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
