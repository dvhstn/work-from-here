package dev.dvhstn.workfromhere.spaces.dto;

import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;

public class SpaceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private SpaceTypeResource type;
    private boolean wifiAvailable;
    private String wifiPassword;
}
