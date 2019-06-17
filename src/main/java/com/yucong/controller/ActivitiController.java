package com.yucong.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yucong.service.ActivitiService;

@RestController
@RequestMapping("/activiti")
public class ActivitiController {

    @Autowired
    private ActivitiService activitiService;

    @GetMapping("/test1")
    public String test1() {
        activitiService.test1();
        return "success";
    }

    // 开始流程
    @GetMapping("/test2")
    public String test2(HttpServletRequest request) {
        activitiService.test2(request);
        return "success";
    }

    // 并行流程，保存用户
    @GetMapping("/test3")
    public String test3(HttpServletRequest request) {
        activitiService.test3(request);
        return "success";
    }

    // 并行流程，保存用户，异常，是否回滚
    @GetMapping("/test4")
    public String test4(HttpServletRequest request) {
        activitiService.test4(request);
        return "success";
    }

    // 正常执行流程
    @GetMapping("/test5")
    public String test5(HttpServletRequest request) {
        activitiService.test5(request);
        return "success";
    }
}

