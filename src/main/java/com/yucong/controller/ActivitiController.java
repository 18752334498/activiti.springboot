package com.yucong.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/act")
public class ActivitiController {

    @GetMapping("/test1")
    public String test1() {

        return "success";
    }

    // 开始流程
    @GetMapping("/test2")
    public String test2(HttpServletRequest request) {
        return "success";
    }

    // 并行流程，保存用户
    @GetMapping("/test3")
    public String test3(HttpServletRequest request) {
        return "success";
    }

}

