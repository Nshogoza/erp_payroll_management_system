package com.gov.rw.erp.repository;

import com.gov.rw.erp.entity.Deduction;
import com.gov.rw.erp.entity.Employee;
import com.gov.rw.erp.enums.DeductionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    boolean existsByEmployeeAndMonthAndYear(Employee employee, int month, int year);

    boolean existsByCode(String code);

    List<Deduction> findByMonthAndYear(int month, int year);

    List<Deduction> findByMonthAndYearAndStatus(int month, int year, DeductionStatus status);

    List<Deduction> findByEmployee_Id(Long employeeId);

    Optional<Deduction> findByEmployee_IdAndMonthAndYear(Long employeeId, int month, int year);
}
