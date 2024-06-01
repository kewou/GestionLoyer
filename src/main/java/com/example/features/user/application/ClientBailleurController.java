package com.example.features.user.application;


import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.user.domain.services.impl.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joel NOUMIA
 */
@RestController
@RequestMapping("/bailleur/users")
public class ClientBailleurController extends ClientController {

    @Autowired
    public ClientBailleurController(ClientService clientAppService, AuthenticationService authenticationService) {
        super(clientAppService, authenticationService);
    }


}
