package com.chhavi.payroll.repository;

import com.chhavi.payroll.entity.Payroll;
import com.chhavi.payroll.entity.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findByEmployeeId(Long employeeId);

    List<Payroll> findByPayPeriod(String payPeriod);

    boolean existsByEmployeeIdAndPayPeriod(
            Long employeeId,
            String payPeriod
    );

    @Query("""
            SELECT COUNT(p.id),
                   COALESCE(SUM(p.grossSalary), 0),
                   COALESCE(SUM(p.taxAmount), 0),
                   COALESCE(SUM(p.deductions), 0),
                   COALESCE(SUM(p.netSalary), 0)
            FROM Payroll p
            WHERE p.payPeriod = :payPeriod
            """)
    List<Object[]> getPayrollSummary(
            @Param("payPeriod") String payPeriod
    );

    @Query("""
            SELECT COUNT(p.id),
                   COALESCE(SUM(p.grossSalary), 0),
                   COALESCE(SUM(p.taxAmount), 0),
                   COALESCE(SUM(p.deductions), 0),
                   COALESCE(SUM(p.netSalary), 0)
            FROM Payroll p
            WHERE p.employee.id = :employeeId
            """)
    List<Object[]> getEmployeePayrollSummary(
            @Param("employeeId") Long employeeId
    );

    @Query("""
            SELECT COUNT(DISTINCT e.id),
                   COUNT(p.id),
                   COALESCE(SUM(p.grossSalary), 0),
                   COALESCE(SUM(p.taxAmount), 0),
                   COALESCE(SUM(p.deductions), 0),
                   COALESCE(SUM(p.netSalary), 0)
            FROM Payroll p
            JOIN p.employee e
            WHERE e.department = :department
            """)
    List<Object[]> getDepartmentPayrollSummary(
            @Param("department") String department
    );

    @Query("""
            SELECT p.status,
                   COUNT(p.id),
                   COALESCE(SUM(p.grossSalary), 0),
                   COALESCE(SUM(p.taxAmount), 0),
                   COALESCE(SUM(p.deductions), 0),
                   COALESCE(SUM(p.netSalary), 0)
            FROM Payroll p
            GROUP BY p.status
            """)
    List<Object[]> getPayrollSummaryByStatus();
}