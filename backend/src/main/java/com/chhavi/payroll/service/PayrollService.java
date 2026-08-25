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
import com.chhavi.payroll.dto.PayrollSummaryResponse;
import com.chhavi.payroll.dto.EmployeePayrollSummaryResponse;
import com.chhavi.payroll.dto.DepartmentPayrollSummaryResponse;
import com.chhavi.payroll.dto.PayrollStatusSummaryResponse;

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

    public PayrollSummaryResponse getPayrollSummary(String payPeriod) {

        List<Object[]> result =
                payrollRepository.getPayrollSummary(payPeriod);

        Object[] summary = result.get(0);

        long totalEmployees = ((Number) summary[0]).longValue();

        BigDecimal totalGrossSalary = (BigDecimal) summary[1];
        BigDecimal totalTax = (BigDecimal) summary[2];
        BigDecimal totalDeductions = (BigDecimal) summary[3];
        BigDecimal totalNetSalary = (BigDecimal) summary[4];

        return new PayrollSummaryResponse(
                payPeriod,
                totalEmployees,
                totalGrossSalary,
                totalTax,
                totalDeductions,
                totalNetSalary
        );
    }

    public EmployeePayrollSummaryResponse getEmployeePayrollSummary(
            Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee not found with id: " + employeeId));

        List<Object[]> result =
                payrollRepository.getEmployeePayrollSummary(employeeId);

        Object[] summary = result.get(0);

        long totalPayrolls =
                ((Number) summary[0]).longValue();

        BigDecimal totalGrossSalary =
                (BigDecimal) summary[1];

        BigDecimal totalTax =
                (BigDecimal) summary[2];

        BigDecimal totalDeductions =
                (BigDecimal) summary[3];

        BigDecimal totalNetSalary =
                (BigDecimal) summary[4];

        String employeeName =
                employee.getFirstName() + " " + employee.getLastName();

        return new EmployeePayrollSummaryResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employeeName,
                totalPayrolls,
                totalGrossSalary,
                totalTax,
                totalDeductions,
                totalNetSalary
        );
    }

    public DepartmentPayrollSummaryResponse getDepartmentPayrollSummary(
            String department) {

        List<Object[]> result =
                payrollRepository.getDepartmentPayrollSummary(department);

        Object[] summary = result.get(0);

        long totalEmployees =
                ((Number) summary[0]).longValue();

        long totalPayrolls =
                ((Number) summary[1]).longValue();

        BigDecimal totalGrossSalary =
                (BigDecimal) summary[2];

        BigDecimal totalTax =
                (BigDecimal) summary[3];

        BigDecimal totalDeductions =
                (BigDecimal) summary[4];

        BigDecimal totalNetSalary =
                (BigDecimal) summary[5];

        return new DepartmentPayrollSummaryResponse(
                department,
                totalEmployees,
                totalPayrolls,
                totalGrossSalary,
                totalTax,
                totalDeductions,
                totalNetSalary
        );
    }

    public List<PayrollStatusSummaryResponse> getPayrollSummaryByStatus() {

        List<Object[]> results =
                payrollRepository.getPayrollSummaryByStatus();

        return results.stream()
                .map(summary -> {

                    PayrollStatus status =
                            (PayrollStatus) summary[0];

                    long totalPayrolls =
                            ((Number) summary[1]).longValue();

                    BigDecimal totalGrossSalary =
                            (BigDecimal) summary[2];

                    BigDecimal totalTax =
                            (BigDecimal) summary[3];

                    BigDecimal totalDeductions =
                            (BigDecimal) summary[4];

                    BigDecimal totalNetSalary =
                            (BigDecimal) summary[5];

                    return new PayrollStatusSummaryResponse(
                            status,
                            totalPayrolls,
                            totalGrossSalary,
                            totalTax,
                            totalDeductions,
                            totalNetSalary
                    );
                })
                .toList();
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