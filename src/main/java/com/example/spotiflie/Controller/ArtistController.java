package com.example.spotiflie.Controller;

import com.example.spotiflie.DTO.ArtistDTO;
import com.example.spotiflie.DTO.AlbumDTO;
import com.example.spotiflie.Service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/artist")
public class ArtistController {

    private final SpotifyService spotifyService;

    public ArtistController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/{id}")
    public String artistDetail(
            @PathVariable String id,
            HttpSession session,
            Model model) {
        String accessToken = (String) session.getAttribute("spotifyToken");

        if (accessToken == null) {
            return "redirect:/";
        }

        ArtistDTO artist = spotifyService.getArtistById(accessToken, id);
        List<AlbumDTO> albums = spotifyService.getArtistAlbums(accessToken, id);

        model.addAttribute("artist", artist);
        model.addAttribute("albums", albums);
        System.out.println("Artist ID = " + id);
        System.out.println("Albums size = " + albums.size());
        System.out.println("=== CONTROLLER CALLED ===");
        return "fragments/artist";
    }
}