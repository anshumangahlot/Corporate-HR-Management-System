DROP DATABASE hr_database;
CREATE DATABASE hr_database;
USE hr_database;

-- 1. BASE TABLES

CREATE TABLE Zip_Directory (
    ZipCode INT PRIMARY KEY,
    City VARCHAR(50),
    State VARCHAR(50)
);

CREATE TABLE Department (
    department_id INT PRIMARY KEY,
    d_name VARCHAR(50),
    d_head VARCHAR(50)
);



-- 2. JOB ROLE

CREATE TABLE Job_Role (
    role_id INT PRIMARY KEY,
    designation VARCHAR(50),
    work_hours INT,
    max_bonus FLOAT,
    base_salary FLOAT,
    min_exp INT,
    job_type VARCHAR(50),
    total_leaves INT,
    dept_id INT,
    FOREIGN KEY (dept_id) REFERENCES Department(department_id)
);

-- 3. EMPLOYEE

CREATE TABLE Employee (
    EmpID INT PRIMARY KEY,
    Emp_name VARCHAR(50),
    Gender VARCHAR(10),
    DOB DATE,
    Email VARCHAR(50),
    Street VARCHAR(100),
    ZipCode INT,
    role_id INT NOT NULL,
    department_id INT NOT NULL,

    CONSTRAINT FK_Employee_Zip FOREIGN KEY (ZipCode)
        REFERENCES Zip_Directory(ZipCode),

    CONSTRAINT FK_Employee_Department FOREIGN KEY (department_id)
        REFERENCES Department(department_id),

    CONSTRAINT FK_Employee_Role FOREIGN KEY (role_id)
        REFERENCES Job_Role(role_id)
);
-- users
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    EmpID INT NULL,

    CONSTRAINT FK_users_employee
        FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
);

-- 4. EMPLOYEE RELATED
CREATE TABLE Employee_Phones (
    EmpID INT,
    Phone_Number CHAR(10),

    PRIMARY KEY (EmpID, Phone_Number),

    CONSTRAINT chk_employee_phones_phone_number
        CHECK (Phone_Number REGEXP '^[0-9]{10}$'),

    CONSTRAINT FK_EmployeePhones_Emp FOREIGN KEY (EmpID)
        REFERENCES Employee(EmpID)
);

CREATE TABLE Intern (
    EmpID INT PRIMARY KEY,
    Internship_StartDate DATE,
    Internship_EndDate DATE,
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
);

CREATE TABLE Full_Time (
    EmpID INT PRIMARY KEY,
    PF_No VARCHAR(20),
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
);

-- 7. LEAVE SYSTEM

CREATE TABLE Leave_Request (
    leave_id INT PRIMARY KEY,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20),
    EmpID INT,
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
);

CREATE TABLE Sick_Leave (
    leave_id INT PRIMARY KEY,
    DoctorNote VARCHAR(100),
    FOREIGN KEY (leave_id) REFERENCES Leave_Request(leave_id)
);

CREATE TABLE Casual_Leave (
    leave_id INT PRIMARY KEY,
    Reason VARCHAR(100),
    FOREIGN KEY (leave_id) REFERENCES Leave_Request(leave_id)
);

CREATE TABLE Paid_Leave (
    leave_id INT PRIMARY KEY,
    Balance_Used INT,
    FOREIGN KEY (leave_id) REFERENCES Leave_Request(leave_id)
);

-- 8. ATTENDANCE

CREATE TABLE Attendance_Log (
    att_id INT PRIMARY KEY,
    work_date DATE,
    in_time TIME,
    out_time TIME,
    shift VARCHAR(20),
    remark VARCHAR(100),
    EmpID INT,
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
);

-- 9. PAYROLL

CREATE TABLE Payroll (
    payroll_id INT PRIMARY KEY,
    paydate DATE,
    total_amount FLOAT,
    transaction_id VARCHAR(50),
    EmpID INT,
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
);



-- 10. PROJECTS

CREATE TABLE Projects (
    project_id INT PRIMARY KEY,
    PName VARCHAR(50),
    StartDate DATE,
    EndDate DATE,
    Status VARCHAR(20),
    TeamLead INT NOT NULL,
    dept_id INT NOT NULL,
    FOREIGN KEY (TeamLead) REFERENCES Employee(EmpID),
    FOREIGN KEY (dept_id) REFERENCES Department(department_id)
);

CREATE TABLE Employee_Projects (
    EmpID INT,
    project_id INT,
    assigned_date DATE,
    role_in_project VARCHAR(50),
    hours_per_week INT,
    PRIMARY KEY (EmpID, project_id),
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID),
    FOREIGN KEY (project_id) REFERENCES Projects(project_id)
);


-- 12. MEETING

CREATE TABLE Meeting (
    meeting_id INT PRIMARY KEY,
    m_date DATE,
    m_time TIME,
    topic VARCHAR(100),
    dept_id INT,
    FOREIGN KEY (dept_id) REFERENCES Department(department_id)
);
CREATE TABLE Role_Skills (
    role_id INT,
    skill_name VARCHAR(50),
    PRIMARY KEY (role_id, skill_name),
    FOREIGN KEY (role_id) REFERENCES Job_Role(role_id)
);
-- Procedures

DELIMITER //

CREATE PROCEDURE AddEmployee(
    IN p_id INT,
    IN p_name VARCHAR(50),
    IN p_gender VARCHAR(10),
    IN p_dob DATE,
    IN p_email VARCHAR(50),
    IN p_street VARCHAR(100),
    IN p_zip INT,
    IN p_role INT,
    IN p_dept INT
)
BEGIN
    INSERT INTO Employee
    VALUES (p_id, p_name, p_gender, p_dob, p_email, p_street, p_zip, p_role, p_dept);
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE GetEmployeesByDepartment()
BEGIN
    SELECT 
        e.EmpID,
        e.Emp_name,
        d.d_name,
        ROW_NUMBER() OVER (PARTITION BY d.department_id ORDER BY e.EmpID) AS dept_rank
    FROM Employee e
    JOIN Department d ON e.department_id = d.department_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE GetDepartmentEmployeeCount()
BEGIN
    SELECT 
        d.department_id,
        d.d_name,
        COUNT(e.EmpID) AS total_employees
    FROM Department d
    LEFT JOIN Employee e 
        ON d.department_id = e.department_id
    GROUP BY d.department_id, d.d_name;
END //

DELIMITER ;

-- Triggers

DELIMITER //

CREATE TRIGGER CheckAttendanceTime
BEFORE INSERT ON Attendance_Log
FOR EACH ROW
BEGIN
    IF NEW.out_time <= NEW.in_time THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid attendance: out_time must be after in_time';
    END IF;
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER CheckLeaveDates
BEFORE INSERT ON Leave_Request
FOR EACH ROW
BEGIN
    IF NEW.end_date < NEW.start_date THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid leave: end date cannot be before start date';
    END IF;
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER CheckPayrollAmount
BEFORE INSERT ON Payroll
FOR EACH ROW
BEGIN
    IF NEW.total_amount < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid payroll: total amount cannot be negative';
    END IF;
END //

DELIMITER ;

-- Functions

DELIMITER //

CREATE FUNCTION GetEmployeeAge(p_emp_id INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE age INT;

    SELECT TIMESTAMPDIFF(YEAR, DOB, CURDATE())
    INTO age
    FROM Employee
    WHERE EmpID = p_emp_id;

    RETURN age;
END //

DELIMITER ;

DELIMITER //

CREATE FUNCTION GetDepartmentName(p_emp_id INT)
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE dept_name VARCHAR(50);

    SELECT d.d_name
    INTO dept_name
    FROM Employee e
    JOIN Department d ON e.department_id = d.department_id
    WHERE e.EmpID = p_emp_id;

    RETURN dept_name;
END //

DELIMITER ;

DELIMITER //

CREATE FUNCTION GetApprovedLeaveCount(p_emp_id INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE total INT;

    SELECT COUNT(*)
    INTO total
    FROM Leave_Request
    WHERE EmpID = p_emp_id
      AND status = 'Approved';

    RETURN total;
END //

DELIMITER ;

-- insert data

-- 1. BASE TABLES

INSERT INTO Zip_Directory VALUES
(411001, 'Pune', 'Maharashtra'),
(400001, 'Mumbai', 'Maharashtra'),
(560001, 'Bangalore', 'Karnataka'),
(110001, 'Delhi', 'Delhi');

INSERT INTO Department VALUES
(1, 'IT', 'Rahul'),
(2, 'HR', 'Sneha'),
(3, 'Finance', 'Amit'),
(4, 'Marketing', 'Neha');

-- 2. JOB ROLE (before Employee)

INSERT INTO Job_Role VALUES
(1, 'Developer', 8, 5000, 40000, 1, 'Full-Time', 20, 1),
(2, 'HR Executive', 8, 3000, 30000, 1, 'Full-Time', 18, 2),
(3, 'Accountant', 8, 4000, 35000, 2, 'Full-Time', 18, 3),
(4, 'Marketing Lead', 8, 6000, 45000, 3, 'Full-Time', 20, 4);

-- 3. EMPLOYEE

INSERT INTO Employee VALUES
(101, 'Om', 'Male', '2003-01-01', 'om@gmail.com', 'Street A', 411001, 1, 1),
(102, 'Priya', 'Female', '2002-05-10', 'priya@gmail.com', 'Street B', 400001, 2, 2),
(103, 'Rohit', 'Male', '2001-08-15', 'rohit@gmail.com', 'Street C', 560001, 3, 3),
(104, 'Neha', 'Female', '2000-12-20', 'neha@gmail.com', 'Street D', 110001, 4, 4);

-- 4. USERS (after Employee)

INSERT INTO users (username, password, role, EmpID) VALUES
('admin', 'admin123', 'Admin', NULL),
('om', 'pass1', 'Employee', 101),
('priya', 'pass2', 'Employee', 102),
('rohit', 'pass3', 'Employee', 103),
('neha', 'pass4', 'Employee', 104);

-- 5. EMPLOYEE RELATED

INSERT INTO Employee_Phones VALUES
(101, '9876543210'),
(102, '9123456789'),
(103, '9988776655'),
(104, '9090909090');

INSERT INTO Intern VALUES
(101, '2024-01-01', '2024-06-01');

INSERT INTO Full_Time VALUES
(102, 'PF102'),
(103, 'PF103'),
(104, 'PF104');

-- 6. LEAVE SYSTEM

INSERT INTO Leave_Request VALUES
(1, '2026-04-01', '2026-04-03', 'Approved', 101),
(2, '2026-04-05', '2026-04-06', 'Pending', 102),
(3, '2026-04-07', '2026-04-08', 'Approved', 103),
(4, '2026-04-09', '2026-04-10', 'Rejected', 104);

INSERT INTO Sick_Leave VALUES
(1, 'Fever'),
(3, 'Cold');

INSERT INTO Casual_Leave VALUES
(2, 'Personal Work');

INSERT INTO Paid_Leave VALUES
(4, 2);

-- 7. ATTENDANCE

INSERT INTO Attendance_Log VALUES
(1, '2026-04-01', '09:00:00', '18:00:00', 'Day', 'Normal', 101),
(2, '2026-04-01', '09:30:00', '18:30:00', 'Day', 'Normal', 102),
(3, '2026-04-01', '08:45:00', '17:45:00', 'Day', 'Normal', 103),
(4, '2026-04-01', '09:15:00', '18:15:00', 'Day', 'Normal', 104);

-- 8. PAYROLL

INSERT INTO Payroll VALUES
(1, '2026-04-01', 40000, 'TXN_101_20260401', 101),
(2, '2026-04-01', 30000, 'TXN_102_20260401', 102),
(3, '2026-04-01', 35000, 'TXN_103_20260401', 103),
(4, '2026-04-01', 45000, 'TXN_104_20260401', 104);

-- 9. PROJECTS

INSERT INTO Projects VALUES
(1, 'HR System', '2026-01-01', '2026-06-01', 'Ongoing', 101, 1),
(2, 'Payroll App', '2026-02-01', '2026-07-01', 'Ongoing', 102, 2),
(3, 'Finance Tool', '2026-03-01', '2026-08-01', 'Ongoing', 103, 3),
(4, 'Marketing Campaign', '2026-04-01', '2026-09-01', 'Ongoing', 104, 4);

INSERT INTO Employee_Projects VALUES
(101, 1, '2026-01-01', 'Backend', 40),
(102, 2, '2026-02-01', 'HR Ops', 35),
(103, 3, '2026-03-01', 'Accounts', 30),
(104, 4, '2026-04-01', 'Marketing', 25);

-- 10. MEETING

INSERT INTO Meeting VALUES
(1, '2026-04-05', '10:00:00', 'IT Planning', 1),
(2, '2026-04-06', '11:00:00', 'HR Meeting', 2),
(3, '2026-04-07', '12:00:00', 'Finance Review', 3),
(4, '2026-04-08', '01:00:00', 'Marketing Strategy', 4);

-- queries

SELECT 
    e.EmpID,
    e.Emp_name,
    d.d_name
FROM Employee e
JOIN Department d 
    ON e.department_id = d.department_id;
    
SELECT 
    d.d_name,
    COUNT(e.EmpID) AS total_employees
FROM Department d
LEFT JOIN Employee e 
    ON d.department_id = e.department_id
GROUP BY d.d_name;

SELECT 
    e.EmpID,
    e.Emp_name,
    d.d_name,
    ROW_NUMBER() OVER (PARTITION BY d.department_id ORDER BY e.EmpID) AS dept_rank
FROM Employee e
JOIN Department d 
    ON e.department_id = d.department_id;
    
SELECT 
    EmpID,
    COUNT(*) AS approved_leaves
FROM Leave_Request
WHERE status = 'Approved'
GROUP BY EmpID;

SELECT 
    Emp_name,
    GetEmployeeAge(EmpID) AS age,
    GetApprovedLeaveCount(EmpID) AS leaves_taken
FROM Employee;
