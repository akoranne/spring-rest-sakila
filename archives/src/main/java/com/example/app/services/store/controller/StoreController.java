package com.example.app.services.store.controller;

import com.example.app.services.auth.domain.vo.UserRole;
import com.example.app.services.staff.assembler.StaffRepresentationModelAssembler;
import com.example.app.services.staff.domain.dto.StaffDto;
import com.example.app.services.store.assembler.StoreDetailsRepresentationModelAssembler;
import com.example.app.services.store.assembler.StoreRepresentationModelAssembler;
import com.example.app.services.store.domain.dto.StoreDetailsDto;
import com.example.app.services.store.domain.dto.StoreDto;
import com.example.app.services.store.service.StoreService;
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
@RequestMapping(value = "/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final StoreRepresentationModelAssembler storeAssembler;
    private final StoreDetailsRepresentationModelAssembler storeDetailsAssembler;
    private final StaffRepresentationModelAssembler staffAssembler;

    @GetMapping(path = "")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<StoreDto.StoreResponse>> getStoreList(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(storeAssembler.toCollectionModel(
                storeService.getStoreList(pageable)));
    }

    @PostMapping(path = "")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> addStore(@RequestBody StoreDto.StoreRequest model) {
        final var result = storeService.addStore(model);
        return ResponseEntity.created(linkTo(methodOn(StoreController.class)
                .getStore(result.getStoreId())).toUri()).build();
    }

    @GetMapping(path = "/{storeId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<StoreDto.StoreResponse> getStore(@PathVariable Integer storeId) {
        return storeService.getStore(storeId)
                .map(storeAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{storeId}")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> updateStore(@PathVariable Integer storeId,
                                            @ModelAttribute StoreDto.StoreRequest model) {
        final var result = storeService.updateStore(storeId, model);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{storeId}")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> deleteStore(@PathVariable Integer storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{storeId}/staffs")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<StaffDto.StaffResponse>> getStoreStaffList(@PathVariable Integer storeId) {
        return ResponseEntity.ok(staffAssembler.toCollectionModel(
                storeService.getStoreStaffList(storeId)));
    }

    @GetMapping(path = "/{storeId}/staffs/{staffId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<StaffDto.StaffResponse> getStoreStaff(@PathVariable Integer storeId,
                                                                @PathVariable Integer staffId) {
        return storeService.getStoreStaff(storeId, staffId)
                .map(staffAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/{storeId}/staffs/{staffId}")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> addStoreStaff(@PathVariable Integer storeId,
                                              @PathVariable Integer staffId) {
        final var result = storeService.addStoreStaff(storeId, staffId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{storeId}/staffs/{staffId}")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> updateStoreStaff(@PathVariable Integer storeId,
                                                 @PathVariable Integer staffId) {
        final var result = storeService.updateStoreStaff(storeId, staffId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{storeId}/staffs/{staffId}")
    @Secured(UserRole.Constants.ROLE_ADMIN)
    public ResponseEntity<Void> deleteStoreStaff(@PathVariable Integer storeId,
                                                 @PathVariable Integer staffId) {
        storeService.removeStoreStaff(storeId, staffId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{storeId}/details")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<StoreDetailsDto.StoreDetailsResponse> getStoreDetails(@PathVariable Integer storeId) {
        return storeService.getStoreDetails(storeId)
                .map(storeDetailsAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
