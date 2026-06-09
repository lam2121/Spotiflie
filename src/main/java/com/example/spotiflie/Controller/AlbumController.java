package com.example.spotiflie.Controller;
import com.example.spotiflie.DTO.AlbumDTO;
import com.example.spotiflie.DTO.TrackDTO;
import com.example.spotiflie.Service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/album")
public class AlbumController {

    private final SpotifyService spotifyService;

    public AlbumController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/{id}/tracks")
    public String albumTracks(@PathVariable String id,
                              Model model,
                              HttpSession session) throws Exception {
        String accessToken = (String) session.getAttribute("spotifyToken");

        if (accessToken == null) {
            return "redirect:/";
        }
        AlbumDTO album = spotifyService.getAlbumById(accessToken, id);
        List<TrackDTO> tracks = spotifyService.getAlbumTracks(accessToken, id);
        model.addAttribute("album", album);
        model.addAttribute("tracks", tracks);

        return "fragments/album-tracks";
    }
}


