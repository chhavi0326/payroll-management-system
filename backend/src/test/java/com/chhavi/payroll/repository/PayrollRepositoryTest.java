package com.chhavi.payroll.repository;

import com.chhavi.payroll.entity.Employee;
import com.chhavi.payroll.entity.Payroll;
import com.chhavi.payroll.entity.PayrollStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PayrollRepositoryTest {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void shouldCalculatePayrollSummaryByPayPeriod() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        Payroll payroll = createPayroll(
                employee,
                "TEST-MARCH-2027",
                new BigDecimal("50000"),
                new BigDecimal("5000"),
                new BigDecimal("5000"),
                new BigDecimal("5000"),
                new BigDecimal("45000"),
                PayrollStatus.PAID
        );

        payrollRepository.saveAndFlush(payroll);

        List<Object[]> result =
                payrollRepository.getPayrollSummary("TEST-MARCH-2027");

        assertFalse(result.isEmpty());

        Object[] summary = result.get(0);

        assertEquals(1L, ((Number) summary[0]).longValue());

        assertEquals(
                new BigDecimal("55000.00"),
                ((BigDecimal) summary[1]).setScale(2)
        );

        assertEquals(
                new BigDecimal("5000.00"),
                ((BigDecimal) summary[2]).setScale(2)
        );

        assertEquals(
                new BigDecimal("5000.00"),
                ((BigDecimal) summary[3]).setScale(2)
        );

        assertEquals(
                new BigDecimal("45000.00"),
                ((BigDecimal) summary[4]).setScale(2)
        );
    }

    @Test
    void shouldCalculateEmployeePayrollSummary() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        Payroll payroll1 = createPayroll(
                employee,
                "TEST-EMPLOYEE-APRIL-2027",
                new BigDecimal("50000"),
                new BigDecimal("5000"),
                new BigDecimal("5000"),
                new BigDecimal("5000"),
                new BigDecimal("45000"),
                PayrollStatus.PAID
        );

        Payroll payroll2 = createPayroll(
                employee,
                "TEST-EMPLOYEE-MAY-2027",
                new BigDecimal("60000"),
                new BigDecimal("10000"),
                new BigDecimal("7000"),
                new BigDecimal("7000"),
                new BigDecimal("56000"),
                PayrollStatus.PROCESSED
        );

        payrollRepository.saveAllAndFlush(List.of(
                payroll1,
                payroll2
        ));

        List<Object[]> result =
                payrollRepository.getEmployeePayrollSummary(employee.getId());

        assertFalse(result.isEmpty());

        Object[] summary = result.get(0);

        assertTrue(
                ((Number) summary[0]).longValue() >= 2
        );

        BigDecimal totalGross =
                (BigDecimal) summary[1];

        BigDecimal totalTax =
                (BigDecimal) summary[2];

        BigDecimal totalDeductions =
                (BigDecimal) summary[3];

        BigDecimal totalNet =
                (BigDecimal) summary[4];

        assertTrue(totalGross.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(totalTax.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(totalDeductions.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(totalNet.compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void shouldCalculateDepartmentPayrollSummary() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        Payroll payroll = createPayroll(
                employee,
                "TEST-DEPARTMENT-JUNE-2027",
                new BigDecimal("50000"),
                new BigDecimal("5000"),
                new BigDecimal("5000"),
                new BigDecimal("5000"),
                new BigDecimal("45000"),
                PayrollStatus.PAID
        );

        payrollRepository.saveAndFlush(payroll);

        List<Object[]> result =
                payrollRepository.getDepartmentPayrollSummary(
                        employee.getDepartment()
                );

        assertFalse(result.isEmpty());

        Object[] summary = result.get(0);

        assertTrue(
                ((Number) summary[0]).longValue() >= 1
        );

        assertTrue(
                ((Number) summary[1]).longValue() >= 1
        );

        assertTrue(
                ((BigDecimal) summary[2])
                        .compareTo(BigDecimal.ZERO) > 0
        );
    }

    @Test
    void shouldCalculatePayrollSummaryByStatus() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        Payroll payroll = createPayroll(
                employee,
                "TEST-STATUS-JULY-2027",
                new BigDecimal("50000"),
                new BigDecimal("5000"),
                new BigDecimal("5000"),
                new BigDecimal("5000"),
                new BigDecimal("45000"),
                PayrollStatus.PAID
        );

        payrollRepository.saveAndFlush(payroll);

        List<Object[]> result =
                payrollRepository.getPayrollSummaryByStatus();

        assertFalse(result.isEmpty());

        boolean paidStatusFound = result.stream()
                .anyMatch(summary ->
                        summary[0] == PayrollStatus.PAID
                );

        assertTrue(paidStatusFound);
    }

    private Payroll createPayroll(
            Employee employee,
            String payPeriod,
            BigDecimal basicSalary,
            BigDecimal allowances,
            BigDecimal deductions,
            BigDecimal taxAmount,
            BigDecimal netSalary,
            PayrollStatus status) {

        Payroll payroll = new Payroll();

        payroll.setEmployee(employee);
        payroll.setBasicSalary(basicSalary);
        payroll.setAllowances(allowances);
        payroll.setDeductions(deductions);
        payroll.setTaxAmount(taxAmount);
        payroll.setGrossSalary(
                basicSalary.add(allowances)
        );
        payroll.setNetSalary(netSalary);
        payroll.setPayPeriod(payPeriod);
        payroll.setStatus(status);

        return payroll;
    }
}