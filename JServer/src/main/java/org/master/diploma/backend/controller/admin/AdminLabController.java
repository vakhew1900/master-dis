package org.master.diploma.backend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.dto.admin.AdminLabDto;
import org.master.diploma.backend.dto.admin.AdminTaskDto;
import org.master.diploma.backend.entity.LaboratoryWork;
import org.master.diploma.backend.service.AdminLabService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.Routes.ADMIN_LABS)
@RequiredArgsConstructor
@Tag(name = "Admin Lab Management", description = "Endpoints for teachers to manage laboratory works")
public class AdminLabController {
    private final AdminLabService adminLabService;

    @GetMapping
    @Operation(summary = "Get all laboratory works")
    public List<AdminLabDto> getAllLabs() {
        return adminLabService.getAllLabs();
    }

    @PostMapping
    @Operation(summary = "Create a new laboratory work")
    public AdminLabDto createLab(@RequestBody LaboratoryWork lab) {
        return adminLabService.createLab(lab);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a laboratory work")
    public ResponseEntity<AdminLabDto> updateLab(@PathVariable Long id, @RequestBody LaboratoryWork labDetails) {
        AdminLabDto updated = adminLabService.updateLab(id, labDetails);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get laboratory work by ID")
    public ResponseEntity<AdminLabDto> getLabById(@PathVariable Long id) {
        AdminLabDto lab = adminLabService.getLabById(id);
        return lab != null ? ResponseEntity.ok(lab) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/tasks")
    @Operation(summary = "Get all tasks for a specific laboratory work")
    public ResponseEntity<List<AdminTaskDto>> getTasksByLabId(@PathVariable Long id) {
        List<AdminTaskDto> tasks = adminLabService.getTasksByLabId(id);
        return !tasks.isEmpty() ? ResponseEntity.ok(tasks) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a laboratory work")
    public ResponseEntity<Void> deleteLab(@PathVariable Long id) {
        adminLabService.deleteLab(id);
        return ResponseEntity.ok().build();
    }
}
