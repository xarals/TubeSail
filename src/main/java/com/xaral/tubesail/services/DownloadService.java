package com.xaral.tubesail.services;

import com.xaral.tubesail.domain.DownloadHistory;
import com.xaral.tubesail.domain.User;
import com.xaral.tubesail.download.MultiThreadedDownload;
import com.xaral.tubesail.repositories.DownloadHistoryRepository;
import com.xaral.tubesail.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class DownloadService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DownloadHistoryRepository downloadHistoryRepository;

    private final String ffmpegPath = "ffmpeg";

    public void downloadFromYouTube(String fileName, String videoUrl, String audioUrl, String videoId, String image,
                                    String quality, String length, HttpServletResponse response, HttpServletRequest request) throws IOException {
        long time = System.currentTimeMillis();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                User user = userRepository.findByUsername(auth.getName()).orElseThrow();
                DownloadHistory downloadHistory = new DownloadHistory(user, videoId, image, fileName, quality, length, time, true);
                downloadHistoryRepository.save(downloadHistory);
            } catch (Exception ignore) {}
        }
        if (videoUrl.equals(audioUrl)) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".m4a\"");
            response.setContentType("audio/mp4");
        } else {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".mkv\"");
            response.setContentType("video/x-matroska");
        }
        response.setHeader("Transfer-Encoding", "chunked");

        File tempAudio = new File(time + ".mp3");
        while (!tempAudio.createNewFile()) {
            time++;
            tempAudio = new File(time + ".mp3");
        }

        try (FileOutputStream audioOutputStream = new FileOutputStream(tempAudio)) {
            MultiThreadedDownload audioDownload = new MultiThreadedDownload(audioUrl, 10, true);
            audioDownload.start();
            audioDownload.join();

            audioOutputStream.write(audioDownload.getFileData());
        } catch (Exception ex) {
            tempAudio.delete();
            return;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegPath, "-i", "pipe:0", "-i", tempAudio.toString(), "-c:v", "copy", "-c:a", "copy", "-f", "mpegts", "-hide_banner", "-loglevel", "error", "pipe:1");

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Process process = processBuilder.start();

        AtomicBoolean finish = new AtomicBoolean(false);

        MultiThreadedDownload videoDownload = new MultiThreadedDownload(videoUrl, 10);
        videoDownload.start();

        executorService.submit(() -> {
            try {
                try (OutputStream ffmpegInput = process.getOutputStream()) {
                    int partIndex = 0;
                    while (partIndex != videoDownload.getPartCount()) {
                        if (videoDownload.getPartCount() == -1 || !videoDownload.readyPart(partIndex)) {
                            Thread.sleep(100);
                            continue;
                        }
                        ffmpegInput.write(videoDownload.getPart(partIndex));
                        partIndex++;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                finish.set(true);
            }
        });

        executorService.submit(() -> {
            try (InputStream processInputStream = process.getInputStream();
                 OutputStream responseOutputStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = processInputStream.read(buffer)) != -1 || !finish.get()) {
                    if (bytesRead == -1) {
                        Thread.sleep(100);
                        continue;
                    }
                    responseOutputStream.write(buffer, 0, bytesRead);
                    response.flushBuffer();
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        int exitCode = -1;
        try {
            exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("ffmpeg process failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            tempAudio.delete();
            executorService.shutdown();
            process.destroy();
            File log = new File("download.log");
            try (FileWriter fw = new FileWriter(log, true)) {
                String info = LocalDateTime.now() + " IP: " + request.getRemoteAddr() + " Code: " + exitCode + " Time: " + ((System.currentTimeMillis() - time) / 1000.0) + " s \n";
                fw.write(info);
            }
        }
    }
}
