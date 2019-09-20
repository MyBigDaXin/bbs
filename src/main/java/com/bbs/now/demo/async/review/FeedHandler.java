package com.bbs.now.demo.async.review;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/27
 **/
public class FeedHandler implements EventHandler {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    public String buildFeedData(EventModel eventModel){
        Map<String,Object> map = new HashMap<>();
        int actorId = eventModel.getActorId();
        User user = userService.selectById(actorId);
        if(user != null){
            map.put("username",user.getName());
            map.put("id",user.getId());
            map.put("userHead",user.getHeadUrl());
        }
        if(eventModel.getType() == EventType.COMMENT  || (eventModel.getType() == EventType.FOLLOW && eventModel.getType() == EventType.QUESTOIN)) {
            int questionId = eventModel.getEntityId();
            Question question = questionService.getById(questionId);
            if(question != null){
                map.put("questionId",question.getId());
                map.put("content",question.getContent());
            }
        }
        String res = JSON.toJSONString(map);
        return res;
    }

    @Override
    public void doHandle(EventModel eventModel) {

        Random r = new Random();
        eventModel.setActorId(1+r.nextInt(10));

        Feed feed = new Feed();
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setCreatedDate(new Date());
        feed.setData(buildFeedData(eventModel));

        feedService.addFeed(feed);
        // 加入后 给哪些粉丝推新鲜事儿
        List<Integer> follower = followService.getFollower(EntityType.ENTITY_USER, eventModel.getActorId(), Integer.MAX_VALUE);
        follower.add(0);

        for (Integer integer : follower) {
            String timeLineKey = RedisKeyUtils.getTimeLineKey(integer);
            jedisAdapter.lpush(timeLineKey,String.valueOf(integer));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE,EventType.FOLLOW,EventType.QUESTOIN);
    }
}
