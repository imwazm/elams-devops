INSERT INTO shift (start_time, end_time, type) VALUES
('08:00:00', '16:00:00', 'MORNING'),
('16:00:00', '00:00:00', 'EVENING'),
('00:00:00', '08:00:00', 'NIGHT'),
('09:00:00', '17:00:00', 'GENERAL');

INSERT INTO employee (employee_name, role, shift_id, manager_id) VALUES
('Admin User', 'ADMIN', NULL, NULL),
('Jane Smith', 'MANAGER', 4, NULL),
('David Lee', 'MANAGER', 4, NULL);

INSERT INTO employee (employee_name, role, shift_id, manager_id) VALUES
('Alice Brown', 'EMPLOYEE', 1, 2),
('Bob White', 'EMPLOYEE', 2, 2),
('Charlie Green', 'EMPLOYEE', 3, 2),
('Eve Black', 'EMPLOYEE', 1, 3),
('Frank Miller', 'EMPLOYEE', 2, 3);

INSERT INTO attendance (clock_in_time, clock_out_time, work_hours, date, status, employee_id) VALUES
('08:05:00', '16:00:00', 7.9, '2025-05-27', 'PRESENT', 4),
('08:00:00', '12:00:00', 4.0, '2025-05-28', 'HALF_DAY', 4),
(NULL, NULL, 0.0, '2025-05-29', 'ABSENT', 4),
('08:00:00', '18:00:00', 10.0, '2025-05-30', 'ABNORMAL', 4);

INSERT INTO attendance (clock_in_time, clock_out_time, work_hours, date, status, employee_id) VALUES
('16:00:00', '00:05:00', 8.0, '2025-05-27', 'PRESENT', 5),
('16:15:00', '00:00:00', 7.75, '2025-05-28', 'PRESENT', 5);

INSERT INTO attendance (clock_in_time, clock_out_time, work_hours, date, status, employee_id) VALUES
('09:00:00', '17:00:00', 8.0, '2025-05-27', 'PRESENT', 2);

INSERT INTO leave_balance (leave_type, balance, employee_id) VALUES
('SICK_LEAVE', 10, 4),
('CASUAL_LEAVE', 5, 4),
('VACATION_LEAVE', 15, 4);

INSERT INTO leave_balance (leave_type, balance, employee_id) VALUES
('SICK_LEAVE', 8, 5),
('LOSS_OF_PAY', 2, 5);

INSERT INTO leave_balance (leave_type, balance, employee_id) VALUES
('VACATION_LEAVE', 20, 2);

INSERT INTO leave_request (leave_type, start_date, end_date, status, reason, employee_id) VALUES
('SICK_LEAVE', '2025-06-05', '2025-06-05', 'PENDING', 'Fever', 4),
('CASUAL_LEAVE', '2025-06-10', '2025-06-11', 'APPROVED', 'Family event', 4),
('VACATION_LEAVE', '2025-07-01', '2025-07-07', 'REJECTED', 'High workload', 5);

INSERT INTO attendance_report (start_date, end_date, total_present, total_absent, type, employee_id) VALUES
('2025-05-26', '2025-05-30', 3, 1, 'WEEKLY', 4),
('2025-05-01', '2025-05-31', 20, 2, 'MONTHLY', 4),
('2025-01-01', '2025-12-31', 240, 10, 'YEARLY', 2);