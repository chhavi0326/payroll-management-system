package com.chhavi.payroll.dto;

import com.chhavi.payroll.entity.PayrollStatus;

import java.math.BigDecimal;

public class PayrollResponse {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private BigDecimal basicSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal taxAmount;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private String payPeriod;
    private PayrollStatus status;

    public PayrollResponse() {
    }

    public PayrollResponse(
            Long id,
            Long employeeId,
            String employeeCode,
            String employeeName,
            BigDecimal basicSalary,
            BigDecimal allowances,
            BigDecimal deductions,
            BigDecimal taxAmount,
            BigDecimal grossSalary,
            BigDecimal netSalary,
            String payPeriod,
            PayrollStatus status) {

        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.deductions = deductions;
        this.taxAmount = taxAmount;
        this.grossSalary = grossSalary;
        this.netSalary = netSalary;
        this.payPeriod = payPeriod;
        this.status = status;
    }

    public Long getId() {
        return id;
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

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public PayrollStatus getStatus() {
        return status;
    }
}