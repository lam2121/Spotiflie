package com.example.spotiflie.Controller;

import com.example.spotiflie.Service.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Controller
public class SpotifyController {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @GetMapping("/spotify/login")
    public String login() {

        String scope = "playlist-read-private playlist-read-collaborative user-top-read user-read-recently-played user-modify-playback-state user-read-playback-state streaming";

        String url =
                "https://accounts.spotify.com/authorize" +
                        "?client_id=" + clientId +
                        "&response_type=code" +
                        "&redirect_uri=" + redirectUri +
                        "&scope=" + scope;

        return "redirect:" + url;
    }
    private final SpotifyService spotifyService;

    public SpotifyController(
            SpotifyService spotifyService) {

        this.spotifyService = spotifyService;
    }
    @GetMapping("/callback")
    public String callback(
            @RequestParam("code") String code,
            HttpSession session) throws Exception {

        System.out.println("Spotify Code: " + code);
        String accessToken = spotifyService.getAccessToken(code);

        session.setAttribute("spotifyToken", accessToken);
        System.out.println("ACCESS TOKEN = " + accessToken);
        return "redirect:/";
    }
    @PutMapping("/spotify/play-web")
    @ResponseBody
    public ResponseEntity<?> playTrackOnWeb(
            @RequestParam String deviceId,
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        String uri = body.get("uri");
        String accessToken = (String) session.getAttribute("spotifyToken");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "uris", List.of(uri)
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.exchange(
                "https://api.spotify.com/v1/me/player/play?device_id=" + deviceId,
                HttpMethod.PUT,
                entity,
                String.class
        );

        return ResponseEntity.ok("Playing on web");
    }
    @PostMapping("/spotify/next")
    @ResponseBody
    public ResponseEntity<?> nextTrack(HttpSession session) {
        String accessToken = (String) session.getAttribute("spotifyToken");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        new RestTemplate().exchange(
                "https://api.spotify.com/v1/me/player/next",
                HttpMethod.POST,
                entity,
                String.class
        );

        return ResponseEntity.ok("Next");
    }
    @PostMapping("/spotify/previous")
    @ResponseBody
    public ResponseEntity<?> previousTrack(HttpSession session) {
        String accessToken = (String) session.getAttribute("spotifyToken");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        new RestTemplate().exchange(
                "https://api.spotify.com/v1/me/player/previous",
                HttpMethod.POST,
                entity,
                String.class
        );

        return ResponseEntity.ok("Previous");
    }
}