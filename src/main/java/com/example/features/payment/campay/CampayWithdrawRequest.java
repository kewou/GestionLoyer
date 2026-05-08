package com.example.features.payment.campay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampayWithdrawRequest {
    private String amount;
    private String currency;
    private String to;
    private String description;
    @JsonProperty("external_reference")
    private String externalReference;
}

