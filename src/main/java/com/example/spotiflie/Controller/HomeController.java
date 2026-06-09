package com.example.spotiflie.Controller;

import com.example.spotiflie.DTO.SpotifyItemDTO;
import com.example.spotiflie.Service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final SpotifyService spotifyService;

    public HomeController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping({"/", "/Home"})
    public String home(
            HttpSession session,
            Model model,
            @AuthenticationPrincipal OAuth2User user) throws Exception {

        addHomeData(session, model, user);

        return "index";
    }
    @GetMapping("/home-content")
    public String homeContent(
            HttpSession session,
            Model model,
            @AuthenticationPrincipal OAuth2User user) throws Exception {

        addHomeData(session, model, user);

        return "fragments/home";
    }
    private void addHomeData(
            HttpSession session,
            Model model,
            OAuth2User user) throws Exception {

        String token = (String) session.getAttribute("spotifyToken");

        if (token != null) {
            model.addAttribute("spotifyConnected", true);
            model.addAttribute("spotifyToken", token);

            model.addAttribute("playlists",
                    spotifyService.getPlaylists(token));

            model.addAttribute("topTracks",
                    spotifyService.getTopTracks(token));

            model.addAttribute("topArtists",
                    spotifyService.getTopArtists(token));
        } else {
            model.addAttribute("spotifyConnected", false);
        }

        if (user != null) {
            model.addAttribute("userName", user.getAttribute("name"));
            model.addAttribute("userEmail", user.getAttribute("email"));
            model.addAttribute("userPicture", user.getAttribute("picture"));
        }
    }
}