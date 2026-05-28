package com.gov.rw.erp.repository;

import com.gov.rw.erp.entity.Employee;
import com.gov.rw.erp.entity.Employment;
import com.gov.rw.erp.enums.EmployeeStatus;
import com.gov.rw.erp.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {

    Optional<Employment> findByEmployee(Employee employee);

    Optional<Employment> findByEmployee_Id(Long employeeId);

    boolean existsByEmployee(Employee employee);

    boolean existsByCode(String code);

    List<Employment> findByStatus(EmploymentStatus status);

    List<Employment> findByStatusAndEmployee_Status(EmploymentStatus employmentStatus, EmployeeStatus employeeStatus);
}
