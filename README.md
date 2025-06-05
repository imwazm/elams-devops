# Employee Attendance Microservice

This project is a microservices-based system designed to manage employee attendance, leave balances, and shift schedules. It consists of multiple modules, each responsible for a specific functionality. The system is built using Spring Boot and follows a modular architecture to ensure scalability and maintainability.

## Project Modules and Ports

| **Module**             | **Port** |
|-------------------------|----------|
| **API Gateway**         | 9090     |
| **Config Server**       | 8888     |
| **Eureka Server**       | 8761     |
| **Employee Management** | 9191     |
| **Leave Management**    | 9192     |
| **Attendance Management** | 9193   |

---

## API Documentation

### **Employee Management**

| **API Endpoint**                                         | **Functionality**                              | **Description**                                                                 |
|----------------------------------------------------------|------------------------------------------------|---------------------------------------------------------------------------------|
| `POST /api/employees/add-employee`                      | Add a new employee                            | Creates a new employee with the role of `EMPLOYEE`.                            |
| `POST /api/employees/add-manager`                       | Add a new manager                             | Creates a new employee with the role of `MANAGER`.                             |
| `POST /api/employees/add-admin`                         | Add a new admin                               | Creates a new employee with the role of `ADMIN`.                               |
| `GET /api/employees`                                    | Fetch all employees                           | Retrieves a list of all employees in the system.                                |
| `GET /api/employees/{id}`                               | Fetch employee by ID                          | Retrieves details of a specific employee based on their ID.                    |
| `POST /api/employees/{id}/promote`                      | Promote employee                              | Promotes an employee to a higher role (e.g., `EMPLOYEE` to `MANAGER`).         |
| `POST /api/employees/{id}/demote`                       | Demote employee                               | Demotes an employee to a lower role (e.g., `MANAGER` to `EMPLOYEE`).           |
| `POST /api/employees/{id}/delete`                       | Delete an employee                            | Removes an employee from the system.                                            |
| `PUT /api/employees/{id}/update`                        | Update employee details                       | Updates the details of an existing employee.                                    |
| `POST /api/employees/{employeeId}/assign-manager/{managerId}` | Assign manager to employee                   | Assigns a manager to a specific employee.                                       |
| `GET /api/employees/{managerId}/team-members`           | Fetch team members for a manager              | Retrieves a list of employees managed by a specific manager.                   |

---

### **Attendance Management**

| **API Endpoint**                                         | **Functionality**                              | **Description**                                                                 |
|----------------------------------------------------------|------------------------------------------------|---------------------------------------------------------------------------------|
| `GET /api/attendance`                                   | Fetch all attendance records                  | Retrieves a list of all attendance records.                                     |
| `POST /api/attendance/clock-in`                         | Clock in attendance                           | Records the clock-in time for an employee.                                      |
| `POST /api/attendance/clock-out`                        | Clock out attendance                          | Records the clock-out time for an employee.                                     |
| `DELETE /api/attendance/{id}/delete`                    | Delete attendance record                      | Removes an attendance record from the system.                                   |
| `GET /api/attendance/{id}`                              | Fetch attendance by ID                        | Retrieves details of a specific attendance record based on its ID.             |

---

### **Leave Management**

| **API Endpoint**                                         | **Functionality**                              | **Description**                                                                 |
|----------------------------------------------------------|------------------------------------------------|---------------------------------------------------------------------------------|
| `POST /api/leave-balances`                              | Add leave balance                             | Adds a new leave balance for an employee.                                       |
| `GET /api/leave-balances`                               | Fetch all leave balances                      | Retrieves a list of all leave balances.                                         |
| `GET /api/leave-balances/{id}`                          | Fetch leave balance by ID                     | Retrieves details of a specific leave balance based on its ID.                 |
| `GET /api/leave-balances/employee/{employeeId}`         | Fetch leave balances by employee ID           | Retrieves leave balances for a specific employee.                               |
| `PUT /api/leave-balances/{id}`                          | Update leave balance                          | Updates an existing leave balance.                                              |
| `PATCH /api/leave-balances/adjust`                      | Adjust leave balance                          | Adjusts leave balance based on approval or rejection of leave requests.         |
| `DELETE /api/leave-balances/{id}`                       | Delete leave balance                          | Removes a leave balance from the system.                                        |
| `POST /api/leave-balances/initialize/{employeeId}`      | Initialize leave balances for new employee    | Initializes default leave balances for a newly added employee.                 |
| `POST /api/leave-requests`                              | Create leave request                          | Submits a new leave request for an employee.                                    |
| `PUT /api/leave-requests/{leaveId}/status`              | Update leave request status                   | Updates the status of a leave request (e.g., `APPROVED`, `REJECTED`).           |
| `GET /api/leave-requests`                               | Fetch all leave requests                      | Retrieves a list of all leave requests.                                         |
| `GET /api/leave-requests/{leaveId}`                     | Fetch leave request by ID                     | Retrieves details of a specific leave request based on its ID.                 |
| `GET /api/leave-requests/by-status`                     | Fetch leave requests by status                | Retrieves leave requests filtered by their status.                              |
| `GET /api/leave-requests/by-status-and-employee`        | Fetch leave requests by status and employee   | Retrieves leave requests filtered by status and employee ID.                    |

---

### **Shift Management**

| **API Endpoint**                                         | **Functionality**                              | **Description**                                                                 |
|----------------------------------------------------------|------------------------------------------------|---------------------------------------------------------------------------------|
| `PUT /api/shifts/{id}`                                  | Update shift details                          | Updates the details of an existing shift.                                       |
| `GET /api/shifts/all`                                   | Fetch all shifts                              | Retrieves a list of all shifts.                                                 |
| `GET /api/shifts/{id}`                                  | Fetch shift by ID                             | Retrieves details of a specific shift based on its ID.                         |
| `GET /api/shifts`                                       | Fetch shift by type                           | Retrieves details of a shift based on its type (e.g., `MORNING`, `EVENING`).    |

---

## Project Description

This microservices-based project is designed to streamline employee management processes in an organization. It includes the following features:

1. **Employee Management**: Handles CRUD operations for employees, including role assignments and team management.
2. **Attendance Management**: Tracks employee attendance with clock-in and clock-out functionalities.
3. **Leave Management**: Manages leave balances and requests, including approval workflows.
4. **Shift Management**: Manages employee shifts, including updates and retrieval by type or ID.
5. **API Gateway**: Acts as a single entry point for all microservices, enabling routing and load balancing.
6. **Config Server**: Centralized configuration management for all microservices.
7. **Eureka Server**: Service discovery and registration for microservices.

This system is built with scalability and modularity in mind, making it suitable for organizations of various sizes.