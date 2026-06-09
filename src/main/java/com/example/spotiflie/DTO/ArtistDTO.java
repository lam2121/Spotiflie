package com.example.spotiflie.DTO;

public class ArtistDTO {
    private String id;
    private String name;
    private String imageUrl;

    public ArtistDTO(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}