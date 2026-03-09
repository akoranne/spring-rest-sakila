package com.example.app.services.location.controller;

import com.example.app.services.auth.domain.vo.UserRole;
import com.example.app.services.location.assembler.AddressRepresentationModelAssembler;
import com.example.app.services.location.assembler.CityRepresentationModelAssembler;
import com.example.app.services.location.domain.dto.AddressDto;
import com.example.app.services.location.domain.dto.CityDto;
import com.example.app.services.location.service.LocationService;
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
@RequestMapping(value = "/location")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    private final AddressRepresentationModelAssembler addressAssembler;
    private final CityRepresentationModelAssembler cityAssembler;

    @GetMapping(path = "/addresses")
    @Secured(UserRole.Constants.ROLE_READ)
    public ResponseEntity<CollectionModel<AddressDto.AddressResponse>> getAddressList(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(addressAssembler.toCollectionModel(
                locationService.getAddressList(pageable)));
    }

    @PostMapping(path = "/addresses")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> addAddress(@RequestBody AddressDto.AddressRequest model) {
        final var result = locationService.addAddress(model);
        return ResponseEntity.created(linkTo(methodOn(LocationController.class)
                .getAddress(result.getAddressId())).toUri()).build();
    }

    @GetMapping(path = "/addresses/{addressId}")
    @Secured(UserRole.Constants.ROLE_READ)
    public ResponseEntity<AddressDto.AddressResponse> getAddress(@PathVariable Integer addressId) {
        return locationService.getAddress(addressId)
                .map(addressAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/addresses/{addressId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> updateAddress(@PathVariable Integer addressId,
                                              @RequestBody AddressDto.AddressRequest model) {
        final var result = locationService.updateAddress(addressId, model);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/addresses/{addressId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer addressId) {
        locationService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/addresses/{addressId}/details")
    @Secured(UserRole.Constants.ROLE_READ)
    public ResponseEntity<AddressDto.AddressResponse> getAddressDetails(@PathVariable Integer addressId) {
        return locationService.getAddressDetails(addressId)
                .map(addressAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/cities")
    @Secured(UserRole.Constants.ROLE_READ)
    public ResponseEntity<CollectionModel<CityDto.CityResponse>> getCityList(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(cityAssembler.toCollectionModel(
                locationService.getCityList(pageable)));
    }

    @PostMapping(path = "/cities")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> addCity(@RequestBody CityDto.CityRequest model) {
        final var result = locationService.addCity(model);
        return ResponseEntity.created(linkTo(methodOn(LocationController.class)
                .getCity(result.getCityId())).toUri()).build();
    }

    @GetMapping(path = "/cities/{cityId}")
    @Secured(UserRole.Constants.ROLE_READ)
    public ResponseEntity<CityDto.CityResponse> getCity(@PathVariable Integer cityId) {
        return locationService.getCity(cityId)
                .map(cityAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/cities/{cityId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> updateCity(@PathVariable Integer cityId,
                                           @RequestBody CityDto.CityRequest model) {
        final var result = locationService.updateCity(cityId, model);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/cities/{cityId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> deleteCity(@PathVariable Integer cityId) {
        locationService.deleteCity(cityId);
        return ResponseEntity.noContent().build();
    }
}
