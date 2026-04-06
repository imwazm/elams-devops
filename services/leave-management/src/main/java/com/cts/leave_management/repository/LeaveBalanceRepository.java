package com.cts.leave_management.repository;

import com.cts.leave_management.entity.LeaveBalance;
import com.cts.leave_management.entity.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {


    List<LeaveBalance> findByEmployeeId(Long employeeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveType(Long employeeId, LeaveType leaveType);
}