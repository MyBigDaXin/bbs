package com.bbs.now.demo.controller;

import com.bbs.now.demo.async.EventHandler;
import com.bbs.now.demo.async.EventModel;
import com.bbs.now.demo.async.EventProducer;
import com.bbs.now.demo.async.EventType;
import com.bbs.now.demo.pojo.EntityType;
import com.bbs.now.demo.pojo.HostHolder;
import com.bbs.now.demo.pojo.Question;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.pojo.ViewObject;
import com.bbs.now.demo.service.CommentService;
import com.bbs.now.demo.service.FollowService;
import com.bbs.now.demo.service.QuestionService;
import com.bbs.now.demo.service.UserService;
import com.bbs.now.demo.utlis.WendaUtil;
import org.apache.catalina.Host;
import org.apache.ibatis.annotations.Param;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.awt.windows.WEmbeddedFrame;
import sun.text.resources.cldr.om.FormatData_om;

import javax.management.monitor.MonitorSettingException;
import javax.xml.stream.events.EndElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/15
 **/
@Controller
public class FollowController {


    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @RequestMapping("/follower")
    @ResponseBody
    public String follower(@RequestParam("userId") int userId){
        // 判断等没登录
        User user = hostHolder.get();
        if(user == null){
            return WendaUtil.getJSONString(999);
        }
        // 得到是否关注成功了该用户
        boolean ret = followService.isFollower(userId, EntityType.ENTITY_USER, user.getId());
        // 异步消息队列
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
         .setActorId(hostHolder.get().getId()).setEntityId(userId)
        .setEntityType(EntityType.ENTITY_USER)
        .setEntityOwnerId(userId));
        //返回
        return WendaUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.get().getId(),EntityType.ENTITY_USER)));
    }


    /**
    * @Description: 取消关注
    * @Author: Tonghuan
    * @Date: 2019/5/15
    */
    @RequestMapping("/unfollowUser")
    @ResponseBody
    public String unFollowee(@RequestParam("userId") int userId){
            // 同理判断有没有登录
        User user = hostHolder.get();
        if(user ==null){
            return WendaUtil.getJSONString(999);
        }
        // 是否取消关注成功了该用户
        boolean res = followService.unFollow(EntityType.ENTITY_USER, userId, user.getId());
        // 异步队列
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setEntityOwnerId(userId)
        .setEntityType(EntityType.ENTITY_USER)
        .setActorId(user.getId()).setEntityId(userId)
        );
        // 回答
        return WendaUtil.getJSONString(res ? 0: 1,String.valueOf(followService.getFolloweeCount(user.getId(),EntityType.ENTITY_USER)));
    }


    @RequestMapping("/followQuestion")
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        // 没登陆
        User user = hostHolder.get();
        if(user == null){
            return WendaUtil.getJSONString(999);
        }
        // 问题有没有
        Question question = questionService.getById(questionId);
        if(question == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean res = followService.addFollower(EntityType.ENTITY_QUESTION, questionId, user.getId());
        // 去关注一波问题
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId)
                .setEntityOwnerId(question.getUserId())
                .setActorId(user.getId()));
        // 异步队列来一发
        //封装好要返回的值
        Map<String,Object> info = new HashMap<>();
        info.put("headUrl", user.getHeadUrl());
        info.put("name", user.getName());
        info.put("id", user.getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        // 返回
        return WendaUtil.getJSONString(0,info);
    }

    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid")int userId){
        User user = hostHolder.get();
        // 得到关注着的Id
        List<Integer> follower = followService.getFollower(userId, EntityType.ENTITY_USER, 0,10);
        // 将id转为list<user>
        if(user != null){
            // 判断是否登录了 登录了就查到账户信息 不是差
            model.addAttribute("followers",getUserInfo(user.getId(),follower));
        }else{
            model.addAttribute("followers",getUserInfo(0,follower));
        }
        // followerCount curUser
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser",userService.selectById(userId));
        return "followers";
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        // 登没登录
        User user = hostHolder.get();
        List<Integer> followee = followService.getFollowee(userId, EntityType.ENTITY_USER, 0, 10);
        if(user !=null){
            model.addAttribute("folowee",getUserInfo(user.getId(),followee));
        }else{
            model.addAttribute("folowee",getUserInfo(0,followee));
        }

        // 得到该用户的关注对象
        model.addAttribute("followeCount",followService.getFolloweeCount(user.getId(),EntityType.ENTITY_USER));

        // followeeCount curUser
        model.addAttribute("curUser",userService.selectById(userId));
        return "followees";
    }






    private List<ViewObject> getUserInfo(int userId, List<Integer> id) {
        List<ViewObject> vos = new ArrayList<>();
        for (Integer integer : id) {
            User user = userService.selectById(integer);
            if(user == null){
                continue;
            }
            ViewObject vo = new ViewObject();
            // commentCount followeeCount followerCount user followed
            vo.set("user",user);
            vo.set("followeeCount",followService.getFollowerCount(EntityType.ENTITY_USER,integer));
            vo.set("followerCount",followService.getFolloweeCount(integer,EntityType.ENTITY_USER));
            vo.set("commentCount",commentService.getCommentsById(integer));
            if(userId != 0 ){
                vo.set("followed", followService.isFollower(userId,EntityType.ENTITY_USER,integer));
            }else{
                vo.set("followed",false);
            }
            vos.add(vo);
        }
        return vos;
    }


}
