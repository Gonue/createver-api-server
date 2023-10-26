package com.template.server.domain.page.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @RequestMapping(value = "/**/{path:[^\\.]*}, params = \"!swagger\"")
    public String redirect() {
        return "forward:/index.html";
    }
}
