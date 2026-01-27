package dev.dvhstn.workfromhere.spaces.controller;

import dev.dvhstn.workfromhere.spaces.model.SpaceResource;
import dev.dvhstn.workfromhere.spaces.service.SpaceResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/spaces")
public class SpaceResourceController {

    private final SpaceResourceService spaceResourceService;

    public SpaceResourceController(SpaceResourceService spaceResourceService) {
        this.spaceResourceService = spaceResourceService;
    }

    @GetMapping()
    public ResponseEntity<List<SpaceResource>> getAllSpaces() {
        return ResponseEntity.ok(spaceResourceService.getAllSpaces());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SpaceResource> getSpaceById(@PathVariable Long id) {
        return ResponseEntity.ok(spaceResourceService.getSpaceResourceById(id));
    }

    @PostMapping()
    public ResponseEntity<SpaceResource> createSpaceResource(@RequestBody SpaceResource spaceResource) {

        spaceResourceService.createSpaceResource(spaceResource);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(spaceResource.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(spaceResource);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateSpaceResource(
            @PathVariable Long id, @RequestBody SpaceResource updatedSpaceResource)
    {
        spaceResourceService.updateSpaceResource(updatedSpaceResource, id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteSpaceResource(@PathVariable Long id) {
        spaceResourceService.deleteSpaceResource(id);

        return ResponseEntity.noContent().build();
    }
}
