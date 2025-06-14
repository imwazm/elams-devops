package com.cts.api_gateway.repository;

import com.cts.api_gateway.entity.AuthUser;
import com.cts.api_gateway.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}