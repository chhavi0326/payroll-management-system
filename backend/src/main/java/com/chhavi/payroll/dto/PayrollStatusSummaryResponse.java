package com.chhavi.payroll.dto;

import com.chhavi.payroll.entity.PayrollStatus;

import java.math.BigDecimal;

public class PayrollStatusSummaryResponse {

    private PayrollStatus status;
    private long totalPayrolls;
    private BigDecimal totalGrossSalary;
    private BigDecimal totalTax;
    private BigDecimal totalDeductions;
    private BigDecimal totalNetSalary;

    public PayrollStatusSummaryResponse() {
    }

    public PayrollStatusSummaryResponse(
            PayrollStatus status,
            long totalPayrolls,
            BigDecimal totalGrossSalary,
            BigDecimal totalTax,
            BigDecimal totalDeductions,
            BigDecimal totalNetSalary) {

        this.status = status;
        this.totalPayrolls = totalPayrolls;
        this.totalGrossSalary = totalGrossSalary;
        this.totalTax = totalTax;
        this.totalDeductions = totalDeductions;
        this.totalNetSalary = totalNetSalary;
    }

    public PayrollStatus getStatus() {
        return status;
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