package com.gov.rw.erp.repository;

import com.gov.rw.erp.entity.Employee;
import com.gov.rw.erp.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCode(String code);

    List<Employee> findByStatus(EmployeeStatus status);
}
