package dev.dvhstn.workfromhere.spaces.controller;

import dev.dvhstn.workfromhere.spaces.dto.SpaceRequestDTO;
import dev.dvhstn.workfromhere.spaces.dto.SpaceResponseDTO;
import dev.dvhstn.workfromhere.spaces.service.SpaceResourceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<List<SpaceResponseDTO>> getAllSpaces() {
        return ResponseEntity.ok(spaceResourceService.getAllSpaces());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SpaceResponseDTO> getSpaceById(@PathVariable Long id) {
        return ResponseEntity.ok(spaceResourceService.getSpaceResourceById(id));
    }

    @PostMapping()
    public ResponseEntity<SpaceResponseDTO> createSpaceResource(@Valid @RequestBody SpaceRequestDTO spaceResource) {

        SpaceResponseDTO responseDTO = spaceResourceService.createSpaceResource(spaceResource);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(responseDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateSpaceResource(
            @PathVariable Long id, @Valid @RequestBody SpaceRequestDTO updatedSpaceResource)
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
