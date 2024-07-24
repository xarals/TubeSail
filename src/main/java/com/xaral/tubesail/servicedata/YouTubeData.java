package com.xaral.tubesail.servicedata;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YouTubeData {
    private Map<String, Map<String, String>> videoData;
    private final String audioUrl;
    private final String audioSize;
    private final String title;
    private final String thumbnail;
    private final String length;

    public YouTubeData(String videoId) {
        YoutubeDownloader downloader = new YoutubeDownloader();
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = downloader.getVideoInfo(request);

        VideoInfo video = response.data();
        AudioFormat audioFormat = video.bestAudioFormat();
        long audioLength = audioFormat.contentLength();

        title = video.details().title();
        thumbnail = video.details().thumbnails().get(1);

        int lengthSeconds = video.details().lengthSeconds();
        int lengthMinutes = lengthSeconds / 60;
        int lengthHours = lengthMinutes / 60;

        String videoLength = "";
        if (lengthHours > 0)
            videoLength += lengthHours + ":";
        lengthMinutes -= lengthHours * 60;
        if (lengthMinutes < 10)
            videoLength += "0";
        videoLength += lengthMinutes + ":";
        lengthSeconds -= lengthHours * 3600 + lengthMinutes * 60;
        if (lengthSeconds < 10)
            videoLength += "0";
        videoLength += lengthSeconds;
        length = videoLength;

        List<VideoFormat> videoFormats = video.videoFormats();
        videoData = new HashMap<>();
        for (VideoFormat format : videoFormats) {
            if (!format.mimeType().startsWith("video/mp4; codecs=\"avc1"))
                continue;
            Map<String, String> formatInfo = new HashMap<>();
            formatInfo.put("url", format.url());
            if (format.contentLength() == null)
                continue;
            double size = Math.round((format.contentLength() + audioLength) / 1024.0 / 1024.0 * 10) / 10.0;
            String sizeUnits = "MB";
            if (size > 1024) {
                size = Math.round(size / 1024.0 * 10) / 10.0;
                sizeUnits = "GB";
            }
            formatInfo.put("size", size + " " + sizeUnits);
            videoData.put(format.qualityLabel(), formatInfo);
        }

        audioUrl = audioFormat.url();

        double size = Math.round(audioFormat.contentLength() / 1024.0 / 1024.0 * 10) / 10.0;
        String sizeUnits = "MB";
        if (size > 1024) {
            size = Math.round(size / 1024.0 * 10) / 10.0;
            sizeUnits = "GB";
        }

        audioSize = size + " " + sizeUnits;
    }

    public String getTitle() {
        return title;
    }

    public String getLength() {
        return length;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Set<String> getQuality() {
        return videoData.keySet();
    }

    public Map<String, Map<String, String>> getVideoData() {
        return videoData;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getAudioSize() {
        return audioSize;
    }
}
