package com.example.security;

public class SecurityRule {

    public static final String ADMIN = "hasAuthority('ADMIN')";

    public static final String OWNER_LOGEMENT_OR_ADMIN = "@authenticationService.isOwnerLogement(#refUser,#refLgt)|| hasAuthority('ADMIN')";

    public static final String OWNER_APPART_OR_ADMIN = "@authenticationService.isOwnerAppart(#refLgt)|| hasAuthority('ADMIN')";

    public static final String OWNER_BAILLEUR_APPART_OR_ADMIN = "@authenticationService.isOwnerBailleurAppart(#refUser,#refAppart) || hasAuthority('ADMIN')";

    public static final String OWNER_LOCATAIRE_APPART_OR_ADMIN = "@authenticationService.isOwnerLocataireAppart(#reference,#refAppart) || hasAuthority('ADMIN')";

    public static final String CONNECTED_OR_ADMIN = "@authenticationService.isUserConnected(#reference) || hasAuthority('ADMIN')";


}
