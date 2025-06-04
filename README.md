PORTS

api-gateway = 9090

config-server = 8888

eureka-server = 8761

employee-management = 9191

leave-management = 9192

attendance-management = 9193

### API Tables with Module, Endpoint, Functionality, and Description

| **Module**          | **API Endpoint**                     | **Functionality**                              | **Description**                                                                 |
|----------------------|--------------------------------------|------------------------------------------------|---------------------------------------------------------------------------------|
| **Employee**         | `POST /api/employees/add-employee`  | Add a new employee                            | Creates a new employee with the role of `EMPLOYEE`.                            |
|                      | `POST /api/employees/add-manager`   | Add a new manager                             | Creates a new employee with the role of `MANAGER`.                             |
|                      | `POST /api/employees/add-admin`     | Add a new admin                               | Creates a new employee with the role of `ADMIN`.                               |
|                      | `GET /api/employees`                | Fetch all employees                           | Retrieves a list of all employees in the system.                                |
|                      | `GET /api/employees/{id}`           | Fetch employee by ID                          | Retrieves details of a specific employee based on their ID.                    |
|                      | `POST /api/employees/{id}/promote`  | Promote employee                              | Promotes an employee to a higher role (e.g., `EMPLOYEE` to `MANAGER`).         |
|                      | `POST /api/employees/{id}/demote`   | Demote employee                               | Demotes an employee to a lower role (e.g., `MANAGER` to `EMPLOYEE`).           |
|                      | `POST /api/employees/{id}/delete`   | Delete an employee                            | Removes an employee from the system.                                            |
|                      | `PUT /api/employees/{id}/update`    | Update employee details                       | Updates the details of an existing employee.                                    |
|                      | `POST /api/employees/{employeeId}/assign-manager/{managerId}` | Assign manager to employee                   | Assigns a manager to a specific employee.                                       |
|                      | `GET /api/employees/{managerId}/team-members` | Fetch team members for a manager            | Retrieves a list of employees managed by a specific manager.                   |
| **Attendance**       | `GET /api/attendance`               | Fetch all attendance records                  | Retrieves a list of all attendance records.                                     |
|                      | `POST /api/attendance/clock-in`     | Clock in attendance                           | Records the clock-in time for an employee.                                      |
|                      | `POST /api/attendance/clock-out`    | Clock out attendance                          | Records the clock-out time for an employee.                                     |
|                      | `DELETE /api/attendance/{id}/delete` | Delete attendance record                     | Removes an attendance record from the system.                                   |
|                      | `GET /api/attendance/{id}`          | Fetch attendance by ID                        | Retrieves details of a specific attendance record based on its ID.             |
| **Leave Balance**    | `POST /api/leave-balances`          | Add leave balance                             | Adds a new leave balance for an employee.                                       |
|                      | `GET /api/leave-balances`           | Fetch all leave balances                      | Retrieves a list of all leave balances.                                         |
|                      | `GET /api/leave-balances/{id}`      | Fetch leave balance by ID                     | Retrieves details of a specific leave balance based on its ID.                 |
|                      | `GET /api/leave-balances/employee/{employeeId}` | Fetch leave balances by employee ID          | Retrieves leave balances for a specific employee.                               |
|                      | `PUT /api/leave-balances/{id}`      | Update leave balance                          | Updates an existing leave balance.                                              |
|                      | `PATCH /api/leave-balances/adjust`  | Adjust leave balance                          | Adjusts leave balance based on approval or rejection of leave requests.         |
|                      | `DELETE /api/leave-balances/{id}`   | Delete leave balance                          | Removes a leave balance from the system.                                        |
|                      | `POST /api/leave-balances/initialize/{employeeId}` | Initialize leave balances for new employee | Initializes default leave balances for a newly added employee.                 |
| **Shift**            | `PUT /api/shifts/{id}`              | Update shift details                          | Updates the details of an existing shift.                                       |
|                      | `GET /api/shifts/all`               | Fetch all shifts                              | Retrieves a list of all shifts.                                                 |
|                      | `GET /api/shifts/{id}`              | Fetch shift by ID                             | Retrieves details of a specific shift based on its ID.                         |

This table summarizes the API endpoints, their functionalities, and descriptions for each module in the system.