package org.master.diploma.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.entity.LaboratoryWork;
import org.master.diploma.backend.repository.LaboratoryWorkRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(Constants.Routes.ADMIN_LABS)
@RequiredArgsConstructor
@Tag(name = "Admin Lab Management", description = "Endpoints for teachers to manage laboratory works")
public class AdminLabController {
    private final LaboratoryWorkRepository laboratoryWorkRepository;

    @GetMapping
    @Operation(summary = "Get all laboratory works")
    public List<LaboratoryWork> getAllLabs() {
        return laboratoryWorkRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Create a new laboratory work")
    public LaboratoryWork createLab(@RequestBody LaboratoryWork lab) {
        return laboratoryWorkRepository.save(lab);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a laboratory work")
    public ResponseEntity<LaboratoryWork> updateLab(@PathVariable Long id, @RequestBody LaboratoryWork labDetails) {
        return laboratoryWorkRepository.findById(id)
                .map(lab -> {
                    lab.setNumber(labDetails.getNumber());
                    lab.setTopic(labDetails.getTopic());
                    lab.setDescription(labDetails.getDescription());
                    return ResponseEntity.ok(laboratoryWorkRepository.save(lab));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
