package dev.dvhstn.workfromhere.spaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceFilterDTO {
    private String type;
    private Boolean hasWifi;
}
