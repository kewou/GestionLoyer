package com.example.features.payment.campay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampayCollectRequest {
    private String amount;
    private String currency;
    private String from;
    private String description;
    @JsonProperty("external_reference")
    private String externalReference;
    @JsonProperty("redirect_url")
    private String redirectUrl;
    private String webhook;
}

