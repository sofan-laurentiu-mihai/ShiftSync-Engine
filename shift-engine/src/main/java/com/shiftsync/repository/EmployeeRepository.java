package com.shiftsync.repository;

import com.shiftsync.model.DepartmentRole;
import com.shiftsync.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// JpaRepository<Employee, UUID> tells Spring
// 1. Work with entity 'Employee'
// 2. The type of the primary key (ID) is 'UUID'
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    // Sprind deduces automatically
    // SELECT * FROM employees WHERE email = ?
    // Returns Optional to prevent NullPointerException if the mail does not exist
    Optional<Employee> findByEmail(String email);

    // Find all employees from a section with a certain qualification
    // SELECT * FROM employees WHERE department = ? AND role = ?
    List<Employee> findByDepartmentAndRole(String department, DepartmentRole role);
}