package com.example.features.payment.enums;

/**
 * Mode d'encaissement choisi par le bailleur.
 * DEUX_ETAPES : le loyer crédite le solde BeezyWeb, le bailleur retire quand il veut.
 * DIRECT      : le retrait vers Orange Money est déclenché automatiquement après chaque paiement.
 */
public enum ModeEncaissement {
    DEUX_ETAPES,
    DIRECT
}

