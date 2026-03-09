package com.example.app.services.rental.service;

import com.example.app.common.exception.ResourceNotAvailableException;
import com.example.app.common.exception.ResourceNotFoundException;
import com.example.app.services.catalog.service.FilmService;
import com.example.app.services.customer.service.CustomerService;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.payment.service.PaymentService;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.rental.domain.mapper.RentalMapper;
import com.example.app.services.rental.repository.RentalRepository;
import com.example.app.services.staff.service.StaffService;
import com.example.app.services.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final StoreService storeService;
    private final PaymentService paymentService;
    private final FilmService filmService;
    private final CustomerService customerService;
    private final StaffService staffService;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto.Rental> getRentalList(Pageable pageable) {
        final var list = rentalRepository.findAll(pageable);
        return rentalMapper.mapToDtoList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RentalDto.Rental> getRental(Integer rentalId) {
        final var entity = rentalRepository.findById(rentalId).orElseThrow(() ->
                new ResourceNotFoundException("Rental not found with id '" + rentalId + "'"));
        return Optional.of(rentalMapper.mapToDto(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getOverdueRentalList() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getOverdueRental(Integer customerId) {
        return null;
    }

    @Override
    @Transactional
    public RentalDto.Rental rentDvd(RentalDto.RentalCreateRequest model) {
        final var stock = storeService.checkInventoryStock(model.getStoreId(), model.getFilmId());
        if (stock.size() == 0) {
            throw new ResourceNotAvailableException("No stock available for film '" + model.getFilmId() + "'");
        }
        final var inventory = stock.get(0);
        final var customer = customerService.getCustomer(model.getCustomerId()).orElseThrow(() ->
                new ResourceNotFoundException("Customer not found with id '" + model.getCustomerId() + "'"));
        final var staff = staffService.getStaff(model.getStaffId()).orElseThrow(() ->
                new ResourceNotFoundException("Staff not found with id '" + model.getStaffId() + "'"));
        final var rental = RentalDto.Rental.builder()
                .rentalDate(model.getRentalDate())
                .inventoryId(inventory.getInventoryId())
                .customerId(model.getCustomerId())
                .staffId(model.getStaffId())
                .inventoryByInventoryId(inventory)
                .customerByCustomerId(customer)
                .staffByStaffId(staff)
                .build();
        final var savedRental = rentalRepository.save(rentalMapper.mapToEntity(rental));

        final var price = filmService.getFilmRentalPrice(model.getFilmId());
        final var payment = PaymentDto.PaymentRequest.builder()
                .rentalId(savedRental.getRentalId())
                .customerId(savedRental.getCustomerId())
                .staffId(savedRental.getStaffId())
                .amount(price)
                .paymentDate(LocalDateTime.now())
                .build();
        final var paymentResult = paymentService.createPayment(payment, customer, staff, rentalMapper.mapToDto(savedRental));

        return rentalMapper.mapToDto(savedRental);
    }

    @Override
    @Transactional
    public RentalDto.Rental returnDvd(Map<String, String> model) {
        final var customerId = Integer.parseInt(model.get("customerId"));
        final var inventoryId = Integer.parseInt(model.get("inventoryId"));
        final var returnDate = LocalDateTime.parse(model.get("returnDate"));
        final var rental = rentalRepository.findRentedDvdRentalId(customerId, inventoryId);
        if (rental == null) {
            throw new ResourceNotAvailableException("No rented dvd found for customer '" + customerId
                    + "' and inventory '" + inventoryId + "'");
        }
        final var newRental = RentalDto.Rental.builder()
                .rentalId(rental.getRentalId())
                .rentalDate(rental.getRentalDate())
                .inventoryId(rental.getInventoryId())
                .customerId(rental.getCustomerId())
                .staffId(rental.getStaffId())
                .returnDate(returnDate)
                .build();
        rental.update(rentalMapper.mapToEntity(newRental));
        return rentalMapper.mapToDto(rental);
    }

    @Override
    @Transactional
    public RentalDto.Rental updateRental(Integer rentalId, RentalDto.RentalUpdateRequest model) {
        final var entity = rentalRepository.findById(rentalId).orElseThrow(() ->
                new ResourceNotFoundException("Rental not found with id '" + rentalId + "'"));
        entity.update(rentalMapper.mapToEntity(model));
        return rentalMapper.mapToDto(entity);
    }

    @Override
    @Transactional
    public void deleteRental(Integer rentalId) {
        rentalRepository.deleteById(rentalId);
    }
}
