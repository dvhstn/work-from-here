package dev.dvhstn.workfromhere.spaces.specification;

import dev.dvhstn.workfromhere.spaces.dto.SpaceFilterDTO;
import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.model.SpaceTypeResource;
import org.springframework.data.jpa.domain.Specification;

public class SpaceResourceSpecification {

    private SpaceResourceSpecification() {}

    public static Specification<SpaceResource> withFilter(SpaceFilterDTO filter) {
        Specification<SpaceResource> spec = (root, query, cb) -> cb.conjunction();

        if (filter.getType() != null) {
            SpaceTypeResource spaceType = SpaceTypeResource.getByName(filter.getType());
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), spaceType));
        }

        if (filter.getHasWifi() != null) {
            boolean wifi = filter.getHasWifi();
            spec = spec.and((root, query, cb) -> wifi
                    ? cb.isNotNull(root.get("wifiPassword"))
                    : cb.isNull(root.get("wifiPassword")));
        }

        return spec;
    }
}
