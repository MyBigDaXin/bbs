package com.bbs.now.demo.async.handler;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.bbs.now.demo.async.EventHandler;
import com.bbs.now.demo.async.EventModel;
import com.bbs.now.demo.async.EventType;
import com.bbs.now.demo.pojo.EntityType;
import com.bbs.now.demo.pojo.Feed;
import com.bbs.now.demo.pojo.Question;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.service.FeedService;
import com.bbs.now.demo.service.FollowService;
import com.bbs.now.demo.service.QuestionService;
import com.bbs.now.demo.service.UserService;
import com.bbs.now.demo.utlis.JedisAdapter;
import com.bbs.now.demo.utlis.RedisKeyUtils;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/24
 **/
@Component
public class FeedHandler implements EventHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private FollowService followService;

    @Autowired
    private JedisAdapter jedisAdapter;


    @Override
    public void doHandle(EventModel eventModel) {
        // 这一部分是为了把model层的id随机一波
        Random r = new Random();
        eventModel.setActorId(1+r.nextInt(10));

        Feed feed = new Feed();
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setCreatedDate(new Date());
        feed.setData(buildFeedData(eventModel));

        // 写入
        feedService.addFeed(feed);

        // 发送给粉丝 , 先查出来自己的粉丝
        List<Integer> followee = followService.getFollowee(eventModel.getActorId(), EntityType.ENTITY_USER, Integer.MAX_VALUE);

        // 还要加上系统默认
        followee.add(0);

        for (Integer integer : followee) {
            String timeLineKey = RedisKeyUtils.getTimeLineKey(integer);
            // 这里放的是feedId
            jedisAdapter.lpush(timeLineKey,String.valueOf(feed.getId()));
        }


    }


    public String buildFeedData(EventModel eventModel){
        Map<String,String> map = new HashMap<>();
        int actorId = eventModel.getActorId();
        User user = userService.selectById(actorId);
        if(user != null){
            map.put("headUrl",user.getHeadUrl());
            map.put("userId",String.valueOf(user.getId()));
            map.put("userName", user.getName());
        } else{
            return null;
        }
        if(eventModel.getType() == EventType.COMMENT || (eventModel.getType() == EventType.FOLLOW && eventModel.getType() == EventType.QUESTOIN)){
            Question question = questionService.getById(eventModel.getEntityId());
            if(question != null){
                map.put("questionTitle",question.getTitle());
                map.put("questionId",String.valueOf(question.getId()));
            }
            return JSON.toJSONString(map);
        }
        return null;
    }
    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT,EventType.FOLLOW);
    }
}
