package com.xaral.tubesail.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CopyrightController {

    @GetMapping("/copyright")
    public String copyright() {
        return "copyright";
    }
}
