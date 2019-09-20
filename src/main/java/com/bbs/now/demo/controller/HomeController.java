package com.bbs.now.demo.controller;

import com.bbs.now.demo.mapper.QuestionMapper;
import com.bbs.now.demo.pojo.Question;
import com.bbs.now.demo.pojo.ViewObject;
import com.bbs.now.demo.service.QuestionService;
import com.bbs.now.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Controller
public class HomeController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @RequestMapping(value = {"/","/index"})
    public String index(Model model){
        List<Question> latestQuestions = questionService.getLatestQuestions(0, 0, 10);
        List<ViewObject> vos = new ArrayList<>();
        for (Question latestQuestion : latestQuestions) {
            ViewObject vo = new ViewObject();
            vo.set("question",latestQuestion);
            vo.set("user",userService.selectById(latestQuestion.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("vos",vos);
        return "index";
    }

}
