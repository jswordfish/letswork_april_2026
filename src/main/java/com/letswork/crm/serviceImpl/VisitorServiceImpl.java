package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Visitor;
import com.letswork.crm.repo.VisitorRepository;
import com.letswork.crm.service.VisitorService;



@Service
@Transactional
public class VisitorServiceImpl implements VisitorService {
	
	@Autowired
	VisitorRepository repo;

	@Override
	public String saveOrUpdate(Visitor visitor) {
		// TODO Auto-generated method stub
		
		Visitor vit = repo.findByNameAndEmail(visitor.getName(), visitor.getEmail());
		
		if(vit!=null) {
			
			vit.setName(visitor.getName());
			vit.setPhone(visitor.getPhone());
			vit.setEmail(visitor.getEmail());
			vit.setOneDayPass(visitor.getOneDayPass());
			
			
			repo.save(vit);
			return "record updated";
			
		}
		
		else {
			repo.save(visitor);
			return "record saved";
		}
		
	}

	@Override
	public List<Visitor> viewByDate(LocalDate visitDate) {
		// TODO Auto-generated method stub
		return repo.findByVisitDate(visitDate);
	}

	@Override
	public String deleteVisitor(Visitor visitor) {
		// TODO Auto-generated method stub
		
		Visitor vit = repo.findByNameAndEmail(visitor.getName(), visitor.getEmail());
		if(vit!=null) {
		repo.delete(visitor);
		return "record deleted";
		}
		else return "No visitor found";
		
	}
	
	private static final int PAGE_SIZE = 10; 

    @Override
    public PaginatedResponseDto viewByDate(LocalDate visitDate, String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<Visitor> visitorPage = repo.findByVisitDate(visitDate, companyId, pageable);

        return buildPaginatedResponse(visitorPage, page);
    }

    
    private PaginatedResponseDto buildPaginatedResponse(Page<Visitor> visitorPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) visitorPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) visitorPage.getTotalElements());
        response.setTotalNumberOfPages(visitorPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(visitorPage.getContent());
        return response;
    }

}
