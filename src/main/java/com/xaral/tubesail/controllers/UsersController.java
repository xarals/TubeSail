package com.xaral.tubesail.controllers;

import com.xaral.tubesail.domain.DownloadHistory;
import com.xaral.tubesail.domain.User;
import com.xaral.tubesail.repositories.DownloadHistoryRepository;
import com.xaral.tubesail.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class UsersController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DownloadHistoryRepository downloadHistoryRepository;

    @GetMapping("/users")
    private String users(Model model) {
        List<User> users = userRepository.findAll();
        List<String> roleOrder = Arrays.asList("Creator", "Admin", "Moderator", "User");
        users.sort(Comparator.comparingInt(user -> roleOrder.indexOf(user.getHighestRole())));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        return "users";
    }

    @PostMapping("/users/disable")
    private String disableUser(@RequestParam("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        User user = userRepository.findById(id).orElseThrow();
        if (!currentUser.canChange(user.getHighestRole()))
            return "users";
        user.setActivity(false);
        userRepository.save(user);
        return "users";
    }

    @PostMapping("/users/enable")
    private String enableUser(@RequestParam("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        User user = userRepository.findById(id).orElseThrow();
        if (!currentUser.canChange(user.getHighestRole()))
            return "users";
        user.setActivity(true);
        userRepository.save(user);
        return "users";
    }

    @PostMapping("/users/delete")
    private String deleteUser(@RequestParam("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        User user = userRepository.findById(id).orElseThrow();
        if (!currentUser.canChange(user.getHighestRole()) || !currentUser.canChange("Moderator"))
            return "users";
        userRepository.delete(user);
        return "users";
    }

    @PostMapping("/users/give_role")
    private String giveRole(@RequestParam("userId") Long userId, @RequestParam("role") String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!currentUser.canChange(user.getHighestRole()) || user.getHighestRole().equals(role))
            return "users";
        List<String> roleOrder = Arrays.asList("user", "moderator", "admin", "creator");
        StringBuilder newRoles = new StringBuilder();
        for (int i = 0; i <= roleOrder.indexOf(role.toLowerCase()); i++) {
            if (i > 0)
                newRoles.append(", ");
            newRoles.append(roleOrder.get(i));
        }
        user.setRoles(newRoles.toString());
        userRepository.save(user);
        return "users";
    }

    @GetMapping("/users/history")
    private String userHistory(@RequestParam("id") Long userId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!currentUser.canChange(user.getHighestRole()))
            return "redirect:/users";
        List<DownloadHistory> downloadHistories = user.getDownloadHistory();
        Collections.reverse(downloadHistories);
        model.addAttribute("usernameHistory", user.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("downloadHistories", downloadHistories);
        return "user-history";
    }

    @PostMapping("/users/history/disable")
    private String disableHistory(@RequestParam("historyId") Long historyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        DownloadHistory history = downloadHistoryRepository.findById(historyId).orElseThrow();
        User userHistory = history.getUser();
        if (!currentUser.canChange(userHistory.getHighestRole()))
            return "redirect:/users";
        history.setVisibility(false);
        downloadHistoryRepository.save(history);
        return "user-history";
    }

    @PostMapping("/users/history/enable")
    private String enableHistory(@RequestParam("historyId") Long historyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        DownloadHistory history = downloadHistoryRepository.findById(historyId).orElseThrow();
        User userHistory = history.getUser();
        if (!currentUser.canChange(userHistory.getHighestRole()))
            return "redirect:/users";
        history.setVisibility(true);
        downloadHistoryRepository.save(history);
        return "user-history";
    }

    @PostMapping("/users/history/delete")
    private String deleteHistory(@RequestParam("historyId") Long historyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        DownloadHistory history = downloadHistoryRepository.findById(historyId).orElseThrow();
        User userHistory = history.getUser();
        if (!currentUser.canChange(userHistory.getHighestRole()) || !currentUser.canChange("Moderator"))
            return "redirect:/users";
        downloadHistoryRepository.delete(history);
        return "user-history";
    }
}
