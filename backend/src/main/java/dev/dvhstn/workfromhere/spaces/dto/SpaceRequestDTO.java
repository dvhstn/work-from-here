package dev.dvhstn.workfromhere.spaces.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpaceRequestDTO {
    private String name;
    private String description;
    private Integer typeId;
    private boolean wifiAvailable;
    private String wifiPassword;
}
