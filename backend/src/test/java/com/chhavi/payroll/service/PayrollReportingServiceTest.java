package com.chhavi.payroll.service;

import com.chhavi.payroll.dto.DepartmentPayrollSummaryResponse;
import com.chhavi.payroll.dto.EmployeePayrollSummaryResponse;
import com.chhavi.payroll.dto.PayrollSummaryResponse;
import com.chhavi.payroll.dto.PayrollStatusSummaryResponse;
import com.chhavi.payroll.entity.PayrollStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PayrollReportingServiceTest {

    @Test
    void shouldCreatePayrollSummaryResponse() {

        PayrollSummaryResponse response =
                new PayrollSummaryResponse(
                        "TEST-AUGUST-2027",
                        2,
                        new BigDecimal("150000"),
                        new BigDecimal("15000"),
                        new BigDecimal("10000"),
                        new BigDecimal("125000")
                );

        assertEquals(
                "TEST-AUGUST-2027",
                response.getPayPeriod()
        );

        assertEquals(
                2,
                response.getTotalEmployees()
        );

        assertEquals(
                new BigDecimal("150000"),
                response.getTotalGrossSalary()
        );

        assertEquals(
                new BigDecimal("15000"),
                response.getTotalTax()
        );

        assertEquals(
                new BigDecimal("10000"),
                response.getTotalDeductions()
        );

        assertEquals(
                new BigDecimal("125000"),
                response.getTotalNetSalary()
        );
    }

    @Test
    void shouldCreateEmployeePayrollSummaryResponse() {

        EmployeePayrollSummaryResponse response =
                new EmployeePayrollSummaryResponse(
                        1L,
                        "EMP003",
                        "Priya Singh",
                        4,
                        new BigDecimal("257000"),
                        new BigDecimal("12500"),
                        new BigDecimal("25000"),
                        new BigDecimal("219500")
                );

        assertEquals(1L, response.getEmployeeId());
        assertEquals("EMP003", response.getEmployeeCode());
        assertEquals("Priya Singh", response.getEmployeeName());
        assertEquals(4, response.getTotalPayrolls());

        assertEquals(
                new BigDecimal("257000"),
                response.getTotalGrossSalary()
        );

        assertEquals(
                new BigDecimal("12500"),
                response.getTotalTax()
        );

        assertEquals(
                new BigDecimal("25000"),
                response.getTotalDeductions()
        );

        assertEquals(
                new BigDecimal("219500"),
                response.getTotalNetSalary()
        );
    }

    @Test
    void shouldCreateDepartmentPayrollSummaryResponse() {

        DepartmentPayrollSummaryResponse response =
                new DepartmentPayrollSummaryResponse(
                        "Finance",
                        5,
                        20,
                        new BigDecimal("1250000"),
                        new BigDecimal("125000"),
                        new BigDecimal("100000"),
                        new BigDecimal("1025000")
                );

        assertEquals(
                "Finance",
                response.getDepartment()
        );

        assertEquals(
                5,
                response.getTotalEmployees()
        );

        assertEquals(
                20,
                response.getTotalPayrolls()
        );

        assertEquals(
                new BigDecimal("1250000"),
                response.getTotalGrossSalary()
        );

        assertEquals(
                new BigDecimal("125000"),
                response.getTotalTax()
        );

        assertEquals(
                new BigDecimal("100000"),
                response.getTotalDeductions()
        );

        assertEquals(
                new BigDecimal("1025000"),
                response.getTotalNetSalary()
        );
    }

    @Test
    void shouldCreateStatusSummaryResponse() {

        PayrollStatusSummaryResponse response =
                new PayrollStatusSummaryResponse(
                        PayrollStatus.PAID,
                        3,
                        new BigDecimal("210000"),
                        new BigDecimal("21000"),
                        new BigDecimal("14000"),
                        new BigDecimal("175000")
                );

        assertEquals(
                PayrollStatus.PAID,
                response.getStatus()
        );

        assertEquals(
                3,
                response.getTotalPayrolls()
        );

        assertEquals(
                new BigDecimal("210000"),
                response.getTotalGrossSalary()
        );

        assertEquals(
                new BigDecimal("21000"),
                response.getTotalTax()
        );

        assertEquals(
                new BigDecimal("14000"),
                response.getTotalDeductions()
        );

        assertEquals(
                new BigDecimal("175000"),
                response.getTotalNetSalary()
        );
    }
}