package com.example.features.user.application.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateInvitationResponseDto {

    private String invitationCode;

    private LocalDateTime expiresAt;

    private String locataireReference;

    private boolean emailSent;
}
