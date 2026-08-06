package com.letswork.crm.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	@Query("SELECT o FROM Offers o WHERE o.name = :name AND o.companyId = :companyId AND (o.deleted = false OR o.deleted IS NULL)")
    Optional<Offers> findByNameAndCompanyId(
        @Param("name") String name, 
        @Param("companyId") String companyId
    );

	Optional<Offers> findByCodeAndCompanyIdAndActiveTrue(
	        String code,
	        String companyId
	);
	
	@Query("SELECT o FROM Offers o WHERE o.code = :code AND o.companyId = :companyId AND (o.deleted = false OR o.deleted IS NULL)")
    Optional<Offers> findByCodeAndCompanyId(
        @Param("code") String code, 
        @Param("companyId") String companyId
    );
	
	Page<Offers> findAllByCompanyId(String companyId, Pageable pageable);

	Page<Offers> findAllByCompanyIdAndOfferType(String companyId, OfferType offerType, Pageable pageable);

	List<Offers> findByCompanyIdAndActiveTrue(String companyId);
	
	@Query("SELECT o FROM Offers o WHERE o.companyId = :companyId AND (o.deleted = false OR o.deleted IS NULL)")
    List<Offers> findByCompanyId(@Param("companyId") String companyId);

	List<Offers> findAllByCompanyIdAndActiveTrue(String companyId);
	
	@Query("SELECT o FROM Offers o WHERE o.companyId = :companyId AND (o.deleted = false OR o.deleted IS NULL)")
    List<Offers> findAllByCompanyId(@Param("companyId") String companyId);

	List<Offers> findAllByCompanyIdAndOfferTypeAndActiveTrue(
	        String companyId,
	        OfferType offerType
	);
	
	@Query("SELECT o FROM Offers o WHERE o.companyId = :companyId " +
		       "AND (o.deleted = false OR o.deleted IS NULL) " +
		       "AND (" +
		       "    LOWER(o.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       " OR LOWER(o.code) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       " OR LOWER(o.minDiscountValue) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       " OR CAST(o.discount AS string) LIKE CONCAT('%', :search, '%') " +
		       " OR LOWER(CAST(o.offerType AS string)) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       " OR CAST(o.active AS string) LIKE CONCAT('%', :search, '%') " +
		       " OR CAST(o.startDate AS string) LIKE CONCAT('%', :search, '%') " +
		       " OR CAST(o.endDate AS string) LIKE CONCAT('%', :search, '%')" +
		       ")")
	Page<Offers> searchOffers(
		    @Param("companyId") String companyId,
		    @Param("search") String search,
		    Pageable pageable
		);
	
	@Query("SELECT o FROM Offers o WHERE o.companyId = :companyId AND o.offerType = :offerType AND (o.deleted = false OR o.deleted IS NULL)")
    List<Offers> findAllByCompanyIdAndOfferType(
        @Param("companyId") String companyId, 
        @Param("offerType") OfferType offerType
    );
	
	@Query("SELECT o FROM Offers o WHERE (o.active = true OR o.active IS NULL) AND o.endDate < :now AND (o.deleted = false OR o.deleted IS NULL)")
    List<Offers> findExpiredOffers(@Param("now") LocalDateTime now);
    
}
