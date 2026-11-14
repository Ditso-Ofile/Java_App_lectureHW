package com.example.homework.towns;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** React menu: serves the built React app (src/main/resources/static/react/index.html). */
@Controller
public class ReactPageController {

    @GetMapping({"/react", "/react/"})
    public String react() {
        return "forward:/react/index.html";
    }
}
