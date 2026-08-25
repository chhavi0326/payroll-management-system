package com.chhavi.payroll.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig
class PayrollCalculationServiceTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        PayrollCalculationService payrollCalculationService() {
            return new PayrollCalculationService(
                    new BigDecimal("0.10")
            );
        }
    }

    @Autowired
    private PayrollCalculationService calculationService;

    @Test
    void shouldCalculateGrossSalary() {

        BigDecimal grossSalary =
                calculationService.calculateGrossSalary(
                        new BigDecimal("60000"),
                        new BigDecimal("10000")
                );

        assertEquals(
                new BigDecimal("70000"),
                grossSalary
        );
    }

    @Test
    void shouldCalculateTax() {

        BigDecimal tax =
                calculationService.calculateTax(
                        new BigDecimal("70000")
                );

        assertEquals(
                new BigDecimal("7000.00"),
                tax
        );
    }

    @Test
    void shouldCalculateNetSalary() {

        BigDecimal netSalary =
                calculationService.calculateNetSalary(
                        new BigDecimal("70000"),
                        new BigDecimal("7000"),
                        new BigDecimal("5000")
                );

        assertEquals(
                new BigDecimal("58000.00"),
                netSalary
        );
    }

    @Test
    void shouldRoundTaxToTwoDecimalPlaces() {

        BigDecimal tax =
                calculationService.calculateTax(
                        new BigDecimal("55555")
                );

        assertEquals(
                new BigDecimal("5555.50"),
                tax
        );
    }
}