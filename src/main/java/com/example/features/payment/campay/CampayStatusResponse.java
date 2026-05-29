package com.example.features.payment.campay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CampayStatusResponse {
    private String reference;
    private String status; // SUCCESSFUL, FAILED, PENDING
    private String amount;
    private String currency;
    private String operator;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("external_reference")
    private String externalReference;
    private String description;
    @JsonProperty("verify_token")
    private String verifyToken;
}

