package com.xaral.tubesail.controllers;

import com.xaral.tubesail.domain.User;
import com.xaral.tubesail.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(String username, String password, String confirmPassword, Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("message", "Confirm password does not match Password!");
            return "registration";
        }

        if (password.isEmpty()) {
            model.addAttribute("message", "Password is empty!");
            return "registration";
        }

        if (username.isEmpty()) {
            model.addAttribute("message", "Username is empty!");
            return "registration";
        }

        Optional<User> userFromDb = userRepository.findByUsername(username);

        if (userFromDb.isPresent()) {
            model.addAttribute("message", "User with the same username already exists!");
            return "registration";
        }

        User newUser = new User(username, passwordEncoder.encode(password));

        newUser.setActivity(true);
        newUser.setRoles("user");
        userRepository.save(newUser);

        return "redirect:/login";
    }
}
