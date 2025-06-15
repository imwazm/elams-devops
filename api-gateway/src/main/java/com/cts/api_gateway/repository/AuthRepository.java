package com.cts.api_gateway.repository;

import com.cts.api_gateway.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByEmployeeEmail(String email);
}
