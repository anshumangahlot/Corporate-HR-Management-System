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
    d_head VARCHAR(50),
    FOREIGN KEY (d_head) REFERENCES Employee(EmpID)
);

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
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
    FOREIGN KEY (ZipCode) REFERENCES Zip_Directory(ZipCode),
    FOREIGN KEY (department_id) REFERENCES Department(department_id),
    FOREIGN KEY (role_id) REFERENCES Job_Role(role_id)
);

-- 4. EMPLOYEE RELATED

CREATE TABLE Employee_Phones (
    EmpID INT,
    Phone_Number CHAR(10),
    PRIMARY KEY (EmpID, Phone_Number),
    CONSTRAINT chk_employee_phones_phone_number CHECK (Phone_Number REGEXP '^[0-9]{10}$'),
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
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



-- 6. ROLE SKILLS

CREATE TABLE Role_Skills (
    role_id INT,
    skill_name VARCHAR(50),
    PRIMARY KEY (role_id, skill_name),
    FOREIGN KEY (role_id) REFERENCES Job_Role(role_id)
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

-- 11. RECRUITMENT


-- 12. MEETING

CREATE TABLE Meeting (
    meeting_id INT PRIMARY KEY,
    m_date DATE,
    m_time TIME,
    topic VARCHAR(100),
    dept_id INT,
    FOREIGN KEY (dept_id) REFERENCES Department(department_id)
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

-- Triggers

DELIMITER //

CREATE TRIGGER AfterEmployeeInsert
AFTER INSERT ON Employee
FOR EACH ROW
BEGIN
    INSERT INTO Payroll (payroll_id, paydate, total_amount, transaction_id, EmpID)
    VALUES (
        NEW.EmpID,
        CURDATE(),
        0,
        CONCAT('TXN_', NEW.EmpID, '_', DATE_FORMAT(CURDATE(), '%Y%m%d')),
        NEW.EmpID
    );
END //

DELIMITER ;

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
