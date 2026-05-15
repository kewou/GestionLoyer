package com.example.features.user.domain.services.impl;

import com.example.exceptions.BusinessException;
import com.example.features.common.mail.MessageDto;
import com.example.features.common.mail.MessageService;
import com.example.features.user.application.appService.VirtualLocataireAppService;
import com.example.features.user.application.mapper.ClaimAccountDto;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.ClientMapper;
import com.example.features.user.application.mapper.CreateVirtualLocataireDto;
import com.example.features.user.application.mapper.GenerateInvitationResponseDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import com.example.security.Role;
import com.example.utils.GeneralUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;
import static com.example.exceptions.BusinessException.BusinessErrorType.OTHER;

@Service
@Slf4j
@Transactional
public class VirtualLocataireService implements VirtualLocataireAppService {

    private final ClientRepository clientRepository;
    private final MessageService messageService;
    private final ClientMapper clientMapper;
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;

    @Value("${invitation.code.ttl-days:7}")
    private long invitationTtlDays;

    @Value("${invitation.subject:GestionLoyer : invitation a activer votre compte}")
    private String invitationSubject;

    @Value("${invitation.message:}")
    private String invitationMessage;

    @Value("${invitation.validation.uri:https://beezyweb.net}")
    private String uriSite;

    @Value("${inscription.sender:noreply@beezyweb.net}")
    private String invitationSender;

    public VirtualLocataireService(ClientRepository clientRepository,
                                   MessageService messageService,
                                   ClientMapper clientMapper,
                                   ClientService clientService,
                                   PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.messageService = messageService;
        this.clientMapper = clientMapper;
        this.clientService = clientService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientDto createVirtual(CreateVirtualLocataireDto dto, String bailleurEmail) throws BusinessException {
        Client bailleur = requireBailleur(bailleurEmail);

        String email = normalizeEmail(dto.getEmail());
        if (email != null) {
            if (clientRepository.findRealClientByEmail(email).isPresent()) {
                throw new BusinessException(
                        String.format("Un compte existe deja avec l'email %s", email), OTHER);
            }
            if (clientRepository.findVirtualByEmailAndBailleur(email, bailleur.getReference()).isPresent()) {
                throw new BusinessException(
                        String.format("Vous avez deja cree un locataire virtuel avec l'email %s", email), OTHER);
            }
        }

        Set<String> roles = new HashSet<>();
        roles.add(Role.LOCATAIRE.name());

        Client virtual = Client.builder()
                .reference(GeneralUtils.generateReference())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .email(email)
                .password(null)
                .isEnabled(Boolean.FALSE)
                .isVirtual(Boolean.TRUE)
                .createdByBailleurRef(bailleur.getReference())
                .roles(roles)
                .logements(new HashSet<>())
                .baux(new HashSet<>())
                .build();

        clientRepository.save(virtual);
        log.info("Locataire virtuel {} cree par bailleur {}", virtual.getReference(), bailleur.getReference());
        return clientMapper.dto(virtual);
    }

    @Override
    public GenerateInvitationResponseDto generateInvitation(String refLocataire, String bailleurEmail) throws BusinessException {
        Client bailleur = requireBailleur(bailleurEmail);
        Client virtual = clientRepository.findByReference(refLocataire)
                .orElseThrow(() -> new BusinessException(
                        String.format("Locataire %s introuvable", refLocataire), NOT_FOUND));

        if (!Boolean.TRUE.equals(virtual.getIsVirtual())) {
            throw new BusinessException("Ce locataire n'est pas un compte virtuel", OTHER);
        }
        if (!bailleur.getReference().equals(virtual.getCreatedByBailleurRef())) {
            throw new BusinessException("Vous n'etes pas le createur de ce locataire virtuel", OTHER);
        }
        if (virtual.getClaimedAt() != null) {
            throw new BusinessException("Ce compte a deja ete revendique", OTHER);
        }

        String code = GeneralUtils.generateInvitationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(invitationTtlDays);
        virtual.setInvitationCode(code);
        virtual.setInvitationCodeExpiresAt(expiresAt);
        clientRepository.save(virtual);

        boolean emailSent = false;
        if (virtual.getEmail() != null && !virtual.getEmail().isBlank()) {
            emailSent = sendInvitationMail(virtual, code, bailleur, expiresAt);
        }

        return GenerateInvitationResponseDto.builder()
                .invitationCode(code)
                .expiresAt(expiresAt)
                .locataireReference(virtual.getReference())
                .emailSent(emailSent)
                .build();
    }

    @Override
    public List<ClientDto> listMyVirtualLocataires(String bailleurEmail) throws BusinessException {
        Client bailleur = requireBailleur(bailleurEmail);
        return clientRepository.findVirtualLocatairesByBailleur(bailleur.getReference()).stream()
                .map(clientMapper::dto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDto claim(ClaimAccountDto dto) throws BusinessException {
        Client virtual = clientRepository.findByInvitationCode(dto.getInvitationCode())
                .orElseThrow(() -> new BusinessException("Code d'invitation invalide", NOT_FOUND));

        if (!Boolean.TRUE.equals(virtual.getIsVirtual())) {
            throw new BusinessException("Ce compte n'est pas un compte virtuel", OTHER);
        }
        if (virtual.getClaimedAt() != null) {
            throw new BusinessException("Ce code a deja ete utilise", OTHER);
        }
        if (virtual.getInvitationCodeExpiresAt() == null
                || virtual.getInvitationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Ce code d'invitation a expire", OTHER);
        }

        String email = normalizeEmail(dto.getEmail());
        if (email == null) {
            throw new BusinessException("L'email est requis pour activer le compte", OTHER);
        }
        Client existing = clientRepository.findByEmail(email);
        if (existing != null && !existing.getId().equals(virtual.getId())) {
            throw new BusinessException(
                    "Un autre compte utilise deja cet email. Connectez-vous avec ce compte.", OTHER);
        }

        virtual.setEmail(email);
        virtual.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            virtual.setPhone(dto.getPhone());
        }
        virtual.setIsVirtual(Boolean.FALSE);
        virtual.setClaimedAt(LocalDateTime.now());
        virtual.setInvitationCode(null);
        virtual.setInvitationCodeExpiresAt(null);
        virtual.setVerificationToken(GeneralUtils.generateVerificationToken());
        virtual.setIsEnabled(Boolean.FALSE);

        clientRepository.save(virtual);
        log.info("Locataire virtuel {} revendique par {}", virtual.getReference(), email);

        clientService.sendInscriptionMail(virtual);
        return clientMapper.dto(virtual);
    }

    private Client requireBailleur(String bailleurEmail) throws BusinessException {
        if (bailleurEmail == null) {
            throw new BusinessException("Bailleur non authentifie", OTHER);
        }
        Client bailleur = clientRepository.findByEmail(bailleurEmail);
        if (bailleur == null) {
            throw new BusinessException("Bailleur introuvable", NOT_FOUND);
        }
        if (bailleur.getRoles() == null || !bailleur.getRoles().contains(Role.BAILLEUR.name())) {
            throw new BusinessException("L'utilisateur connecte n'est pas un bailleur", OTHER);
        }
        return bailleur;
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim().toLowerCase();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean sendInvitationMail(Client virtual, String code, Client bailleur, LocalDateTime expiresAt) {
        String message;
        String claimLink = String.format("%s/claim?code=%s", uriSite, code);
        String bailleurName = String.format("%s %s",
                bailleur.getName() != null ? bailleur.getName() : "",
                bailleur.getLastName() != null ? bailleur.getLastName() : "").trim();
        if (invitationMessage != null && !invitationMessage.isBlank()) {
            try {
                message = String.format(
                        invitationMessage,
                        String.format("%s %s", virtual.getName(), virtual.getLastName()),
                        bailleurName,
                        code,
                        claimLink,
                        expiresAt.toString()
                );
            } catch (IllegalFormatException ex) {
                log.warn("Invalid invitation.message template, using fallback", ex);
                message = String.format("Bonjour, %s vous a cree un compte. Code d'activation : %s. Lien : %s",
                        bailleurName, code, claimLink);
            }
        } else {
            message = String.format("Bonjour, %s vous a cree un compte. Code d'activation : %s. Lien : %s",
                    bailleurName, code, claimLink);
        }
        try {
            messageService.sendHtmlMessage(MessageDto.builder()
                    .subject(invitationSubject)
                    .sender(invitationSender)
                    .recipients(List.of(virtual.getEmail()))
                    .message(message)
                    .build());
            return true;
        } catch (MessagingException | RuntimeException e) {
            log.error("Erreur lors de l'envoi de l'email d'invitation", e);
            return false;
        }
    }
}
