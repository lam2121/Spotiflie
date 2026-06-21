package com.example.spotiflie.Service;

import com.example.spotiflie.DTO.SpotifyItemDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.example.spotiflie.DTO.ArtistDTO;
import com.example.spotiflie.DTO.TrackDTO;
import com.example.spotiflie.DTO.AlbumDTO;

import java.util.*;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    private final Map<String, List<AlbumDTO>> albumCache = new HashMap<>();
    private final Map<String, List<TrackDTO>> trackCache = new HashMap<>();

    public String getAccessToken(String code) {

        RestTemplate restTemplate = new RestTemplate();

        String credentials = clientId + ":" + clientSecret;

        String base64Credentials =
                Base64.getEncoder()
                        .encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + base64Credentials);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        System.out.println("Redirect URI = " + redirectUri);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        "https://accounts.spotify.com/api/token",
                        request,
                        Map.class);

        return (String) response.getBody().get("access_token");
    }
    public List<SpotifyItemDTO> getPlaylists(String accessToken) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.spotify.com/v1/me/playlists",
                HttpMethod.GET,
                entity,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        List<SpotifyItemDTO> playlists = new ArrayList<>();

        for (JsonNode item : root.get("items")) {
            String name = item.get("name").asText();
            String owner = item.get("owner").get("display_name").asText();

            String imageUrl = "";
            if (item.get("images").size() > 0) {
                imageUrl = item.get("images").get(0).get("url").asText();
            }
            String id = item.get("id").asText();
            playlists.add(new SpotifyItemDTO(id, name, imageUrl, owner));
        }
        return playlists;
    }
    public List<TrackDTO> getTopTracks(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/me/top/tracks?limit=20",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            List<TrackDTO> tracks = new ArrayList<>();

            for (JsonNode item : root.get("items")) {
                String name = item.get("name").asText();
                long durationMs = item.path("duration_ms").asLong(0);

                String duration = String.format(
                        "%d:%02d",
                        durationMs / 60000,
                        (durationMs % 60000) / 1000
                );
                String artistName = item.get("artists").get(0).get("name").asText();

                String imageUrl = "";
                if (item.get("album").get("images").size() > 0) {
                    imageUrl = item.get("album").get("images").get(0).get("url").asText();
                }
                String id = item.get("id").asText();
                tracks.add(new TrackDTO(id,name, imageUrl, artistName, duration));
            }

            return tracks;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<ArtistDTO> getTopArtists(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/me/top/artists?limit=20&time_range=long_term",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            List<ArtistDTO> artists = new ArrayList<>();

            for (JsonNode item : root.get("items")) {
                String name = item.get("name").asText();

                String imageUrl = "";
                if (item.get("images").size() > 0) {
                    imageUrl = item.get("images").get(0).get("url").asText();
                }
                String id = item.get("id").asText();
                artists.add(new ArtistDTO(id, name, imageUrl));
            }

            return artists;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public ArtistDTO getArtistById(String accessToken, String artistId) {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/artists/" + artistId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode item = mapper.readTree(response.getBody());

            String id = item.get("id").asText();
            String name = item.get("name").asText();

            String imageUrl = "";
            if (item.get("images") != null && item.get("images").size() > 0) {
                imageUrl = item.get("images").get(0).get("url").asText();
            };

            return new ArtistDTO(id, name, imageUrl);
    }
    public List<AlbumDTO> getArtistAlbums(String accessToken, String artistId) {

        if (albumCache.containsKey(artistId)) {
            System.out.println("Lấy albums từ cache: " + artistId);
            return albumCache.get(artistId);
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = "https://api.spotify.com/v1/artists/"
                    + artistId
                    + "/albums?include_groups=album,single&market=VN&limit=10";

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            List<AlbumDTO> albums = new ArrayList<>();

            for (JsonNode item : root.get("items")) {
                String id = item.get("id").asText();
                String name = item.get("name").asText();

                String imageUrl = "";
                JsonNode images = item.get("images");

                if (images != null && images.size() > 0) {
                    imageUrl = images.get(0).get("url").asText();
                }

                albums.add(new AlbumDTO(id, name, imageUrl));
            }

            albumCache.put(artistId, albums);

            System.out.println("Lưu albums vào cache: " + artistId);

            return albums;

        } catch (HttpClientErrorException.TooManyRequests e) {

            String retryAfter = e.getResponseHeaders().getFirst("Retry-After");

            System.out.println("===== RATE LIMIT =====");
            System.out.println("Retry After: " + retryAfter + " seconds");

            return Collections.emptyList();
        }
        }
    public List<TrackDTO> getAlbumTracks(String accessToken, String albumId) throws Exception {
        if (trackCache.containsKey(albumId)) {
            System.out.println("Lấy tracks từ cache: " + albumId);
            return trackCache.get(albumId);
        }

        String url = "https://api.spotify.com/v1/albums/" + albumId + "?market=VN";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        String imageUrl = "";

        JsonNode images = root.path("images");
        if (images.isArray() && images.size() > 0) {
            imageUrl = images.get(0).path("url").asText("");
        }

        List<TrackDTO> tracks = new ArrayList<>();

        JsonNode items = root.path("tracks").path("items");

        for (JsonNode item : items) {
            String id = item.path("id").asText("");
            String name = item.path("name").asText("");

            long durationMs = item.path("duration_ms").asLong(0);

            String duration = String.format(
                    "%d:%02d",
                    durationMs / 60000,
                    (durationMs % 60000) / 1000
            );

            String artistName = "";
            JsonNode artists = item.path("artists");
            if (artists.isArray() && artists.size() > 0) {
                artistName = artists.get(0).path("name").asText("");
            }

            tracks.add(new TrackDTO(id, name, imageUrl, artistName, duration));
        }

        trackCache.put(albumId, tracks);

        System.out.println("Lưu tracks vào cache: " + albumId);

        return tracks;
    }
    public AlbumDTO getAlbumById(String accessToken, String albumId) {

        String url =
                "https://api.spotify.com/v1/albums/" + albumId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );

        JsonNode root =
                new ObjectMapper().readTree(response.getBody());

        AlbumDTO album = new AlbumDTO();

        album.setId(root.path("id").asText(""));
        album.setName(root.path("name").asText(""));

        JsonNode images = root.path("images");

        if (images.isArray() && images.size() > 0) {
            album.setImageUrl(
                    images.get(0).path("url").asText("")
            );
        }
        return album;
    }

}


