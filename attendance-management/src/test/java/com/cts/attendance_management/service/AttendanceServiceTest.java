package com.cts.attendance_management.service;

import com.cts.attendance_management.dto.AttendanceClockInRequestDto;
import com.cts.attendance_management.dto.AttendanceClockOutRequestDto;
import com.cts.attendance_management.dto.AttendanceResponseDto;
import com.cts.attendance_management.entity.Attendance;
import com.cts.attendance_management.entity.enums.AttendanceStatus;
import com.cts.attendance_management.exception.AttendanceRegisterException;
import com.cts.attendance_management.exception.ResourceNotFoundException;
import com.cts.attendance_management.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
public class AttendanceServiceTest {

    @MockitoBean
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AttendanceService attendanceService;

    private Attendance savedClockInAttendance, savedClockOutAttendance;

    private AttendanceClockInRequestDto attendanceClockInRequestDto,
            attendanceClockInWithoutClockInOutRequestDto;

    private AttendanceClockOutRequestDto attendanceClockOutRequestDto,
            attendanceClockOutWithoutClockInOutRequestDto;

    private AttendanceResponseDto expectedClockInAttendanceResponse, expectedClockOutAttendanceResponse,
            expectedClockOutWithTotalHoursAttendanceResponse;



    // --- LocalTime (Time without date or timezone) ---
    public final LocalTime MORNING_8_AM = LocalTime.of(8, 0, 0);
    public final LocalTime NOON_12_PM = LocalTime.of(12, 0, 0);
    public final LocalTime EVENING_6_PM = LocalTime.of(18, 0, 0);
    public final LocalTime MIDNIGHT_12_AM = LocalTime.of(0, 0, 0);
    public final LocalTime ALMOST_MIDNIGHT = LocalTime.of(23, 59, 59);
    public final LocalTime START_OF_DAY = LocalTime.MIN; // 00:00:00
    public final LocalTime END_OF_DAY = LocalTime.MAX;   // 23:59:59.999999999

    // --- LocalDate (Date without time or timezone) ---
    public final LocalDate TODAY = LocalDate.now();
    public final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
    public final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    public final LocalDate FIRST_DAY_OF_MONTH = LocalDate.now().withDayOfMonth(1);
    public final LocalDate END_OF_MONTH = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
    public final LocalDate LEAP_YEAR_DATE = LocalDate.of(2024, Month.FEBRUARY, 29); // For leap year specific tests
    public final LocalDate NON_LEAP_YEAR_DATE = LocalDate.of(2023, Month.FEBRUARY, 28);
    public final LocalDate NEW_YEARS_DAY = LocalDate.of(2025, Month.JANUARY, 1);
    public final LocalDate LAST_DAY_OF_YEAR = LocalDate.of(2025, Month.DECEMBER, 31);
    public final LocalDate PAST_DATE = LocalDate.of(2020, Month.JANUARY, 15);
    public final LocalDate FUTURE_DATE = LocalDate.of(2030, Month.MARCH, 20);

    @BeforeEach
    void setup(){
        attendanceClockInWithoutClockInOutRequestDto = new AttendanceClockInRequestDto(null, null, 1L);
        attendanceClockOutWithoutClockInOutRequestDto = new AttendanceClockOutRequestDto(null, null, 1L);
        attendanceClockInRequestDto = new AttendanceClockInRequestDto(MORNING_8_AM, TODAY, 1L);
        attendanceClockOutRequestDto = new AttendanceClockOutRequestDto(EVENING_6_PM, TODAY, 1L);
        savedClockInAttendance = new Attendance(1L, MORNING_8_AM, null,
                0, TODAY, null, 1L);
        savedClockOutAttendance = new Attendance(1L, MORNING_8_AM, EVENING_6_PM,
                10.0, TODAY, AttendanceStatus.PRESENT, 1L);
        expectedClockInAttendanceResponse = new AttendanceResponseDto(1L, MORNING_8_AM, null,
                0, TODAY, null, 1L);
        expectedClockOutAttendanceResponse = new AttendanceResponseDto(1L, MORNING_8_AM, EVENING_6_PM,
                10.0, TODAY, AttendanceStatus.PRESENT, 1L);
    }

    @Test
    void punchIn_shouldRegisterClockInTime(){
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(savedClockInAttendance);

        assertThat(attendanceService.clockIn(attendanceClockInRequestDto),
                is(expectedClockInAttendanceResponse));
    }

    @Test
    void clockIn_shouldThrowErrorIfEmployeePunchInTwiceADay(){
        when(attendanceRepository.findByEmployeeIdAndDate(1L, TODAY))
                .thenReturn(Optional.of(savedClockInAttendance));
        assertThrows(AttendanceRegisterException.class,
                ()->attendanceService.clockIn(attendanceClockInRequestDto));
    }

    @Test
    void clock_out_shouldRegisterClockOutTime(){
        when(attendanceRepository.findByEmployeeIdAndDate(1L, TODAY))
                .thenReturn(Optional.of(savedClockInAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(savedClockOutAttendance);

        AttendanceResponseDto savedAttendanceResponseDto
                = attendanceService.clockOut(attendanceClockOutRequestDto);
        assertThat(savedAttendanceResponseDto, is(expectedClockOutAttendanceResponse));
    }

    @Test
    void clockOut_shouldThrowErrorIfEmployeePunchOutTwiceADay(){
        when(attendanceRepository.findByEmployeeIdAndDate(1L, TODAY))
                .thenReturn(Optional.of(savedClockOutAttendance));
        assertThrows(AttendanceRegisterException.class,
                ()->attendanceService.clockOut(attendanceClockOutRequestDto));
    }

    @Test
    void clockOut_shouldThrowErrorIfClockOutWithOutClockIn(){
        when(attendanceRepository.findByEmployeeIdAndDate(1L, TODAY))
                .thenReturn(Optional.empty());
        assertThrows(AttendanceRegisterException.class,
                ()->attendanceService.clockOut(attendanceClockOutRequestDto));
    }

    @Test
    void getAttendanceById_ShouldReturnTheAttendanceRecord(){
        Long id = 1L;
        when(attendanceRepository.findById(id))
                .thenReturn(Optional.of(savedClockOutAttendance));
        AttendanceResponseDto res = attendanceService.findAttendanceById(id);
        assertThat(res, is(expectedClockOutAttendanceResponse));
    }

    @Test
    void getAttendanceById_shouldThroeExceptionForInvalidId(){
        Long id = 99L;
        when(attendanceRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()->attendanceService.findAttendanceById(id));
    }

    @Test
    void getAllAttendance_shouldReturnListOfAttendance(){
        List<AttendanceResponseDto> attendanceResponseList = List.of(
                expectedClockInAttendanceResponse,
                expectedClockOutAttendanceResponse
        );

        List<Attendance> attendanceList = List.of(
                savedClockInAttendance, savedClockOutAttendance
        );
        when(attendanceRepository.findAll())
                .thenReturn(attendanceList);

        assertThat(attendanceService.findAllAttendance(), is(attendanceResponseList));
    }

}
