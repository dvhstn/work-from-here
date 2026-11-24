package dev.dvhstn.workfromhere.spaces.repository;

import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpaceResourceRepository extends JpaRepository<SpaceResource, Long>,
        JpaSpecificationExecutor<SpaceResource> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
