package com.muke.buying.controller;

import com.alibaba.fastjson.JSON;
import com.muke.buying.dao.UserDao;
import com.muke.buying.result.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class HomeController {
    private final UserDao userDao;

    @RequestMapping("home")
    public String file() {
        return "/home";
    }

    @RequestMapping("user")
    @ResponseBody
    public String user() {
        return JSON.toJSONString(Result.success(userDao.getById(1)));
    }
}
