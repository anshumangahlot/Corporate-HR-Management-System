INSERT INTO Zip_Directory VALUES (110001, 'Delhi', 'Delhi');
INSERT INTO Zip_Directory VALUES (560001, 'Bangalore', 'Karnataka');

INSERT INTO Employee VALUES (1, 'Ansh', 'Male', '2004-01-01', 'ansh@mail.com', 'Street A', 110001, 101, 10);
INSERT INTO Employee VALUES (2, 'Rahul', 'Male', '2003-05-02', 'rahul@mail.com', 'Street B', 560001, 102, 20);
INSERT INTO Employee VALUES (3, 'Priya', 'Female', '2002-03-15', 'priya@mail.com', 'Street C', 110001, 101, 10);
INSERT INTO Employee VALUES (4, 'Amit', 'Male', '2001-08-20', 'amit@mail.com', 'Street D', 560001, 102, 20);
INSERT INTO Employee VALUES (5, 'Neha', 'Female', '2003-11-10', 'neha@mail.com', 'Street E', 110001, 101, 10);

INSERT INTO Employee_Phones VALUES (1, '9876543210');
INSERT INTO Employee_Phones VALUES (2, '9123456780');
INSERT INTO Employee_Phones VALUES (3, '9345678901');
INSERT INTO Employee_Phones VALUES (4, '9567890123');
INSERT INTO Employee_Phones VALUES (5, '9789012345');

INSERT INTO Department VALUES (10, 'HR', 'Mr. Sharma');
INSERT INTO Department VALUES (20, 'IT', 'Ms. Kapoor');
INSERT INTO Department VALUES (30, 'Finance', 'Mr. Patel');

INSERT INTO Branch VALUES (1, 'HQ', 'Corporate', 'Main Street', 110001, 1);
INSERT INTO Branch VALUES (2, 'Tech Park', 'IT', 'Electronic City', 560001, 2);

INSERT INTO Branch_Dept VALUES (1, 10);
INSERT INTO Branch_Dept VALUES (2, 20);
INSERT INTO Branch_Dept VALUES (1, 30);

INSERT INTO Job_Role VALUES (101, 'Manager', 8, 50000, 100000, 5, 'Full-Time', 20, 10);
INSERT INTO Job_Role VALUES (102, 'Developer', 8, 30000, 80000, 2, 'Full-Time', 15, 20);
INSERT INTO Job_Role VALUES (103, 'Analyst', 8, 25000, 75000, 3, 'Full-Time', 15, 30);

INSERT INTO Role_Skills VALUES (101, 'Leadership');
INSERT INTO Role_Skills VALUES (102, 'Java');
INSERT INTO Role_Skills VALUES (103, 'SQL');

INSERT INTO Intern VALUES (1, '2024-01-01', '2024-06-01');
INSERT INTO Intern VALUES (2, '2024-02-01', '2024-07-01');

INSERT INTO Full_Time VALUES (1, 'PF123');
INSERT INTO Full_Time VALUES (2, 'PF456');
INSERT INTO Full_Time VALUES (3, 'PF789');
INSERT INTO Full_Time VALUES (4, 'PF012');
INSERT INTO Full_Time VALUES (5, 'PF345');

INSERT INTO Leave_Request VALUES (1, '2024-03-01', '2024-03-05', 'Approved', 1);
INSERT INTO Leave_Request VALUES (2, '2024-04-01', '2024-04-03', 'Pending', 2);

INSERT INTO Sick_Leave VALUES (1, 'Medical Certificate');
INSERT INTO Casual_Leave VALUES (2, 'Family Event');

INSERT INTO Attendance_Log VALUES (1, '2024-03-10', '09:00:00', '17:00:00', 'Day', 'On time', 1);
INSERT INTO Attendance_Log VALUES (2, '2024-03-10', '09:30:00', '17:00:00', 'Day', 'Late', 2);

INSERT INTO Payroll VALUES (1, '2024-03-31', 50000, 'TXN123', 1);
INSERT INTO Payroll VALUES (2, '2024-03-31', 40000, 'TXN124', 2);

INSERT INTO Salary_Breakdown VALUES (1, 'Basic', 30000, 'Earning', 1);
INSERT INTO Salary_Breakdown VALUES (2, 'Tax', 5000, 'Deduction', 2);

INSERT INTO Projects VALUES (1, 'AI System', '2024-01-01', '2024-12-01', 'Ongoing', 1, 20);
INSERT INTO Projects VALUES (2, 'Web App', '2024-02-01', '2024-10-01', 'Ongoing', 2, 20);

INSERT INTO Employee_Projects VALUES (1, 1, '2024-01-10', 'Lead', 40);
INSERT INTO Employee_Projects VALUES (2, 2, '2024-02-10', 'Developer', 35);

INSERT INTO Meeting VALUES (1, '2024-03-20', '10:00:00', 'Planning', 10);
INSERT INTO Meeting VALUES (2, '2024-03-22', '11:00:00', 'Review', 20);

INSERT INTO users (username, password, role) VALUES
('admin1', 'admin123', 'admin'),
('emp1', 'emp123', 'employee'),
('emp2', 'emp123', 'employee');