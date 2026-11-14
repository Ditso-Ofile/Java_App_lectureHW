package com.example.homework;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Mainpage menu */
@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
