package com.example.features.payment.campay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service HTTP dédié aux appels vers l'API Campay.
 * Gère l'obtention du token, les collectes (dépôts) et les retraits.
 */
@Service
@Slf4j
public class CampayApiService {

    @Value("${campay.base-url:https://demo.campay.net/api}")
    private String baseUrl;

    @Value("${campay.username:}")
    private String username;

    @Value("${campay.password:}")
    private String password;

    @Value("${campay.default-country-code:237}")
    private String defaultCountryCode;

    @Value("${campay.local-number-min-length:9}")
    private int localNumberMinLength;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Obtient un token d'accès Campay.
     */
    public String getToken() {
        String url = baseUrl + "/token/";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Build body as JSON-compatible map
            var body = new java.util.HashMap<String, String>();
            body.put("username", username);
            body.put("password", password);

            HttpEntity<java.util.Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<CampayTokenResponse> responseEntity = restTemplate.postForEntity(url, request, CampayTokenResponse.class);
            CampayTokenResponse response = responseEntity.getBody();

            if (response == null || response.getToken() == null) {
                log.error("Token Campay introuvable dans la réponse (status={})", responseEntity.getStatusCode());
                throw new RuntimeException("Token Campay introuvable dans la réponse");
            }
            return response.getToken();
        } catch (RestClientException e) {
            log.error("Erreur lors de l'obtention du token Campay", e);
            throw new RuntimeException("Impossible de s'authentifier auprès de Campay", e);
        }
    }

    /**
     * Initie une collecte (demande de paiement) via Orange Money.
     */
    public CampayCollectResponse collect(CampayCollectRequest request) {
        String token = getToken();
        String url = baseUrl + "/collect/";
        try {
            // Normaliser le numéro de téléphone pour s'assurer qu'il contient le code pays
            String from = request.getFrom();
            try {
                String normalized = normalizePhone(from);
                request.setFrom(normalized);
            } catch (IllegalArgumentException iae) {
                log.error("Numéro de téléphone invalide pour la collecte: {}", from);
                throw new RuntimeException(iae.getMessage(), iae);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Token " + token);
            // Sérialiser explicitement le corps en JSON pour être conforme à l'exemple OkHttp
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody;
            try {
                jsonBody = mapper.writeValueAsString(request);
            } catch (JsonProcessingException jpe) {
                log.error("Erreur de sérialisation JSON pour la requête collect", jpe);
                throw new RuntimeException("Erreur lors de la préparation de la requête Campay collect", jpe);
            }

            // Log the normalized phone and JSON payload to help debug ER101
            log.debug("Campay collect - payload: {}", jsonBody);
            log.debug("Campay collect - normalized from: {}", request.getFrom());

            HttpEntity<String> httpRequest = new HttpEntity<>(jsonBody, headers);
            try {
                ResponseEntity<CampayCollectResponse> responseEntity = restTemplate.postForEntity(url, httpRequest, CampayCollectResponse.class);
                CampayCollectResponse response = responseEntity.getBody();
                if (response == null) {
                    throw new RuntimeException("Réponse Campay collect vide");
                }
                log.info("Campay collect initié : reference={}", response.getReference());
                return response;
            } catch (HttpClientErrorException hcee) {
                // Log server response body to diagnose errors like ER101
                String respBody = hcee.getResponseBodyAsString();
                log.error("Campay API error during collect: status={}, body={}", hcee.getStatusCode(), respBody);
                throw new RuntimeException("Campay API error: " + respBody, hcee);
            }
        } catch (RestClientException e) {
            log.error("Erreur lors de l'appel Campay collect", e);
            throw new RuntimeException("Erreur lors de l'initiation du paiement Orange Money", e);
        }
    }

    /**
     * Normalise un numéro de téléphone en supprimant espaces/caractères non numériques
     * et en ajoutant le code pays par défaut si nécessaire.
     * Exemples acceptés en entrée: "06xxxxxxxx", "+2376xxxxxxxx", "2376xxxxxxxx", "6xxxxxxxx"
     * Sortie: digits uniquement, ex: "2376xxxxxxxx" (sans "+").
     * Lance IllegalArgumentException si le numéro est invalide.
     */
    private String normalizePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Numéro de téléphone absent");
        }
        // Garder uniquement les chiffres
        String digits = phone.replaceAll("\\D+", "");

        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Numéro de téléphone invalide : aucun chiffre trouvé");
        }

        // Enlever les zéros initiaux redondants (ex: 06... -> 6...)
        while (digits.startsWith("0") && digits.length() > 1) {
            digits = digits.substring(1);
        }

        // Si le numéro commence déjà par le code pays, ok
        if (digits.startsWith(defaultCountryCode)) {
            return digits;
        }

        // Sinon, préfixer par le code pays
        String normalized = defaultCountryCode + digits;

        // Contrôle basique de longueur : au moins localNumberMinLength chiffres après le code pays
        if (normalized.length() < defaultCountryCode.length() + localNumberMinLength) {
            throw new IllegalArgumentException("Numéro de téléphone invalide après normalisation : '" + normalized + "'. Format attendu : '" + defaultCountryCode + "' + numéro local (au moins " + localNumberMinLength + " chiffres). Exemple: '" + defaultCountryCode + "6XXXXXXXX' ");
        }
        return normalized;
    }

    /**
     * Initie un retrait (transfert vers un compte Orange Money).
     */
    public CampayWithdrawResponse withdraw(CampayWithdrawRequest request) {
        String token = getToken();
        String url = baseUrl + "/withdraw/";
        log.info("Campay withdraw - url={}, to={}, amount={}", url, request.getTo(), request.getAmount());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Token " + token);

            // Sérialiser explicitement en JSON (cohérent avec collect)
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody;
            try {
                jsonBody = mapper.writeValueAsString(request);
            } catch (JsonProcessingException jpe) {
                log.error("Erreur de sérialisation JSON pour la requête withdraw", jpe);
                throw new RuntimeException("Erreur lors de la préparation de la requête Campay withdraw", jpe);
            }
            log.debug("Campay withdraw - payload: {}", jsonBody);

            HttpEntity<String> httpRequest = new HttpEntity<>(jsonBody, headers);
            try {
                ResponseEntity<CampayWithdrawResponse> responseEntity = restTemplate.postForEntity(url, httpRequest, CampayWithdrawResponse.class);
                CampayWithdrawResponse response = responseEntity.getBody();
                if (response == null) {
                    throw new RuntimeException("Réponse Campay withdraw vide");
                }
                log.info("Campay withdraw initié : reference={}", response.getReference());
                return response;
            } catch (HttpClientErrorException hcee) {
                String respBody = hcee.getResponseBodyAsString();
                log.error("Campay API error during withdraw: status={}, body={}", hcee.getStatusCode(), respBody);
                if (hcee.getStatusCode().value() == 401) {
                    throw new RuntimeException(
                            "Campay 401 : token invalide ou retrait non autorisé sur ce compte. " +
                            "Vérifiez que les retraits API sont activés dans les paramètres de votre application Campay. " +
                            "Body: " + respBody, hcee);
                }
                throw new RuntimeException("Campay API error [" + hcee.getStatusCode() + "]: " + respBody, hcee);
            }
        } catch (RestClientException e) {
            log.error("Erreur lors de l'appel Campay withdraw", e);
            throw new RuntimeException("Erreur lors de l'initiation du retrait Orange Money", e);
        }
    }

    /**
     * Vérifie le statut d'une transaction Campay.
     */
    public CampayStatusResponse checkStatus(String campayReference) {
        String token = getToken();
        String url = baseUrl + "/transaction/" + campayReference + "/";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Token " + token);

            HttpEntity<Void> httpRequest = new HttpEntity<>(headers);
            ResponseEntity<CampayStatusResponse> responseEntity = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, httpRequest, CampayStatusResponse.class);
            CampayStatusResponse response = responseEntity.getBody();
            if (response == null) {
                throw new RuntimeException("Réponse Campay status vide pour ref=" + campayReference);
            }
            return response;
        } catch (RestClientException e) {
            log.error("Erreur lors de la vérification du statut Campay : ref={}", campayReference, e);
            throw new RuntimeException("Impossible de vérifier le statut du paiement", e);
        }
    }
}

