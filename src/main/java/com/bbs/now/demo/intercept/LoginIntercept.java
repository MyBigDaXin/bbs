package com.bbs.now.demo.intercept;

import com.bbs.now.demo.mapper.LoginTicketMapper;
import com.bbs.now.demo.mapper.UserMapper;
import com.bbs.now.demo.pojo.HostHolder;
import com.bbs.now.demo.pojo.LoginTicket;
import com.bbs.now.demo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Component
public class LoginIntercept implements HandlerInterceptor {

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        if(httpServletRequest.getCookies()!= null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                // 如果是
                    if (cookie.getName().equals("ticket")) {
                        ticket = cookie.getValue();
                        break;
                    }
            }
        }
        if(ticket != null){
            LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                return true;
            }
                User user = userMapper.selectById(loginTicket.getUserId());
                hostHolder.set(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        // 判断模型和视图是否为空 将 user传给view
            if(modelAndView != null){
                modelAndView.addObject("user",hostHolder.get());
            }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
