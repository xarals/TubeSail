package com.xaral.tubesail.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {
    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/copyright")
    public String copyright() {
        return "copyright";
    }

    @GetMapping("/privacy_policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }
}
