package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Referral;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.ReferralRepository;
import com.letswork.crm.service.ReferralService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class ReferralServiceImpl implements ReferralService {

    @Autowired
    private ReferralRepository referralRepo;

    @Autowired
    private NewUserRegisterRepository userRepo;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ModelMapper mapper;

    @Override
    public Referral saveOrUpdate(Referral referral) {

        Tenant tenant =
                tenantService.findTenantByCompanyId(referral.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException(
                    "Invalid companyId - " + referral.getCompanyId()
            );
        }
        
        NewUserRegister user =
                userRepo.findByEmailAndCompanyId(
                        referral.getEmailOfUser(),
                        referral.getCompanyId()
                ).orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Optional<Referral> existing =
                referralRepo.findByEmailAndEmailOfUserAndCompanyId(
                        referral.getEmail(),
                        referral.getEmailOfUser(),
                        referral.getCompanyId()
                );

        if (existing.isPresent()) {
            Referral dbReferral = existing.get();
            referral.setId(dbReferral.getId());
            referral.setCreateDate(dbReferral.getCreateDate());
            referral.setUpdateDate(new Date());

            mapper.map(referral, dbReferral);
            return referralRepo.save(dbReferral);
        }

        referral.setCreateDate(new Date());
        return referralRepo.save(referral);
    }

    @Override
    public Page<Referral> getReferrals(
            String companyId,
            String email,
            String name,
            String emailOfUser,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {
        return referralRepo.filter(
                companyId,
                email,
                name,
                emailOfUser,
                fromDate,
                toDate,
                pageable
        );
    }

    @Override
    public Referral updateJoiningDate(
            Long referralId,
            LocalDate joiningDate,
            String companyId
    ) {
        Referral referral =
                referralRepo.findById(referralId)
                        .orElseThrow(() ->
                                new RuntimeException("Referral not found")
                        );

        if (!companyId.equals(referral.getCompanyId())) {
            throw new RuntimeException("Unauthorized access");
        }

        referral.setJoiningDate(joiningDate);
        referral.setUpdateDate(new Date());
        return referralRepo.save(referral);
    }

    @Override
    public boolean isEligibleForBonus(Long referralId, String companyId) {

        Referral referral =
                referralRepo.findById(referralId)
                        .orElseThrow(() ->
                                new RuntimeException("Referral not found")
                        );

        if (!companyId.equals(referral.getCompanyId())) {
            throw new RuntimeException("Unauthorized access");
        }

        if (referral.getJoiningDate() == null) {
            return false;
        }

        return referral.getJoiningDate()
                .plusMonths(1)
                .isBefore(LocalDate.now())
                && !Boolean.TRUE.equals(referral.getReceivedBonus());
    }

    @Override
    public void giveBonus(Long referralId, String companyId) {

        Referral referral =
                referralRepo.findById(referralId)
                        .orElseThrow(() ->
                                new RuntimeException("Referral not found")
                        );

        if (!isEligibleForBonus(referralId, companyId)) {
            throw new RuntimeException("Referral not eligible for bonus");
        }

        NewUserRegister user =
                userRepo.findByEmailAndCompanyId(
                        referral.getEmailOfUser(),
                        companyId
                ).orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        user.setFreeConferenceCredits(
                user.getFreeConferenceCredits() + 4
        );

        userRepo.save(user);

        referral.setReceivedBonus(true);
        referral.setUpdateDate(new Date());
        referralRepo.save(referral);
    }
}
