package com.chhavi.payroll.repository;

import com.chhavi.payroll.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartment(String department);

    Optional<Employee> findByEmployeeCode(String employeeCode);
}