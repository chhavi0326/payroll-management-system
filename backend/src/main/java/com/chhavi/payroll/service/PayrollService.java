package com.chhavi.payroll.service;

import com.chhavi.payroll.dto.PayrollResponse;
import com.chhavi.payroll.entity.Employee;
import com.chhavi.payroll.entity.Payroll;
import com.chhavi.payroll.exception.EmployeeNotFoundException;
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

    public PayrollService(PayrollRepository payrollRepository,
                          EmployeeRepository employeeRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
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

        BigDecimal grossSalary = basicSalary.add(allowances);
        BigDecimal netSalary = grossSalary.subtract(deductions);

        Payroll payroll = new Payroll();

        payroll.setEmployee(employee);
        payroll.setBasicSalary(basicSalary);
        payroll.setAllowances(allowances);
        payroll.setDeductions(deductions);
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
                payroll.getGrossSalary(),
                payroll.getNetSalary(),
                payroll.getPayPeriod()
        );
    }
}