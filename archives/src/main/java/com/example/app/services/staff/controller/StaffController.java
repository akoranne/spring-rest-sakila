package com.example.app.services.staff.controller;

import com.example.app.services.auth.domain.vo.UserRole;
import com.example.app.services.staff.assembler.StaffDetailsRepresentationModelAssembler;
import com.example.app.services.staff.assembler.StaffRepresentationModelAssembler;
import com.example.app.services.staff.domain.dto.StaffDetailsDto;
import com.example.app.services.staff.domain.dto.StaffDto;
import com.example.app.services.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/staffs")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;
    private final StaffRepresentationModelAssembler staffAssembler;
    private final StaffDetailsRepresentationModelAssembler staffDetailsAssembler;

    @GetMapping(path = "")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<StaffDto.StaffResponse>> getStaffList(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(staffAssembler.toCollectionModel(
                staffService.getStaffList(pageable)));
    }

    @PostMapping(path = "")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> addStaff(@RequestBody StaffDto.StaffRequest model) {
        final var result = staffService.addStaff(model);
        return ResponseEntity.created(linkTo(methodOn(StaffController.class)
                .getStaff(result.getStaffId())).toUri()).build();
    }

    @GetMapping(path = "/{staffId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<StaffDto.StaffResponse> getStaff(@PathVariable Integer staffId) {
        return staffService.getStaff(staffId)
                .map(staffAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{staffId}")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> updateStaff(@PathVariable Integer staffId,
                                            @RequestBody StaffDto.StaffRequest model) {
        final var result = staffService.updateStaff(staffId, model);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{staffId}")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> deleteStaff(@PathVariable Integer staffId) {
        staffService.removeStaff(staffId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{staffId}/details")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<StaffDetailsDto.StaffDetailsResponse> getStaffDetails(@PathVariable Integer staffId) {
        return staffService.getStaffDetails(staffId)
                .map(staffDetailsAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
