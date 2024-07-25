package com.xaral.tubesail.domain;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "download_history")
public class DownloadHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String image;
    private String videoTitle;
    private String videoId;
    private String quality;
    private String length;
    private Long date;
    private boolean visibility;

    public DownloadHistory() {}

    public DownloadHistory(User user, String videoId, String image, String videoTitle, String quality, String length, Long date, boolean visibility) {
        this.user = user;
        this.videoId = videoId;
        this.image = image;
        this.videoTitle = videoTitle;
        this.quality = quality;
        this.length = length;
        this.date = date;
        this.visibility = visibility;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(this.date);
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}
