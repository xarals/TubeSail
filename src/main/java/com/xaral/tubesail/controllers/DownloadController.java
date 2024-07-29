package com.xaral.tubesail.controllers;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.xaral.tubesail.domain.DownloadHistory;
import com.xaral.tubesail.domain.User;
import com.xaral.tubesail.download.MultiThreadedDownload;
import com.xaral.tubesail.repositories.DownloadHistoryRepository;
import com.xaral.tubesail.repositories.UserRepository;
import com.xaral.tubesail.services.DownloadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
public class DownloadController {
    @Autowired
    private DownloadService downloadService;

    @GetMapping("/download")
    public void downloadYouTube(@RequestParam("fileName") String fileName,
                                @RequestParam("videoUrl") String videoUrl,
                                @RequestParam("audioUrl") String audioUrl,
                                @RequestParam("videoId") String videoId,
                                @RequestParam("image") String image,
                                @RequestParam("quality") String quality,
                                @RequestParam("length") String length,
                                HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadService.downloadFromYouTube(fileName, videoUrl, audioUrl, videoId, image, quality, length, response, request);
    }
}
