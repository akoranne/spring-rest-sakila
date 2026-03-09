package com.example.app.services.rental.service;

import com.example.app.services.rental.domain.dto.RentalDto;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RentalService {

    List<RentalDto.Rental> getRentalList(Pageable pageable);

    Optional<RentalDto.Rental> getRental(Integer rentalId);

    BigDecimal getOverdueRentalList();

    BigDecimal getOverdueRental(Integer customerId);

    RentalDto.Rental rentDvd(RentalDto.RentalCreateRequest model);

    RentalDto.Rental returnDvd(Map<String, String> model);

    RentalDto.Rental updateRental(Integer rentalId, RentalDto.RentalUpdateRequest model);

    void deleteRental(Integer rentalId);
}
