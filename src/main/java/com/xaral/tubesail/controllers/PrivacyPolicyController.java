package com.xaral.tubesail.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrivacyPolicyController {

    @GetMapping("/privacy_policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }
}
