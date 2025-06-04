package com.cts.leave_management.repository;

import com.cts.leave_management.entity.Employee;
import com.cts.leave_management.entity.LeaveRequest;
import com.cts.leave_management.entity.enums.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByStatus(LeaveRequestStatus status);
    List<LeaveRequest> findByStatusAndEmployee(LeaveRequestStatus status, Employee employee);
    List<LeaveRequest> findByEmployee(Employee employee);
}