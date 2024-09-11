package com.example.component;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = "/{[path:[^\\.]*}")
    public String redirect() {
        // Renvoie la page index.html pour que le routeur Angular prenne le relais
        return "forward:/index.html";
    }
}
