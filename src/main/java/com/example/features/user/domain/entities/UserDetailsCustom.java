package com.example.features.user.domain.entities;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsCustom extends UserDetails {

    public String getReference();

}
