package com.example.features.payment.campay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CampayWithdrawResponse {
    private String reference;
    private String status;
    private String operator;
    @JsonProperty("external_reference")
    private String externalReference;
}

