package com.example.app.services.location.service;

import com.example.app.common.exception.ResourceNotFoundException;
import com.example.app.services.location.domain.dto.AddressDto;
import com.example.app.services.location.domain.dto.CityDto;
import com.example.app.services.location.domain.mapper.AddressMapper;
import com.example.app.services.location.domain.mapper.CityMapper;
import com.example.app.services.location.repository.AddressRepository;
import com.example.app.services.location.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final AddressMapper addressMapper;
    private final CityMapper cityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto.Address> getAddressList(Pageable pageable) {
        final var list = addressRepository.findAll(pageable);
        return addressMapper.mapToDtoList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AddressDto.Address> getAddress(Integer addressId) {
        final var entity = addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException("Address not found with id '" + addressId + "'"));
        return Optional.of(addressMapper.mapToDto(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto.Address> getAddressDetailsList() {
        return addressRepository.findAllAddressDetailsList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AddressDto.Address> getAddressDetails(Integer addressId) {
        final var model = addressRepository.findAddressDetailsById(addressId);
        if (model.isEmpty()) {
            throw new ResourceNotFoundException("Address not found with id '" + addressId + "'");
        }
        return model;
    }

    @Override
    @Transactional
    public AddressDto.Address addAddress(AddressDto.AddressRequest model) {
        final var savedEntity = addressRepository.save(addressMapper.mapToEntity(model));
        return addressMapper.mapToDto(savedEntity);
    }

    @Override
    @Transactional
    public AddressDto.Address updateAddress(Integer addressId, AddressDto.AddressRequest model) {
        final var entity = addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException("Address not found with id '" + addressId + "'"));
        entity.update(addressMapper.mapToEntity(model));
        return addressMapper.mapToDto(entity);
    }

    @Override
    @Transactional
    public void deleteAddress(Integer addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDto.City> getCityList(Pageable pageable) {
        final var list = cityRepository.findAll(pageable);
        return cityMapper.mapToDtoList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CityDto.City> getCity(Integer cityId) {
        final var entity = cityRepository.findById(cityId).orElseThrow(() ->
                new ResourceNotFoundException("City not found with id '" + cityId + "'"));
        return Optional.of(cityMapper.mapToDto(entity));
    }

    @Override
    @Transactional
    public CityDto.City addCity(CityDto.CityRequest model) {
        final var savedEntity = cityRepository.save(cityMapper.mapToEntity(model));
        return cityMapper.mapToDto(savedEntity);
    }

    @Override
    @Transactional
    public CityDto.City updateCity(Integer cityId, CityDto.CityRequest model) {
        final var entity = cityRepository.findById(cityId).orElseThrow(() ->
                new ResourceNotFoundException("City not found with id '" + cityId + "'"));
        entity.update(cityMapper.mapToEntity(model));
        return cityMapper.mapToDto(entity);
    }

    @Override
    @Transactional
    public void deleteCity(Integer cityId) {
        cityRepository.deleteById(cityId);
    }
}
