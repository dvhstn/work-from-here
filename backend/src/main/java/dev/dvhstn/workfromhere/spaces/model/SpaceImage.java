package dev.dvhstn.workfromhere.spaces.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A space's image, stored as the nullable image_* columns on the spaces table. Only the object key is
 * stored, never a CDN URL; clients join the key with their own CDN base.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SpaceImage {
    @Column(name = "image_key")
    private String key;

    @Column(name = "image_alt")
    private String alt;

    @Column(name = "image_credit")
    private String credit;

    @Column(name = "image_license", length = 100)
    private String license;

    @Column(name = "image_source_url", length = 500)
    private String sourceUrl;
}
