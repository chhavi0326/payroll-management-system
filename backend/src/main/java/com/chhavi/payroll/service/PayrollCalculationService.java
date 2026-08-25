package com.chhavi.payroll.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PayrollCalculationService {

    private final BigDecimal taxRate;

    public PayrollCalculationService(
            @Value("${payroll.tax-rate}") BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal calculateGrossSalary(
            BigDecimal basicSalary,
            BigDecimal allowances) {

        return basicSalary.add(allowances);
    }

    public BigDecimal calculateTax(BigDecimal grossSalary) {

        return grossSalary
                .multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateNetSalary(
            BigDecimal grossSalary,
            BigDecimal tax,
            BigDecimal deductions) {

        return grossSalary
                .subtract(tax)
                .subtract(deductions)
                .setScale(2, RoundingMode.HALF_UP);
    }
}