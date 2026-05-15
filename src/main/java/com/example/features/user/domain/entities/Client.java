package com.example.features.user.domain.entities;

import com.example.features.bail.Bail;
import com.example.features.logement.Logement;
import com.example.utils.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sbeezy
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Client implements UserDetailsCustom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", unique = true)
    private String reference;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "isEnabled")
    private Boolean isEnabled;

    private String verificationToken;

    @Column(name = "is_virtual", nullable = false)
    private Boolean isVirtual = Boolean.FALSE;

    @Column(name = "created_by_bailleur_ref", length = 9)
    private String createdByBailleurRef;

    @Column(name = "invitation_code", length = 24)
    private String invitationCode;

    @Column(name = "invitation_code_expires_at")
    private LocalDateTime invitationCodeExpiresAt;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @PostPersist
    private void generateVerificationToken() {
        if (Boolean.TRUE.equals(isVirtual)) {
            isEnabled = Boolean.FALSE;
            verificationToken = null;
            return;
        }
        verificationToken = GeneralUtils.generateVerificationToken();
        isEnabled = Boolean.FALSE;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client", orphanRemoval = true)
    private Set<Logement> logements = new HashSet<>();

    @OneToMany(mappedBy = "locataire", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bail> baux = new HashSet<>();

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled != null && isEnabled && !Boolean.TRUE.equals(isVirtual);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Ajoutez les rÃ´les de l'utilisateur en tant qu'objets GrantedAuthority
        if (this.getRoles() != null) {
            for (String role : this.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return authorities;
    }

    public Client(String name) {
        this.name = name;
    }

    public Client() {

    }

    @Override
    public String getReference() {
        return this.reference;
    }

    @JsonIgnore
    public Set<Logement> getLogements() {
        return this.logements;
    }

    @JsonIgnore
    public Set<Bail> getBaux() {
        return this.baux;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * RÃ©cupÃ¨re les baux actifs du locataire
     *
     * @return Liste des baux actifs
     */
    public List<Bail> getBauxActifs() {
        if (baux == null) {
            return List.of();
        }
        return baux.stream()
                .filter(bail -> Boolean.TRUE.equals(bail.getActif()))
                .collect(Collectors.toList());
    }
}


