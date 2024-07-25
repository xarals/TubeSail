package com.xaral.tubesail.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xaral.tubesail.servicedata.YouTubeData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ApiYouTubeController {

    @GetMapping("/api/youtube/get_quality")
    public ResponseEntity<String> getQuality(@RequestParam("url") String url, HttpServletRequest request) throws IOException {
        File log = new File("get_quality.log");
        try (FileWriter fw = new FileWriter(log, true)) {
            String info = LocalDateTime.now() + " IP: " + request.getRemoteAddr() + " URL: " + url + "\n";
            fw.write(info);
        }
        String videoId;
        if (url.contains("youtu.be/"))
            videoId = url.split("youtu.be/")[url.split("youtu.be/").length > 1 ? 1 : 0].split("\\?")[0];
        else
            videoId = url.split("v=")[url.split("v=").length > 1 ? 1 : 0].split("&")[0];
        Map<String, Map<String, String>> qualityInfo = new LinkedHashMap<>();
        YouTubeData youTubeData = new YouTubeData(videoId);
        Map<String, Map<String, String>> videoInfo = youTubeData.getVideoData();
        List<String> qualities = new ArrayList<>(videoInfo.keySet());
        for (int i = 0; i < qualities.size() - 1; i++) {
            for (int j = 0; j < qualities.size() - i - 1; j++) {
                String[] qualityParts1 = qualities.get(j).split("p");
                String[] qualityParts2 = qualities.get(j + 1).split("p");
                int qualityInt1 = Integer.parseInt(qualityParts1[0]);
                int qualityInt2 = Integer.parseInt(qualityParts2[0]);
                if (qualityInt1 < qualityInt2) {
                    if (qualityParts2.length > 1)
                        qualities.set(j, qualityInt2 + "p" + qualityParts2[1]);
                    else
                        qualities.set(j, qualityInt2 + "p");
                    if (qualityParts1.length > 1)
                        qualities.set(j + 1, qualityInt1 + "p" + qualityParts1[1]);
                    else
                        qualities.set(j + 1, qualityInt1 + "p");
                }
            }
        }
        for (String quality : qualities) {
            qualityInfo.put(quality, videoInfo.get(quality));
        }
        Map<String, String> audioInfo = new HashMap<>();
        audioInfo.put("url", youTubeData.getAudioUrl());
        audioInfo.put("size", youTubeData.getAudioSize());
        qualityInfo.put("audio", audioInfo);
        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(qualityInfo));
    }

    @GetMapping("/api/youtube/get_info")
    public ResponseEntity<String> getInfo(@RequestParam("url") String url, HttpServletRequest request) throws IOException {
        File log = new File("get_info.log");
        try (FileWriter fw = new FileWriter(log, true)) {
            String info = LocalDateTime.now() + " IP: " + request.getRemoteAddr() + " URL: " + url + "\n";
            fw.write(info);
        }
        String videoId;
        if (url.contains("youtu.be/"))
            videoId = url.split("youtu.be/")[url.split("youtu.be/").length > 1 ? 1 : 0].split("\\?")[0];
        else
            videoId = url.split("v=")[url.split("v=").length > 1 ? 1 : 0].split("&")[0];
        YouTubeData youTubeData = new YouTubeData(videoId);
        Map<String, String> videoInfo = new LinkedHashMap<>();
        videoInfo.put("title", youTubeData.getTitle());
        videoInfo.put("length", youTubeData.getLength());
        videoInfo.put("thumbnail", youTubeData.getThumbnail());
        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(videoInfo));
    }

    @PostMapping("/api/youtube/search")
    public String search(@RequestParam("url") String url, Model model, HttpServletRequest request) throws IOException {
        File log = new File("search.log");
        try (FileWriter fw = new FileWriter(log, true)) {
            String info = LocalDateTime.now() + " IP: " + request.getRemoteAddr() + " URL: " + url + "\n";
            fw.write(info);
        }
        String videoId;
        if (url.contains("youtu.be/"))
            videoId = url.split("youtu.be/")[url.split("youtu.be/").length > 1 ? 1 : 0].split("\\?")[0];
        else
            videoId = url.split("v=")[url.split("v=").length > 1 ? 1 : 0].split("&")[0];
        YouTubeData youTubeData = new YouTubeData(videoId);
        Map<String, Map<String, String>> videoData = youTubeData.getVideoData();

        List<String> qualities = new ArrayList<>(videoData.keySet());

        for (int i = 0; i < qualities.size() - 1; i++) {
            for (int j = 0; j < qualities.size() - i - 1; j++) {
                String[] qualityParts1 = qualities.get(j).split("p");
                String[] qualityParts2 = qualities.get(j + 1).split("p");
                int qualityInt1 = Integer.parseInt(qualityParts1[0]);
                int qualityInt2 = Integer.parseInt(qualityParts2[0]);
                if (qualityInt1 < qualityInt2) {
                    if (qualityParts2.length > 1)
                        qualities.set(j, qualityInt2 + "p" + qualityParts2[1]);
                    else
                        qualities.set(j, qualityInt2 + "p");
                    if (qualityParts1.length > 1)
                        qualities.set(j + 1, qualityInt1 + "p" + qualityParts1[1]);
                    else
                        qualities.set(j + 1, qualityInt1 + "p");
                }
            }
        }

        Map<String, String> sizes = new HashMap<>();
        for (String quality : qualities) {
            sizes.put(quality, videoData.get(quality).get("size"));
        }

        Map<String, String> urls = new HashMap<>();
        for (String quality : qualities) {
            urls.put(quality, videoData.get(quality).get("url"));
        }

        String audioQuality = "audio/m4a";

        qualities.add(audioQuality);
        sizes.put(audioQuality, youTubeData.getAudioSize());
        urls.put(audioQuality, youTubeData.getAudioUrl());

        model.addAttribute("videoId", videoId);
        model.addAttribute("qualities", qualities);
        model.addAttribute("sizes", sizes);
        model.addAttribute("urls", urls);
        model.addAttribute("audioUrl", youTubeData.getAudioUrl());
        model.addAttribute("title", youTubeData.getTitle());
        model.addAttribute("length", youTubeData.getLength());
        model.addAttribute("thumbnail", youTubeData.getThumbnail());
        return "blocks/search-result";
    }
}
