package com.chhavi.payroll.security;

import com.chhavi.payroll.entity.Employee;
import com.chhavi.payroll.entity.Payroll;
import com.chhavi.payroll.entity.PayrollStatus;
import com.chhavi.payroll.repository.EmployeeRepository;
import com.chhavi.payroll.repository.PayrollRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Test
    void shouldRejectProtectedEndpointWithoutJwt() throws Exception {

        mockMvc.perform(
                        get("/api/payroll/999999")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldLoginWithValidAdminCredentials() throws Exception {

        String response = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "admin",
                                    "password": "Admin@123"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("\"username\":\"admin\""));
        assertTrue(response.contains("\"role\":\"ADMIN\""));
        assertTrue(response.contains("\"token\""));
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "admin",
                                    "password": "WrongPassword"
                                }
                                """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldLoginWithValidEmployeeCredentials() throws Exception {

        String response = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "priya",
                                    "password": "Priya@123"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("\"username\":\"priya\""));
        assertTrue(response.contains("\"role\":\"EMPLOYEE\""));
        assertTrue(response.contains("\"token\""));
    }

    @Test
    void shouldLoginWithValidHrCredentials() throws Exception {

        String response = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "hr",
                                    "password": "Hr@123"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("\"username\":\"hr\""));
        assertTrue(response.contains("\"role\":\"HR\""));
        assertTrue(response.contains("\"token\""));
    }

    @Test
    void shouldAllowAdminToViewPayroll() throws Exception {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        Payroll payroll = createPayroll(
                employee,
                "SECURITY-ADMIN-VIEW-" + System.nanoTime()
        );

        payroll = payrollRepository.saveAndFlush(payroll);

        String token = login("admin", "Admin@123");

        mockMvc.perform(
                        get("/api/payroll/" + payroll.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowEmployeeToViewOwnPayroll() throws Exception {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        Payroll payroll = createPayroll(
                employee,
                "SECURITY-EMPLOYEE-VIEW-" + System.nanoTime()
        );

        payroll = payrollRepository.saveAndFlush(payroll);

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        get("/api/payroll/" + payroll.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectEmployeeFromCreatingPayroll() throws Exception {

        String token = login("priya", "Priya@123");

        String payPeriod =
                "SECURITY-EMPLOYEE-CREATE-" + System.nanoTime();

        String requestBody = """
                {
                    "employeeId": 1,
                    "basicSalary": 50000,
                    "allowances": 5000,
                    "deductions": 5000,
                    "payPeriod": "%s"
                }
                """.formatted(payPeriod);

        mockMvc.perform(
                        post("/api/payroll")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectEmployeeFromDeletingPayroll() throws Exception {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow();

        Payroll payroll = createPayroll(
                employee,
                "SECURITY-DELETE-" + System.nanoTime()
        );

        payroll = payrollRepository.saveAndFlush(payroll);

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        delete("/api/payroll/" + payroll.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowEmployeeToViewOwnPayrollReport() throws Exception {

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        get("/api/payroll/reports/employee/1")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectEmployeeFromViewingAnotherEmployeePayrollReport()
            throws Exception {

        Employee employee = createSecondEmployee();

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        get("/api/payroll/reports/employee/" + employee.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowEmployeeToViewOwnEmployeePayrollList()
            throws Exception {

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        get("/api/payroll/employee/1")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectEmployeeFromViewingAnotherEmployeePayrollList()
            throws Exception {

        Employee employee = createSecondEmployee();

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        get("/api/payroll/employee/" + employee.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowHrToViewAllPayrolls() throws Exception {

        String token = login("hr", "Hr@123");

        mockMvc.perform(
                        get("/api/payroll")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToCreatePayroll() throws Exception {

        String token = login("admin", "Admin@123");

        String payPeriod =
                "SECURITY-ADMIN-CREATE-" + System.nanoTime();

        String requestBody = """
                {
                    "employeeId": 1,
                    "basicSalary": 60000,
                    "allowances": 10000,
                    "deductions": 7000,
                    "payPeriod": "%s"
                }
                """.formatted(payPeriod);

        mockMvc.perform(
                        post("/api/payroll")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void shouldAllowHrToCreatePayroll() throws Exception {

        String token = login("hr", "Hr@123");

        String payPeriod =
                "SECURITY-HR-CREATE-" + System.nanoTime();

        String requestBody = """
                {
                    "employeeId": 1,
                    "basicSalary": 60000,
                    "allowances": 10000,
                    "deductions": 7000,
                    "payPeriod": "%s"
                }
                """.formatted(payPeriod);

        mockMvc.perform(
                        post("/api/payroll")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectEmployeeFromCreatingEmployee() throws Exception {

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        post("/api/employees")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "employeeCode": "SECURITY-CREATE-001",
                                    "firstName": "Security",
                                    "lastName": "Test",
                                    "email": "security-create@example.com",
                                    "department": "IT",
                                    "designation": "Tester",
                                    "salary": 500000
                                }
                                """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectEmployeeFromUpdatingEmployee() throws Exception {

        Employee employee = createSecondEmployee();

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        put("/api/employees/" + employee.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "employeeCode": "SECURITY-UPDATE",
                                    "firstName": "Security",
                                    "lastName": "Updated",
                                    "email": "security-update@example.com",
                                    "department": "IT",
                                    "designation": "Tester",
                                    "salary": 500000
                                }
                                """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectEmployeeFromDeletingEmployee() throws Exception {

        Employee employee = createSecondEmployee();

        String token = login("priya", "Priya@123");

        mockMvc.perform(
                        delete("/api/employees/" + employee.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToDeleteEmployee() throws Exception {

        Employee employee = createSecondEmployee();

        String token = login("admin", "Admin@123");

        mockMvc.perform(
                        delete("/api/employees/" + employee.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());
    }

    private Payroll createPayroll(
            Employee employee,
            String payPeriod) {

        Payroll payroll = new Payroll();

        payroll.setEmployee(employee);
        payroll.setBasicSalary(new BigDecimal("60000"));
        payroll.setAllowances(new BigDecimal("10000"));
        payroll.setDeductions(new BigDecimal("7000"));
        payroll.setTaxAmount(new BigDecimal("7000"));
        payroll.setGrossSalary(new BigDecimal("70000"));
        payroll.setNetSalary(new BigDecimal("56000"));
        payroll.setPayPeriod(payPeriod);
        payroll.setStatus(PayrollStatus.DRAFT);

        return payroll;
    }

    private Employee createSecondEmployee() {

        Employee employee = new Employee();

        long uniqueId = System.nanoTime();

        employee.setEmployeeCode(
                "SEC-TEST-" + uniqueId
        );

        employee.setFirstName("Security");
        employee.setLastName("Test");

        employee.setEmail(
                "security-" + uniqueId + "@example.com"
        );

        employee.setDepartment("IT");
        employee.setDesignation("Test");
        employee.setSalary(
                new BigDecimal("500000")
        );

        return employeeRepository.saveAndFlush(employee);
    }

    private String login(
            String username,
            String password) throws Exception {

        String response = mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "username": "%s",
                                    "password": "%s"
                                }
                                """.formatted(username, password))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int tokenStart =
                response.indexOf("\"token\":\"") + 9;

        int tokenEnd =
                response.indexOf("\"", tokenStart);

        assertTrue(tokenStart > 8);
        assertTrue(tokenEnd > tokenStart);

        return response.substring(tokenStart, tokenEnd);
    }
}