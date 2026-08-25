package com.chhavi.payroll.dto;

import java.math.BigDecimal;

public class DepartmentPayrollSummaryResponse {

    private String department;
    private long totalEmployees;
    private long totalPayrolls;
    private BigDecimal totalGrossSalary;
    private BigDecimal totalTax;
    private BigDecimal totalDeductions;
    private BigDecimal totalNetSalary;

    public DepartmentPayrollSummaryResponse() {
    }

    public DepartmentPayrollSummaryResponse(
            String department,
            long totalEmployees,
            long totalPayrolls,
            BigDecimal totalGrossSalary,
            BigDecimal totalTax,
            BigDecimal totalDeductions,
            BigDecimal totalNetSalary) {

        this.department = department;
        this.totalEmployees = totalEmployees;
        this.totalPayrolls = totalPayrolls;
        this.totalGrossSalary = totalGrossSalary;
        this.totalTax = totalTax;
        this.totalDeductions = totalDeductions;
        this.totalNetSalary = totalNetSalary;
    }

    public String getDepartment() {
        return department;
    }

    public long getTotalEmployees() {
        return totalEmployees;
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