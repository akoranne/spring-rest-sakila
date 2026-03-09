package com.example.app.services.store.service;

import com.example.app.services.staff.domain.dto.StaffDto;
import com.example.app.services.store.domain.dto.InventoryDto;
import com.example.app.services.store.domain.dto.StoreDetailsDto;
import com.example.app.services.store.domain.dto.StoreDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StoreService {
    List<StoreDto.Store> getStoreList(Pageable pageable);

    Optional<StoreDto.Store> getStore(Integer storeId);

    List<StoreDetailsDto.StoreDetails> getStoreDetailsList();

    Optional<StoreDetailsDto.StoreDetails> getStoreDetails(Integer storeId);

    List<StaffDto.Staff> getStoreStaffList(Integer storeId);

    Optional<StaffDto.Staff> getStoreStaff(Integer storeId, Integer staffId);

    List<InventoryDto.Inventory> checkInventoryStock(Integer storeId, Integer filmId);

    StoreDto.Store addStore(StoreDto.StoreRequest model);

    StaffDto.Staff addStoreStaff(Integer storeId, Integer staffId);

    StoreDto.Store updateStore(Integer storeId, StoreDto.StoreRequest model);

    StaffDto.Staff updateStoreStaff(Integer storeId, Integer staffId);

    void deleteStore(Integer storeId);

    void removeStoreStaff(Integer storeId, Integer staffId);
}
