package com.example.app.services.location.service;

import com.example.app.services.location.domain.dto.AddressDto;
import com.example.app.services.location.domain.dto.CityDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    List<AddressDto.Address> getAddressList(Pageable pageable);

    Optional<AddressDto.Address> getAddress(Integer addressId);

    List<AddressDto.Address> getAddressDetailsList();

    Optional<AddressDto.Address> getAddressDetails(Integer addressId);

    AddressDto.Address addAddress(AddressDto.AddressRequest model);

    AddressDto.Address updateAddress(Integer addressId, AddressDto.AddressRequest model);

    void deleteAddress(Integer addressId);

    List<CityDto.City> getCityList(Pageable pageable);

    Optional<CityDto.City> getCity(Integer cityId);

    CityDto.City addCity(CityDto.CityRequest model);

    CityDto.City updateCity(Integer cityId, CityDto.CityRequest model);

    void deleteCity(Integer cityId);
}
