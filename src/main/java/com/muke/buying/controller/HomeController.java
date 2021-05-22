package com.muke.buying.controller;

import com.alibaba.fastjson.JSON;
import com.muke.buying.dao.UserDao;
import com.muke.buying.result.Result;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class HomeController {
//    @RequestMapping("/home")
//    @ResponseBody
//    public String login() {
//        return "homehomehome";
//    }

    @PreAuthorize("hasAnyRole('user')")
    @GetMapping("/user")
    @ResponseBody
    public String user(){
        return "user角色访问";
    }

    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping("/admin")
    @ResponseBody
    public String admin(){
        return "admin角色访问";
    }
}
