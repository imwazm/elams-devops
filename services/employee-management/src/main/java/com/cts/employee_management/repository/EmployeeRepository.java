package com.cts.employee_management.repository;

import com.cts.employee_management.dto.EmployeeResponseDto;
import com.cts.employee_management.entity.Employee;
import com.cts.employee_management.entity.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByShiftType(ShiftType shiftType);

    Optional<Employee> findByEmail(String email);
}