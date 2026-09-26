package com.chhavi.payroll.repository;

import com.chhavi.payroll.entity.PayrollAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollAuditRepository
        extends JpaRepository<PayrollAudit, Long> {

    List<PayrollAudit> findByPayrollIdOrderByTimestampAsc(
            Long payrollId
    );

    List<PayrollAudit> findByUsernameOrderByTimestampDesc(
            String username
    );
}