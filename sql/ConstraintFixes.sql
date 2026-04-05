use hr_database;
-- Adding the Foreign Key for Department
ALTER TABLE Employee
ADD CONSTRAINT FK_Employee_Department
FOREIGN KEY (department_id) REFERENCES Department(department_id);

-- Adding the Foreign Key for Job Role
ALTER TABLE Employee
ADD CONSTRAINT FK_Employee_Role
FOREIGN KEY (role_id) REFERENCES Job_Role(role_id);
ALTER TABLE Employee 
MODIFY department_id INT NOT NULL,
MODIFY role_id INT NOT NULL;