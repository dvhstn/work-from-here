package dev.dvhstn.workfromhere.spaces.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpaceRequestDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @NotNull(message = "Type id is required")
    private Integer typeId;

    @NotNull(message = "Wifi Availability is required")
    private boolean wifiAvailable;

    @Size(max = 100, message = "WiFi password must be at most 100 characters")
    private String wifiPassword;

    @AssertTrue(message = "WiFi password is required when WiFi is available")
    @JsonIgnore
    public boolean isWifiPasswordValid() {
        return !wifiAvailable || (wifiPassword != null && !wifiPassword.isBlank());
    }
}
