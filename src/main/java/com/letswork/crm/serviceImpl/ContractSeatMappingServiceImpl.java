package com.letswork.crm.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.BulkSeatAssignmentRequestContract;
import com.letswork.crm.dtos.SeatAssignmentDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.ContractSeatMapping;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.ContractRepository;
import com.letswork.crm.repo.ContractSeatMappingRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SeatRepository;
import com.letswork.crm.service.ContractSeatMappingService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class ContractSeatMappingServiceImpl implements ContractSeatMappingService{
	
	@Autowired
	ContractSeatMappingRepository repo;
	
	@Autowired
	ContractRepository contractRepo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
    SeatRepository seatRepo;

	@Override
	public ContractSeatMapping saveOrUpdate(ContractSeatMapping mapping) {
		// TODO Auto-generated method stub
		Tenant tenant = tenantService.findTenantByCompanyId(mapping.getCompanyId());
	    if (tenant == null) {
	        throw new RuntimeException("Invalid CompanyId - " + mapping.getCompanyId());
	    }

	    Contract contract = contractRepo
	            .findByIdAndCompanyId(mapping.getContractId(), mapping.getCompanyId())
	            .orElseThrow(() -> new RuntimeException("Invalid Contract"));

	    LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
	            mapping.getLetsWorkCentre(), mapping.getCompanyId(), mapping.getCity(), mapping.getState());
	    if (centre == null) {
	        throw new RuntimeException("Invalid LetsWorkCentre");
	    }

	    Optional<Seat> seat = seatRepo.findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndStateAndPublishedTrue(
	            mapping.getSeatType(), mapping.getCompanyId(), mapping.getLetsWorkCentre(),
	            mapping.getSeatNumber(), mapping.getCity(), mapping.getState());

	    if (seat.isEmpty()) {
	        throw new RuntimeException("Seat does not exist or not published");
	    }

	    Optional<ContractSeatMapping> alreadyAssigned =
	            repo.findBySeatNumberAndSeatTypeAndLetsWorkCentreAndCompanyIdAndCityAndState(
	                    mapping.getSeatNumber(),
	                    mapping.getSeatType(),
	                    mapping.getLetsWorkCentre(),
	                    mapping.getCompanyId(),
	                    mapping.getCity(),
	                    mapping.getState()
	            );

	    if (alreadyAssigned.isPresent()) {
	        if (!alreadyAssigned.get().getContractId().equals(mapping.getContractId())) {
	            throw new RuntimeException("Seat " + mapping.getSeatNumber() + " is already assigned to another contract");
	        }
	    }

	    Optional<ContractSeatMapping> existingOpt = repo.findByFullBusinessKey(
	            mapping.getContractId(),
	            mapping.getLetsWorkCentre(),
	            mapping.getCompanyId(),
	            mapping.getCity(),
	            mapping.getState(),
	            mapping.getSeatType(),
	            mapping.getSeatNumber()
	    );

	    ModelMapper mapper = new ModelMapper();

	    if (existingOpt.isPresent()) {
	        ContractSeatMapping existing = existingOpt.get();
	        mapping.setId(existing.getId());
	        mapping.setUpdateDate(new Date());
	        mapper.map(mapping, existing);
	        return repo.save(existing);
	    } else {
	        mapping.setCreateDate(new Date());
	        return repo.save(mapping);
	    }
	}

	@Override
	public List<ContractSeatMapping> assignMultipleSeatsToContract(BulkSeatAssignmentRequestContract request) {
		// TODO Auto-generated method stub
		List<ContractSeatMapping> savedMappings = new ArrayList<>();

	    for (SeatAssignmentDto seatDto : request.getSeats()) {

	        ContractSeatMapping mapping = new ContractSeatMapping();
	        mapping.setContractId(request.getContractId());
	        mapping.setLetsWorkCentre(request.getLetsWorkCentre());
	        mapping.setCity(request.getCity());
	        mapping.setState(request.getState());
	        mapping.setCompanyId(request.getCompanyId());
	        mapping.setSeatType(seatDto.getSeatType());
	        mapping.setSeatNumber(seatDto.getSeatNumber());
	        mapping.setStartDate(request.getStartDate());
	        mapping.setEndDate(request.getEndDate());

	        ContractSeatMapping saved = this.saveOrUpdate(mapping);
	        savedMappings.add(saved);
	    }

	    return savedMappings;
	}

	@Override
	public List<ContractSeatMapping> getSeatsByContract(Long contractId, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByContractIdAndCompanyId(contractId, companyId);
	}

}
