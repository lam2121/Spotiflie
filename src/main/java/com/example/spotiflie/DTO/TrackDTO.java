package com.example.spotiflie.DTO;

public class TrackDTO {
    private String id;
    private String name;
    private String imageUrl;
    private String artistName;
    private String duration;

    public TrackDTO(String id, String name, String imageUrl, String artistName, String duration) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.artistName = artistName;
        this.duration = duration;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getArtistName() { return artistName; }

    public String getDuration() {return duration;}

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}
