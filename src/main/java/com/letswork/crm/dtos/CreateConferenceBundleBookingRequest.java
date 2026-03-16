package com.letswork.crm.dtos;

import lombok.Data;

@Data
public class CreateConferenceBundleBookingRequest {
    private Long clientId;
    private Long bundleId;
}
