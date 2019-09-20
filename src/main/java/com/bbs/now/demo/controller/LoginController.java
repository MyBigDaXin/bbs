package com.bbs.now.demo.controller;

import com.bbs.now.demo.async.EventModel;
import com.bbs.now.demo.async.EventProducer;
import com.bbs.now.demo.async.EventType;
import com.bbs.now.demo.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sun.swing.StringUIClientPropertyKey;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.CollationKey;
import java.util.Map;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/reg/",method = RequestMethod.POST)
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      HttpServletResponse response) throws Exception {
        Map<String,Object> map = userService.register(username,password);
        // 有没有加
       if(map.containsKey("ticket")){
           Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
           cookie.setPath("/");
           response.addCookie(cookie);
           // 返回首页广场
           return "redirect:/";
       }
       model.addAttribute("msg",map.get("msg"));
       return "login";
    }

    @RequestMapping(value = "/login/",method = {RequestMethod.POST,RequestMethod.GET})
    public String login(Model model,@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next") String next,
                        HttpServletResponse response){
        Map<String,Object> map = userService.login(username,password);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath("/");
            response.addCookie(cookie);

            // 异步队列
            eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                    .setActorId((int)map.get("userId"))
                    .setExt("username",username)
                    .setExt("email","852172031@qq.com"));

            if(StringUtils.isNotBlank(next)){
                return "redirect:/"+next;
            }
            // 返回首页广场
            return "redirect:/";
        }else{
            model.addAttribute("msg",map.get("msg"));
            return "login";
        }
    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model) {
        return "login";
    }

    @RequestMapping(value = "/logout",method =RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
