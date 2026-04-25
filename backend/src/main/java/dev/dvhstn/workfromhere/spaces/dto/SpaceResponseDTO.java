package dev.dvhstn.workfromhere.spaces.dto;

import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private SpaceTypeResource type;
    private String wifiPassword;

    public boolean isWifiAvailable() {
        return wifiPassword != null;
    }
}
