package com.example.domain.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter(value = AccessLevel.NONE)
@Entity
public class Visiteur {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
}

