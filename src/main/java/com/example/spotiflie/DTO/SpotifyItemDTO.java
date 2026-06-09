package com.example.spotiflie.DTO;

public class SpotifyItemDTO {

    private String id;
    private String name;
    private String imageUrl;
    private String subtitle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public SpotifyItemDTO(String id, String name, String imageUrl, String subtitle) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.subtitle = subtitle;
    }

}
