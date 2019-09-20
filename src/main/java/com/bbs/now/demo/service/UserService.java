package com.bbs.now.demo.service;
import java.util.Date;

import com.bbs.now.demo.mapper.LoginTicketMapper;
import com.bbs.now.demo.mapper.UserMapper;
import com.bbs.now.demo.pojo.LoginTicket;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.utlis.WendaUtil;
import org.apache.ibatis.annotations.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.security.provider.MD5;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Service
public class UserService {

    private static User NO_THING = new User();
    protected static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    LoginTicketMapper ticketMapper;

    public User selectById(int id){
        User user = userMapper.selectById(id);
        return user == null ? NO_THING : user;
    }


    public Map<String,Object> register(String username, String password) throws Exception {
        Map<String,Object> map = new HashMap<>();
        User user = userMapper.selectByusername(username);
        if(user!=null){
            map.put("msg","已注册");
            // 只要有一个不满足就直接返回
            return map;
        }
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(WendaUtil.MD5(password+ user.getSalt()));
        user.setHeadUrl(head);
        userMapper.addUser(user);
        // 视频里面有个错误,所以就得重复来辩
        User user2 = userMapper.selectByusername(username);
        // 注册了就直接进行登录
        String ticket= addLoginTick(user2.getId());
        map.put("ticket",ticket);
        return map;
    }

    private String addLoginTick(int id) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-",""));
        loginTicket.setUserId(id);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        ticketMapper.addLoginTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        User user = userMapper.selectByusername(username);
        if(user == null){
            map.put("msg","用户名不存在");
            return map;
        }
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }
        String ticket= addLoginTick(user.getId());
        map.put("ticket",ticket);
        map.put("userId",user.getId());
        return map;
    }

    public void logout(String ticket) {
        ticketMapper.logout(ticket);

    }

    public User selectByName(String name) {
        return userMapper.selectByusername(name);
    }
}
