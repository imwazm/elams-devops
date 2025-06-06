package com.cts.employee_management.repository;

import com.cts.employee_management.entity.Shift;
import com.cts.employee_management.entity.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> findByType(ShiftType shiftType);
}