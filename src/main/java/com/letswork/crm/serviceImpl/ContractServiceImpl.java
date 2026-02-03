package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.Date;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.ContractStatus;
import com.letswork.crm.repo.ContractRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.ContractService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepo;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private LetsWorkClientRepository letsWorkClientRepo;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public Contract saveOrUpdate(Contract contract) {

        Tenant tenant = tenantService.findTenantByCompanyId(contract.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId - " + contract.getCompanyId());
        }

        LetsWorkClient client = letsWorkClientRepo
                .findByIdAndCompanyId(contract.getLetsWorkClientId(), contract.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Invalid LetsWorkClient ID"));

        if (contract.getId() != null) {
            Contract existing = contractRepo
                    .findByIdAndCompanyId(contract.getId(), contract.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Contract not found"));

            contract.setCreateDate(existing.getCreateDate());
            contract.setUpdateDate(new Date());

            mapper.map(contract, existing);
            return contractRepo.save(existing);
        } 
        else {
            contract.setCreateDate(new Date());
            return contractRepo.save(contract);
        }
    }

    @Override
    public PaginatedResponseDto getPaginated(
            String companyId,
            Long letsWorkClientId,
            ContractStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Contract> resultPage = contractRepo.filter(
                companyId,
                letsWorkClientId,
                status,
                fromDate,
                toDate,
                pageable
        );

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setSelectedPage(page);
        dto.setTotalNumberOfRecords((int) resultPage.getTotalElements());
        dto.setTotalNumberOfPages(resultPage.getTotalPages());
        dto.setRecordsFrom(page * size + 1);
        dto.setRecordsTo(
                Math.min((page + 1) * size, (int) resultPage.getTotalElements())
        );
        dto.setList(resultPage.getContent());

        return dto;
    }
}
