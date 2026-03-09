package com.example.app.services.staff.repository.custom;

import com.example.app.services.staff.domain.dto.StaffDetailsDto;

import java.util.List;
import java.util.Optional;

public interface CustomStaffRepository {
    List<StaffDetailsDto.StaffDetails> findAllStaffDetailsList();

    Optional<StaffDetailsDto.StaffDetails> findStaffDetailsById(Integer staffId);
}
