package com.example.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
public class UserDto {

    private Long id;

    @Size(min = 2, max = 10)
    private String name;

    @NotBlank()
    @Size(min = 2, max = 10)
    private String lastName;

    @NotBlank()
    @Email()
    private String email;

    private String password;

    private String role;

    private Date entryDate;

    private Date getOutDate;

    private int solde = 0;

    private int ancienneteEnMois = 0;
}
