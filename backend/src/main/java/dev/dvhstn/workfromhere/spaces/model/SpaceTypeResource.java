package dev.dvhstn.workfromhere.spaces.model;

import lombok.Getter;
import dev.dvhstn.workfromhere.spaces.exception.SpaceTypeNotFoundException;

@Getter
public enum SpaceTypeResource {
    CAFE(1, "Cafe"),
    HOT_DESK(2, "Hot Desk");

    private final Integer id;
    private final String name;

    SpaceTypeResource(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SpaceTypeResource getById(Integer id) {
        if (id == null) {
            throw new SpaceTypeNotFoundException("Space type id is required");
        }
        for (SpaceTypeResource spaceTypeResource : SpaceTypeResource.values()) {
            if (spaceTypeResource.getId().equals(id)) {
                return spaceTypeResource;
            }
        }
        throw new SpaceTypeNotFoundException("Unknown space type id: " + id);
    }
}
