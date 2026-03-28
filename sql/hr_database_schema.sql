CREATE DATABASE hr_database;
USE hr_database;

CREATE TABLE Zip_Directory (
    ZipCode INT PRIMARY KEY,
    City VARCHAR(50),
    State VARCHAR(50)
);

CREATE TABLE Employee (
    EmpID INT PRIMARY KEY,
    Emp_name VARCHAR(50),
    Gender VARCHAR(10),
    DOB DATE,
    Email VARCHAR(50),
    Street VARCHAR(100),
    ZipCode INT,
    role_id INT,
    department_id INT,
    FOREIGN KEY (ZipCode) REFERENCES Zip_Directory(ZipCode)
);

CREATE TABLE Employee_Phones (
    EmpID INT,
    Phone_Number VARCHAR(10),
    PRIMARY KEY (EmpID, Phone_Number),
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
);

CREATE TABLE Department (
    department_id INT PRIMARY KEY,
    d_name VARCHAR(50),
    d_head VARCHAR(50)
);

CREATE TABLE Branch (
    branch_id INT PRIMARY KEY,
    branch_name VARCHAR(50),
    branch_type VARCHAR(50),
    street VARCHAR(100),
    zip_code INT,
    mgr_id INT,
    FOREIGN KEY (mgr_id) REFERENCES Employee(EmpID)
);

CREATE TABLE Branch_Dept (
    branch_id INT,
    dept_id INT,
    PRIMARY KEY (branch_id, dept_id),
    FOREIGN KEY (branch_id) REFERENCES Branch(branch_id),
    FOREIGN KEY (dept_id) REFERENCES Department(department_id)
);

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

CREATE TABLE Role_Skills (
    role_id INT,
    skill_name VARCHAR(50),
    PRIMARY KEY (role_id, skill_name),
    FOREIGN KEY (role_id) REFERENCES Job_Role(role_id)
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

CREATE TABLE Payroll (
    payroll_id INT PRIMARY KEY,
    paydate DATE,
    total_amount FLOAT,
    transaction_id VARCHAR(50),
    EmpID INT,
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID)
);

CREATE TABLE Salary_Breakdown (
    breakdown_id INT PRIMARY KEY,
    component_name VARCHAR(50),
    amount FLOAT,
    type VARCHAR(20),
    payroll_id INT,
    FOREIGN KEY (payroll_id) REFERENCES Payroll(payroll_id)
);

CREATE TABLE Projects (
    project_id INT PRIMARY KEY,
    PName VARCHAR(50),
    StartDate DATE,
    EndDate DATE,
    Status VARCHAR(20),
    TeamLead INT,
    dept_id INT,
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

CREATE TABLE Recruitment (
    recruitment_id INT PRIMARY KEY,
    hire_date DATE,
    hire_status VARCHAR(20),
    EmpID INT,
    recruiter_id INT,
    role_id INT,
    FOREIGN KEY (EmpID) REFERENCES Employee(EmpID),
    FOREIGN KEY (recruiter_id) REFERENCES Employee(EmpID),
    FOREIGN KEY (role_id) REFERENCES Job_Role(role_id)
);

CREATE TABLE Meeting (
    meeting_id INT PRIMARY KEY,
    m_date DATE,
    m_time TIME,
    topic VARCHAR(100),
    dept_id INT,
    FOREIGN KEY (dept_id) REFERENCES Department(department_id)
);

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);