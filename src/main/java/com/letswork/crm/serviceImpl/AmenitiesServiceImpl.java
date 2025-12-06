package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.AmenityType;
import com.letswork.crm.repo.AmenitiesRepository;
import com.letswork.crm.service.AmenitiesService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class AmenitiesServiceImpl implements AmenitiesService {

    @Autowired
    private AmenitiesRepository repo;

    @Autowired
    private TenantService tenantService;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public Amenities saveOrUpdate(Amenities amenities) {

        // Validate companyId
        Tenant tenant = tenantService.findTenantByCompanyId(amenities.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId - " + amenities.getCompanyId());
        }

        // Check if record exists (business key = name + companyId)
        Amenities existing = repo.findByNameAndCompanyId(
                amenities.getName(),
                amenities.getCompanyId()
        );

        if (existing != null) {
            amenities.setId(existing.getId());
            amenities.setCreateDate(existing.getCreateDate());
            amenities.setUpdateDate(new Date());
            mapper.map(amenities, existing);
            return repo.save(existing);
        } else {
            amenities.setCreateDate(new Date());
            return repo.save(amenities);
        }
    }

    @Override
    public List<Amenities> listByAmenityType(String companyId, AmenityType type) {
        return repo.findByAmenityTypeAndCompanyId(type, companyId);
    }

    @Override
    public void deleteAmenity(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Amenity not found with id = " + id);
        }
        repo.deleteById(id);
    }

	@Override
	public List<Amenities> listByCompanyId(String companyId) {
		// TODO Auto-generated method stub
		return repo.findByCompanyId(companyId);
	}
}
