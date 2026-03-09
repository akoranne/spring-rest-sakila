package com.example.app.services.rental.repository.custom;

import com.example.app.services.rental.domain.entity.RentalEntity;

public interface CustomRentalRepository {
    RentalEntity findRentedDvdRentalId(Integer customerId, Integer inventoryId);
}
