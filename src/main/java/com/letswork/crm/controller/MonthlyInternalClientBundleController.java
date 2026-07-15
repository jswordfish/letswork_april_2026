package com.letswork.crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.serviceImpl.MonthlyInternalClientBundleScheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/monthly-bundles")
@RequiredArgsConstructor
@Slf4j
public class MonthlyInternalClientBundleController {

    private final MonthlyInternalClientBundleScheduler scheduler;

    /**
     * Manually triggers the monthly internal-client bundle generation for the CURRENT month.
     * Intended for admin/testing use — the real trigger is the scheduled cron job.
     */
    @PostMapping("/run")
    public ResponseEntity<String> runNow(@RequestParam String token) {
        log.info("Manual trigger received for monthly internal client bundle generation");
        try {
            scheduler.generateMonthlyInternalClientBundles();
            return ResponseEntity.ok("Monthly bundle generation completed successfully. Check logs for details.");
        } catch (Exception e) {
            log.error("Manual monthly bundle generation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Monthly bundle generation failed: " + e.getMessage());
        }
    }
}
