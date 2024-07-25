package com.xaral.tubesail.controllers;

import com.xaral.tubesail.domain.DownloadHistory;
import com.xaral.tubesail.domain.User;
import com.xaral.tubesail.repositories.DownloadHistoryRepository;
import com.xaral.tubesail.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HistoryController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DownloadHistoryRepository downloadHistoryRepository;

    @GetMapping("/history")
    public String history(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<DownloadHistory> downloadHistories = downloadHistoryRepository.findByUser(user);
        model.addAttribute("downloadHistories", downloadHistories);
        return "history";
    }

    @PostMapping("/history/remove")
    public String removeHistory(@RequestParam("historyId") Long historyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        DownloadHistory downloadHistory = downloadHistoryRepository.findById(historyId).orElseThrow();
        if (downloadHistory.getUser().equals(user)) {
            downloadHistory.setVisibility(false);
            downloadHistoryRepository.save(downloadHistory);
        }
        return "history";
    }
}
