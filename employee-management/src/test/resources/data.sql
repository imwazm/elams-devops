-- Insert data for shifts
INSERT INTO shift (start_time, end_time, type) VALUES
('08:00:00', '16:00:00', 'MORNING'),
('16:00:00', '00:00:00', 'EVENING'),
('00:00:00', '08:00:00', 'NIGHT'),
('09:00:00', '17:00:00', 'GENERAL');

-- Insert data for employees with various roles and managers
INSERT INTO employee (employee_name, email, role, shift_id, manager_id) VALUES
('Admin User', 'admin@company.com', 'ADMIN', NULL, NULL),
('Jane Smith', 'jane.smith@company.com', 'MANAGER', 4, NULL),
('David Lee', 'david.lee@company.com', 'MANAGER', 4, NULL),
('Alice Brown', 'alice.brown@company.com', 'EMPLOYEE', 1, 2),
('Bob White', 'bob.white@company.com', 'EMPLOYEE', 2, 2),
('Charlie Green', 'charlie.green@company.com', 'EMPLOYEE', 3, 2),
('Eve Black', 'eve.black@company.com', 'EMPLOYEE', 1, 3),
('Frank Miller', 'frank.miller@company.com', 'EMPLOYEE', 2, 3),
('Grace Adams', 'grace.adams@company.com', 'EMPLOYEE', 4, NULL),
('Hannah Blue', 'hannah.blue@company.com', 'EMPLOYEE', 1, 2),
('Ian Yellow', 'ian.yellow@company.com', 'EMPLOYEE', 2, 3);

-- Insert data for attendance records
INSERT INTO attendance (clock_in_time, clock_out_time, work_hours, date, status, employee_id) VALUES
('08:05:00', '16:00:00', 7.9, '2025-05-27', 'PRESENT', 4),
('08:00:00', '12:00:00', 4.0, '2025-05-28', 'HALF_DAY', 4),
(NULL, NULL, 0.0, '2025-05-29', 'ABSENT', 4),
('08:00:00', '18:00:00', 10.0, '2025-05-30', 'ABNORMAL', 4),
('16:00:00', '00:05:00', 8.0, '2025-05-27', 'PRESENT', 5),
('16:15:00', '00:00:00', 7.75, '2025-05-28', 'PRESENT', 5),
('09:00:00', '17:00:00', 8.0, '2025-05-27', 'PRESENT', 2),
('00:00:00', '08:00:00', 8.0, '2025-05-27', 'PRESENT', 6),
('09:00:00', '17:00:00', 8.0, '2025-05-28', 'PRESENT', 7),
('08:00:00', '16:00:00', 8.0, '2025-06-01', 'PRESENT', 8),
('16:00:00', '00:00:00', 8.0, '2025-06-02', 'PRESENT', 9);

-- Insert data for leave balances
INSERT INTO leave_balance (leave_type, balance, employee_id) VALUES
('SICK_LEAVE', 10, 4),
('CASUAL_LEAVE', 5, 4),
('VACATION_LEAVE', 15, 4),
('SICK_LEAVE', 8, 5),
('LOSS_OF_PAY', 2, 5),
('VACATION_LEAVE', 20, 2),
('CASUAL_LEAVE', 10, 6),
('SICK_LEAVE', 12, 7),
('PATERNITY_LEAVE', 5, 8),
('COMPENSATORY_OFF', 0, 9);

-- Insert data for leave requests
INSERT INTO leave_request (leave_type, start_date, end_date, status, reason, employee_id) VALUES
('SICK_LEAVE', '2025-06-05', '2025-06-05', 'PENDING', 'Fever', 4),
('CASUAL_LEAVE', '2025-06-10', '2025-06-11', 'APPROVED', 'Family event', 4),
('VACATION_LEAVE', '2025-07-01', '2025-07-07', 'REJECTED', 'High workload', 5),
('SICK_LEAVE', '2025-06-15', '2025-06-16', 'APPROVED', 'Cold', 6),
('CASUAL_LEAVE', '2025-06-20', '2025-06-21', 'PENDING', 'Personal work', 7),
('PATERNITY_LEAVE', '2025-06-25', '2025-06-30', 'APPROVED', 'Childbirth', 8),
('COMPENSATORY_OFF', '2025-06-01', '2025-06-01', 'APPROVED', 'Worked on holiday', 9);

-- Insert data for attendance reports
INSERT INTO attendance_report (start_date, end_date, total_present, total_absent, type, employee_id) VALUES
('2025-05-26', '2025-05-30', 3, 1, 'WEEKLY', 4),
('2025-05-01', '2025-05-31', 20, 2, 'MONTHLY', 4),
('2025-01-01', '2025-12-31', 240, 10, 'YEARLY', 2),
('2025-06-01', '2025-06-07', 5, 0, 'WEEKLY', 5),
('2025-06-01', '2025-06-30', 22, 1, 'MONTHLY', 6),
('2025-06-01', '2025-06-30', 20, 2, 'MONTHLY', 8),
('2025-06-01', '2025-06-30', 18, 4, 'MONTHLY', 9);	