package com.chhavi.payroll.controller;

import com.chhavi.payroll.dto.DepartmentPayrollSummaryResponse;
import com.chhavi.payroll.dto.EmployeePayrollSummaryResponse;
import com.chhavi.payroll.dto.PayrollRequest;
import com.chhavi.payroll.dto.PayrollResponse;
import com.chhavi.payroll.dto.PayrollStatusSummaryResponse;
import com.chhavi.payroll.dto.PayrollSummaryResponse;
import com.chhavi.payroll.security.PayrollSecurityService;
import com.chhavi.payroll.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    // Create Payroll
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<PayrollResponse> createPayroll(
            @Valid @RequestBody PayrollRequest request) {

        PayrollResponse payroll = payrollService.createPayroll(
                request.getEmployeeId(),
                request.getBasicSalary(),
                request.getAllowances(),
                request.getDeductions(),
                request.getPayPeriod()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payroll);
    }

    // Get All Payrolls
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {

        return ResponseEntity.ok(
                payrollService.getAllPayrolls()
        );
    }

    // Get Payroll By ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<PayrollResponse> getPayrollById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.getPayrollById(id)
        );
    }

    // Get Payrolls By Employee
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("@payrollSecurityService.canAccessEmployeePayroll(authentication, #employeeId)")
    public ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                payrollService.getPayrollsByEmployee(employeeId)
        );
    }

    // Get Payrolls By Pay Period
    @GetMapping("/period/{payPeriod}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<PayrollResponse>> getPayrollsByPayPeriod(
            @PathVariable String payPeriod) {

        return ResponseEntity.ok(
                payrollService.getPayrollsByPayPeriod(payPeriod)
        );
    }

    // Process Payroll
    @PutMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<PayrollResponse> processPayroll(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.processPayroll(id)
        );
    }

    // Mark Payroll as Paid
    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<PayrollResponse> payPayroll(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.payPayroll(id)
        );
    }

    // Payroll Summary By Pay Period
    @GetMapping("/reports/summary/{payPeriod}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<PayrollSummaryResponse> getPayrollSummary(
            @PathVariable String payPeriod) {

        return ResponseEntity.ok(
                payrollService.getPayrollSummary(payPeriod)
        );
    }

    // Employee Payroll Summary
    @GetMapping("/reports/employee/{employeeId}")
    @PreAuthorize("@payrollSecurityService.canAccessEmployeePayroll(authentication, #employeeId)")
    public ResponseEntity<EmployeePayrollSummaryResponse> getEmployeePayrollSummary(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                payrollService.getEmployeePayrollSummary(employeeId)
        );
    }

    // Department Payroll Summary
    @GetMapping("/reports/department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<DepartmentPayrollSummaryResponse> getDepartmentPayrollSummary(
            @PathVariable String department) {

        return ResponseEntity.ok(
                payrollService.getDepartmentPayrollSummary(department)
        );
    }

    // Payroll Summary By Status
    @GetMapping("/reports/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<PayrollStatusSummaryResponse>> getPayrollSummaryByStatus() {

        return ResponseEntity.ok(
                payrollService.getPayrollSummaryByStatus()
        );
    }

    // Delete Payroll
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayroll(
            @PathVariable Long id) {

        payrollService.deletePayroll(id);

        return ResponseEntity.noContent().build();
    }
}