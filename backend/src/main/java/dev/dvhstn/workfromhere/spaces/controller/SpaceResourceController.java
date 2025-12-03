package dev.dvhstn.workfromhere.spaces.controller;

import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.service.SpaceResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/spaces")
public class SpaceResourceController {

    private SpaceResourceService spaceResourceService;

    public SpaceResourceController(SpaceResourceService spaceResourceService) {
        this.spaceResourceService = spaceResourceService;
    }

    @GetMapping()
    public ResponseEntity<List<SpaceResource>> getAllSpaces() {
        return null;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SpaceResource> getSpaceById(@PathVariable String id) {
        return null;
    }

    @PostMapping()
    public ResponseEntity<SpaceResource> createSpaceResource(@RequestBody SpaceResource spaceResource) {
        return null;
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<SpaceResource> updateSpaceResource(@PathVariable String id, @RequestBody SpaceResource spaceResource) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SpaceResource> deleteSpaceResource(@PathVariable String id) {
        return null;
    }
}
