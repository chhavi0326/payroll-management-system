package com.chhavi.payroll.dto;

import java.math.BigDecimal;

public class EmployeePayrollSummaryResponse {

    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private long totalPayrolls;
    private BigDecimal totalGrossSalary;
    private BigDecimal totalTax;
    private BigDecimal totalDeductions;
    private BigDecimal totalNetSalary;

    public EmployeePayrollSummaryResponse() {
    }

    public EmployeePayrollSummaryResponse(
            Long employeeId,
            String employeeCode,
            String employeeName,
            long totalPayrolls,
            BigDecimal totalGrossSalary,
            BigDecimal totalTax,
            BigDecimal totalDeductions,
            BigDecimal totalNetSalary) {

        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.totalPayrolls = totalPayrolls;
        this.totalGrossSalary = totalGrossSalary;
        this.totalTax = totalTax;
        this.totalDeductions = totalDeductions;
        this.totalNetSalary = totalNetSalary;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public long getTotalPayrolls() {
        return totalPayrolls;
    }

    public BigDecimal getTotalGrossSalary() {
        return totalGrossSalary;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public BigDecimal getTotalNetSalary() {
        return totalNetSalary;
    }
}