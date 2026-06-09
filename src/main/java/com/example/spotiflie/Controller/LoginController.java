package com.example.spotiflie.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/Login")
    public String Login(
            @AuthenticationPrincipal OAuth2User user,
            Model model) {

        model.addAttribute("currentUrl", "/Login");

        return "login";
    }
}
