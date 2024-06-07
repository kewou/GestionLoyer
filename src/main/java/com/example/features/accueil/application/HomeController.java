package com.example.features.accueil.application;

import com.example.exceptions.AuthenticationException;
import com.example.exceptions.BusinessException;
import com.example.features.accueil.application.dto.MessageCreateDto;
import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.common.mail.application.MessageService;
import com.example.features.common.mail.dto.MessageDto;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.application.mapper.UserInfoDto;
import com.example.features.user.domain.entities.Client;
import com.example.helper.ResponseHelper;
import com.example.utils.JWTUtils;
import com.example.utils.jwt.JwtRequest;
import com.example.utils.jwt.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class HomeController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ClientAppService clientAppService;
    @Autowired
    private MessageService messageService;


    @PostMapping("/authenticate")
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("INVALID CREDENTIAL");
        }
        // Identifiants user + pass ok => On cree le token JWT
        /*
        final Client userDetails = authenticationService.loadUserByUsername(jwtRequest.getUsername());
        final String token = jwtUtils.generateToken(userDetails);
        */
        Client client = clientAppService.getClientByEmail(jwtRequest.getUsername());
        final String token = jwtUtils.generateToken(client);
        return new JwtResponse(token);
    }

    @GetMapping("user-roles")
    public ResponseEntity<UserInfoDto> getUserRole(@RequestBody UserInfoDto userInfoDto, Errors erros
    ) throws BusinessException {
        ResponseHelper.handle(erros);
        return ResponseEntity.ok(clientAppService.getUserRole(userInfoDto));
    }


    @GetMapping("/locataire")
    public String homePrivee() {
        return "Welcome to your Perso Page Locataire";
    }

    @PostMapping("/contact")
    public void contact(@Valid @RequestBody MessageCreateDto messageCreateDto, Errors errors) {
        ResponseHelper.handle(errors);
        log.info("Message envoyé {} - {}", messageCreateDto.getSenderName(), messageCreateDto.getMessage());
        MessageDto messageDto = MessageDto.builder()
                .sender(messageCreateDto.getSenderMail())
                .message(messageCreateDto.getMessage())
                .subject("Contact utilisateur")
                .recipients(List.of("cnantchouang-ext@nexity.fr"))
                .build();
        messageService.sendMessage(messageDto);
    }
}
