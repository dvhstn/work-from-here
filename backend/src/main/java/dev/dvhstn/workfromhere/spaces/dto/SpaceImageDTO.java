package dev.dvhstn.workfromhere.spaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceImageDTO {
    private String key;
    private String alt;
    private String credit;
    private String license;
    private String sourceUrl;
}
