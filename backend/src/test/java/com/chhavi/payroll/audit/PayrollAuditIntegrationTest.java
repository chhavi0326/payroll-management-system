package com.chhavi.payroll.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PayrollAuditIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRecordCompletePayrollAuditLifecycle()
            throws Exception {

        String token = login(
                "admin",
                "Admin@123"
        );

        String payPeriod =
                "AUDIT-TEST-" + System.nanoTime();

        String createResponse =
                mockMvc.perform(
                                post("/api/payroll")
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content("""
                                        {
                                            "employeeId": 1,
                                            "basicSalary": 60000,
                                            "allowances": 10000,
                                            "deductions": 7000,
                                            "payPeriod": "%s"
                                        }
                                        """.formatted(payPeriod))
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        long payrollId = extractId(createResponse);

        String createdAudit =
                mockMvc.perform(
                                get(
                                        "/api/payroll/"
                                                + payrollId
                                                + "/audit"
                                )
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertTrue(
                createdAudit.contains("\"action\":\"CREATED\"")
        );

        mockMvc.perform(
                        put(
                                "/api/payroll/"
                                        + payrollId
                                        + "/process"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put(
                                "/api/payroll/"
                                        + payrollId
                                        + "/pay"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());

        String auditBeforeDelete =
                mockMvc.perform(
                                get(
                                        "/api/payroll/"
                                                + payrollId
                                                + "/audit"
                                )
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertTrue(
                auditBeforeDelete.contains(
                        "\"action\":\"CREATED\""
                )
        );

        assertTrue(
                auditBeforeDelete.contains(
                        "\"action\":\"PROCESSED\""
                )
        );

        assertTrue(
                auditBeforeDelete.contains(
                        "\"action\":\"PAID\""
                )
        );

        assertTrue(
                auditBeforeDelete.contains(
                        "\"username\":\"admin\""
                )
        );

        mockMvc.perform(
                        delete(
                                "/api/payroll/" + payrollId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());

        String auditAfterDelete =
                mockMvc.perform(
                                get(
                                        "/api/payroll/"
                                                + payrollId
                                                + "/audit"
                                )
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertTrue(
                auditAfterDelete.contains(
                        "\"action\":\"CREATED\""
                )
        );

        assertTrue(
                auditAfterDelete.contains(
                        "\"action\":\"PROCESSED\""
                )
        );

        assertTrue(
                auditAfterDelete.contains(
                        "\"action\":\"PAID\""
                )
        );

        assertTrue(
                auditAfterDelete.contains(
                        "\"action\":\"DELETED\""
                )
        );
    }

    @Test
    void shouldAllowAdminToViewUserAuditHistory()
            throws Exception {

        String token = login(
                "admin",
                "Admin@123"
        );

        mockMvc.perform(
                        get("/api/payroll/audit/user/admin")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowHrToViewUserAuditHistory()
            throws Exception {

        String token = login(
                "hr",
                "Hr@123"
        );

        mockMvc.perform(
                        get("/api/payroll/audit/user/admin")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectEmployeeFromViewingUserAuditHistory()
            throws Exception {

        String token = login(
                "priya",
                "Priya@123"
        );

        mockMvc.perform(
                        get("/api/payroll/audit/user/admin")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectUnauthenticatedAuditRequest()
            throws Exception {

        mockMvc.perform(
                        get("/api/payroll/999999/audit")
                )
                .andExpect(status().isForbidden());
    }

    private String login(
            String username,
            String password) throws Exception {

        String response =
                mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s"
                                        }
                                        """.formatted(
                                                username,
                                                password
                                        ))
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        int tokenStart =
                response.indexOf("\"token\":\"") + 9;

        int tokenEnd =
                response.indexOf(
                        "\"",
                        tokenStart
                );

        assertTrue(tokenStart > 8);
        assertTrue(tokenEnd > tokenStart);

        return response.substring(
                tokenStart,
                tokenEnd
        );
    }

    private long extractId(String response) {

        int idStart =
                response.indexOf("\"id\":") + 5;

        int idEnd =
                response.indexOf(
                        ",",
                        idStart
                );

        if (idEnd == -1) {
            idEnd =
                    response.indexOf(
                            "}",
                            idStart
                    );
        }

        return Long.parseLong(
                response.substring(
                        idStart,
                        idEnd
                )
        );
    }
}