package dev.dvhstn.workfromhere.spaces.dto;

import lombok.Data;

@Data
public class SpaceRequestDTO {
    private String name;
    private String description;
    private Long typeId;
    private boolean wifiAvailable;
    private String wifiPassword;
}
