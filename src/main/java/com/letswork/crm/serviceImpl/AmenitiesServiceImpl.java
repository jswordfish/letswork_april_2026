package com.letswork.crm.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    
    @Autowired
    S3Service s3Service;
    
    private String bucketName = "letsworkcentres";

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public Amenities saveOrUpdate(
            Amenities amenities,
            MultipartFile image
    ) throws IOException {

        Tenant tenant =
                tenantService.findTenantByCompanyId(
                        amenities.getCompanyId()
                );

        if (tenant == null) {
            throw new RuntimeException(
                    "Invalid companyId - " + amenities.getCompanyId()
            );
        }

        Amenities existing =
                repo.findByNameAndCompanyId(
                        amenities.getName(),
                        amenities.getCompanyId()
                );

        Amenities saved;

        if (existing != null) {

        	mapper.map(amenities, existing);
            existing.setUpdateDate(new Date());

            saved = repo.save(existing);


        } else {

            amenities.setCreateDate(new Date());
            amenities.setUpdateDate(new Date());

            saved = repo.save(amenities);
        }

        if (image != null && !image.isEmpty()) {

            File tempFile =
                    File.createTempFile(
                            "amenity_",
                            image.getOriginalFilename()
                    );

            image.transferTo(tempFile);

            String s3Url =
                    s3Service.uploadAmenityImage(
                            bucketName,
                            saved.getCompanyId(),
                            saved.getName(),
                            image.getOriginalFilename(),
                            tempFile
                    );

            saved.setS3Path(s3Url);
            repo.save(saved);

            tempFile.delete();
        }

        return saved;
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
