package com.example.features.accueil.application;

import com.example.exceptions.BusinessException;
import com.example.features.accueil.application.dto.OAuth2UserInfoDTO;
import com.example.features.accueil.domain.services.OAuth2Service;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.ClientMapper;
import com.example.features.user.domain.entities.Client;
import com.example.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private OAuth2Service oauth2Service;

    @Autowired
    private ClientAppService clientAppService;


    @GetMapping("/authorize/{provider}")
    public void redirect(@PathVariable String provider,
                         @RequestParam String redirect_uri,
                         HttpServletResponse response) throws IOException {
        String url = oauth2Service.getRedirectUrl(provider, redirect_uri);
        response.sendRedirect(url);
    }

    @GetMapping("/callback/{provider}")
    public void callback(@PathVariable String provider,
                         @RequestParam String code,
                         @RequestParam String state,
                         HttpServletResponse response) throws IOException, BusinessException {
        OAuth2UserInfoDTO userInfo = oauth2Service.processOAuth2Callback(provider, code);
        Client client = clientAppService.getClientByEmail(userInfo.getEmail());
        if (client == null) {
            ClientDto clientDto = new ClientDto();
            clientDto.setEmail(userInfo.getEmail());
            clientDto.setName(userInfo.getName());
            clientDto.setLastName(userInfo.getLastName());
            ClientMapper.getMapper().entitie(clientAppService.register(clientDto));
            client = clientAppService.getClientByEmail(userInfo.getEmail());
        }
        final String token = jwtUtils.generateToken(client);
        response.sendRedirect(state + "?token=" + token);


    }
}
