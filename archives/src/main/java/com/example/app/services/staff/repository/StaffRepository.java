package com.example.app.services.staff.repository;

import com.example.app.services.staff.domain.entity.StaffEntity;
import com.example.app.services.staff.repository.custom.CustomStaffRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Integer>, CustomStaffRepository {
}
