package com.chhavi.payroll.controller;

import com.chhavi.payroll.dto.PayrollRequest;
import com.chhavi.payroll.dto.PayrollResponse;
import com.chhavi.payroll.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {
        return ResponseEntity.ok(
                payrollService.getAllPayrolls()
        );
    }

    // Get Payroll By ID
    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponse> getPayrollById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.getPayrollById(id)
        );
    }

    // Get Payrolls By Employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                payrollService.getPayrollsByEmployee(employeeId)
        );
    }

    // Get Payrolls By Pay Period
    @GetMapping("/period/{payPeriod}")
    public ResponseEntity<List<PayrollResponse>> getPayrollsByPayPeriod(
            @PathVariable String payPeriod) {

        return ResponseEntity.ok(
                payrollService.getPayrollsByPayPeriod(payPeriod)
        );
    }

    // Delete Payroll
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(
            @PathVariable Long id) {

        payrollService.deletePayroll(id);

        return ResponseEntity.noContent().build();
    }
}