package com.letswork.crm.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Offers;
import com.letswork.crm.enums.OfferType;

@Repository
public interface OffersRepository extends JpaRepository<Offers, Long> {

	Optional<Offers> findByNameAndCompanyIdAndActiveTrue(
	        String name,
	        String companyId
	);

	Optional<Offers> findByCodeAndCompanyIdAndActiveTrue(
	        String code,
	        String companyId
	);

	List<Offers> findByCompanyIdAndActiveTrue(String companyId);

	List<Offers> findAllByCompanyIdAndActiveTrue(String companyId);

	List<Offers> findAllByCompanyIdAndOfferTypeAndActiveTrue(
	        String companyId,
	        OfferType offerType
	);
	
	@Query("SELECT o FROM Offers o " +
		       "WHERE (o.active = true OR o.active IS NULL) " +
		       "AND o.endDate < :now")
		List<Offers> findExpiredOffers(@Param("now") LocalDateTime now);
    
}
