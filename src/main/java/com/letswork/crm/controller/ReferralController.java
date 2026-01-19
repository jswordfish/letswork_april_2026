package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.Referral;
import com.letswork.crm.service.ReferralService;

@RestController
@RequestMapping("/referrals")
public class ReferralController {

    @Autowired
    private ReferralService referralService;

    @PostMapping
    public ResponseEntity<Referral> saveOrUpdate(
            @RequestBody Referral referral,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                referralService.saveOrUpdate(referral)
        );
    }

    @GetMapping
    public Page<Referral> getReferrals(
            @RequestParam String companyId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String emailOfUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate toDate,
            Pageable pageable,
            @RequestParam String token
    ) {
        return referralService.getReferrals(
                companyId,
                email,
                name,
                emailOfUser,
                fromDate,
                toDate,
                pageable
        );
    }

    @PutMapping("/{id}/joining-date")
    public ResponseEntity<Referral> updateJoiningDate(
            @PathVariable Long id,
            @RequestParam String companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate joiningDate,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                referralService.updateJoiningDate(id, joiningDate, companyId)
        );
    }

    @GetMapping("/{id}/eligible")
    public ResponseEntity<Boolean> isEligible(
            @PathVariable Long id,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                referralService.isEligibleForBonus(id, companyId)
        );
    }

    @PostMapping("/{id}/give-bonus")
    public ResponseEntity<String> giveBonus(
            @PathVariable Long id,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        referralService.giveBonus(id, companyId);
        return ResponseEntity.ok("Bonus credited successfully");
    }
}
