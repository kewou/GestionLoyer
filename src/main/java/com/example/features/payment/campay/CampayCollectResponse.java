package com.example.features.payment.campay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CampayCollectResponse {
    private String reference;
    @JsonProperty("ussd_code")
    private String ussdCode;
    private String status;
    private String operator;
}

