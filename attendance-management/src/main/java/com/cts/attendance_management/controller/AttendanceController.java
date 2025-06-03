package com.cts.attendance_management.controller;

import com.cts.attendance_management.dto.AttendanceClockInRequestDto;
import com.cts.attendance_management.dto.AttendanceClockOutRequestDto;
import com.cts.attendance_management.dto.AttendanceResponseDto;
import com.cts.attendance_management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public List<AttendanceResponseDto> getAllAttendance(){
        return attendanceService.findAllAttendance();
    }

    @PostMapping("clock-in")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponseDto clockIn(@RequestBody AttendanceClockInRequestDto attendanceClockInRequestDto){
        return attendanceService.clockIn(attendanceClockInRequestDto);
    }

    @PostMapping("clock-out")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponseDto clockOut(@RequestBody AttendanceClockOutRequestDto attendanceClockOutRequestDto){
        return attendanceService.clockOut(attendanceClockOutRequestDto);
    }

    @DeleteMapping("{id}/delete")
    public void deleteAttendance(@PathVariable Long id){
        attendanceService.deleteAttendance(id);
    }

    @GetMapping("{id}")
    public AttendanceResponseDto getById(@PathVariable Long id){
        return attendanceService.findAttendanceById(id);
    }
}
