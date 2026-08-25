package com.chhavi.payroll.service;

import com.chhavi.payroll.dto.PayrollResponse;
import com.chhavi.payroll.entity.Employee;
import com.chhavi.payroll.entity.Payroll;
import com.chhavi.payroll.entity.PayrollStatus;
import com.chhavi.payroll.exception.DuplicatePayrollException;
import com.chhavi.payroll.exception.EmployeeNotFoundException;
import com.chhavi.payroll.exception.InvalidPayrollStateException;
import com.chhavi.payroll.exception.PayrollNotFoundException;
import com.chhavi.payroll.repository.EmployeeRepository;
import com.chhavi.payroll.repository.PayrollRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollCalculationService payrollCalculationService;

    public PayrollService(PayrollRepository payrollRepository,
                          EmployeeRepository employeeRepository,
                          PayrollCalculationService payrollCalculationService) {

        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.payrollCalculationService = payrollCalculationService;
    }

    public PayrollResponse createPayroll(Long employeeId,
                                         BigDecimal basicSalary,
                                         BigDecimal allowances,
                                         BigDecimal deductions,
                                         String payPeriod) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + employeeId));

        if (payrollRepository.existsByEmployeeIdAndPayPeriod(
                employeeId, payPeriod)) {

            throw new DuplicatePayrollException(
                    "Payroll already exists for employee id "
                            + employeeId
                            + " for pay period "
                            + payPeriod);
        }

        BigDecimal grossSalary =
                payrollCalculationService.calculateGrossSalary(
                        basicSalary,
                        allowances
                );

        BigDecimal taxAmount =
                payrollCalculationService.calculateTax(grossSalary);

        BigDecimal netSalary =
                payrollCalculationService.calculateNetSalary(
                        grossSalary,
                        taxAmount,
                        deductions
                );

        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPayrollStateException(
                    "Net salary cannot be negative");
        }

        Payroll payroll = new Payroll();

        payroll.setEmployee(employee);
        payroll.setBasicSalary(basicSalary);
        payroll.setAllowances(allowances);
        payroll.setDeductions(deductions);
        payroll.setTaxAmount(taxAmount);
        payroll.setGrossSalary(grossSalary);
        payroll.setNetSalary(netSalary);
        payroll.setPayPeriod(payPeriod);

        Payroll savedPayroll = payrollRepository.save(payroll);

        return mapToResponse(savedPayroll);
    }

    public List<PayrollResponse> getAllPayrolls() {
        return payrollRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PayrollResponse getPayrollById(Long id) {

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() ->
                        new PayrollNotFoundException(
                                "Payroll not found with id: " + id));

        return mapToResponse(payroll);
    }

    public List<PayrollResponse> getPayrollsByEmployee(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<PayrollResponse> getPayrollsByPayPeriod(String payPeriod) {
        return payrollRepository.findByPayPeriod(payPeriod)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PayrollResponse processPayroll(Long id) {

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() ->
                        new PayrollNotFoundException(
                                "Payroll not found with id: " + id));

        if (payroll.getStatus() != PayrollStatus.DRAFT) {
            throw new InvalidPayrollStateException(
                    "Only DRAFT payroll can be processed");
        }

        payroll.setStatus(PayrollStatus.PROCESSED);

        Payroll updatedPayroll = payrollRepository.save(payroll);

        return mapToResponse(updatedPayroll);
    }

    public PayrollResponse payPayroll(Long id) {

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() ->
                        new PayrollNotFoundException(
                                "Payroll not found with id: " + id));

        if (payroll.getStatus() != PayrollStatus.PROCESSED) {
            throw new InvalidPayrollStateException(
                    "Only PROCESSED payroll can be marked as PAID");
        }

        payroll.setStatus(PayrollStatus.PAID);

        Payroll updatedPayroll = payrollRepository.save(payroll);

        return mapToResponse(updatedPayroll);
    }

    public void deletePayroll(Long id) {

        if (!payrollRepository.existsById(id)) {
            throw new PayrollNotFoundException(
                    "Payroll not found with id: " + id);
        }

        payrollRepository.deleteById(id);
    }

    private PayrollResponse mapToResponse(Payroll payroll) {

        Employee employee = payroll.getEmployee();

        String employeeName =
                employee.getFirstName() + " " + employee.getLastName();

        return new PayrollResponse(
                payroll.getId(),
                employee.getId(),
                employee.getEmployeeCode(),
                employeeName,
                payroll.getBasicSalary(),
                payroll.getAllowances(),
                payroll.getDeductions(),
                payroll.getTaxAmount(),
                payroll.getGrossSalary(),
                payroll.getNetSalary(),
                payroll.getPayPeriod(),
                payroll.getStatus()
        );
    }
}