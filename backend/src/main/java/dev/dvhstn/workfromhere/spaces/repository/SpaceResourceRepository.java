package dev.dvhstn.workfromhere.spaces.repository;

import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceResourceRepository extends JpaRepository<SpaceResource, Integer> {
    public SpaceResource findSpaceById(Long spaceId);
}
