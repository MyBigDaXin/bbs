package com.bbs.now.demo.async.handler;

import com.bbs.now.demo.async.EventHandler;
import com.bbs.now.demo.async.EventModel;
import com.bbs.now.demo.async.EventType;
import com.bbs.now.demo.utlis.MailSender;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/25
 **/
@Component
public class LoginInterceptionHandler implements EventHandler{

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel eventModel) {
        // xxx判断这个
        Map<String,Object> map = new HashMap<>();
        map.put("username",eventModel.getExt("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExt("mail"),"登录ip异常","mail/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
     return Arrays.asList(EventType.LOGIN);
    }
}
