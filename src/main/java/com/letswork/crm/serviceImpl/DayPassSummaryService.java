package com.letswork.crm.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.CentreDayPassSummaryDto;
import com.letswork.crm.dtos.DayPassSummaryResponseDto;
import com.letswork.crm.repo.BuyDayPassBundleRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;

@Service
public class DayPassSummaryService {

    private final BuyDayPassBundleRepository buyRepo;
    private final NewUserRegisterRepository userRepo;

    public DayPassSummaryService(
            BuyDayPassBundleRepository buyRepo,
            NewUserRegisterRepository userRepo
    ) {
        this.buyRepo = buyRepo;
        this.userRepo = userRepo;
    }

    public DayPassSummaryResponseDto getSummary(
            String companyId,
            String email,
            String letsWorkCentre,
            String city,
            String state
    ) {

        List<CentreDayPassSummaryDto> centreWise =
                buyRepo.getCentreWiseSummary(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state
                );

        long total = centreWise.stream()
                .mapToLong(CentreDayPassSummaryDto::getTotalDayPassCredits)
                .sum();

        DayPassSummaryResponseDto response = new DayPassSummaryResponseDto();
        response.setTotalDayPassCredits(total);
        response.setLetsWorkCentreCredit(centreWise);

        return response;
    }
}
