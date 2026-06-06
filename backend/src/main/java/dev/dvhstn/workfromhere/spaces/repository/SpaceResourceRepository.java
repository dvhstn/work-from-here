package dev.dvhstn.workfromhere.spaces.repository;

import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpaceResourceRepository extends JpaRepository<SpaceResource, Long>,
        JpaSpecificationExecutor<SpaceResource> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
