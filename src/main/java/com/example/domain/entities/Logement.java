package com.example.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "logement")
@Getter
@Setter
public class Logement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @javax.persistence.Id
    private Long id;

    @Column(name = "montantLoyer")
    private int montantLoyer;

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,mappedBy = "logement",orphanRemoval = true)
    private Set<RecapByMonth> recapByMonths;

    public Logement(){}

    public Logement(int montantLoyer, String address, String description, User user) {
        this.montantLoyer = montantLoyer;
        this.address = address;
        this.description = description;
        this.user = user;
    }

    @JsonIgnore
    public User getUser() {
        return this.user;
    }


}
