package com.chhavi.payroll.dto;

import java.math.BigDecimal;

public class PayrollSummaryResponse {

    private String payPeriod;
    private long totalEmployees;
    private BigDecimal totalGrossSalary;
    private BigDecimal totalTax;
    private BigDecimal totalDeductions;
    private BigDecimal totalNetSalary;

    public PayrollSummaryResponse() {
    }

    public PayrollSummaryResponse(
            String payPeriod,
            long totalEmployees,
            BigDecimal totalGrossSalary,
            BigDecimal totalTax,
            BigDecimal totalDeductions,
            BigDecimal totalNetSalary) {

        this.payPeriod = payPeriod;
        this.totalEmployees = totalEmployees;
        this.totalGrossSalary = totalGrossSalary;
        this.totalTax = totalTax;
        this.totalDeductions = totalDeductions;
        this.totalNetSalary = totalNetSalary;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public long getTotalEmployees() {
        return totalEmployees;
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