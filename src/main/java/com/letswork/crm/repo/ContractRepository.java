package com.letswork.crm.repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Contract;
import com.letswork.crm.enums.ContractStatus;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByIdAndCompanyId(Long id, String companyId);

    @Query("SELECT c FROM Contract c " +
           "WHERE c.companyId = :companyId " +
           "AND (:letsWorkClientId IS NULL OR c.letsWorkClientId = :letsWorkClientId) " +
           "AND (:status IS NULL OR c.contractStatus = :status) " +
           "AND (:fromDate IS NULL OR c.startDate >= :fromDate) " +
           "AND (:toDate IS NULL OR c.endDate <= :toDate)")
    Page<Contract> filter(
            @Param("companyId") String companyId,
            @Param("letsWorkClientId") Long letsWorkClientId,
            @Param("status") ContractStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}
