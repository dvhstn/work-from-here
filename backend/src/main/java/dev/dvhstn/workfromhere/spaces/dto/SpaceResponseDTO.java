package dev.dvhstn.workfromhere.spaces.dto;

import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpaceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private SpaceTypeResource type;
    private boolean wifiAvailable;
    private String wifiPassword;
}
