package com.bbs.now.demo.controller;

import com.bbs.now.demo.pojo.EntityType;
import com.bbs.now.demo.pojo.Feed;
import com.bbs.now.demo.pojo.HostHolder;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.service.FeedService;
import com.bbs.now.demo.service.FollowService;
import com.bbs.now.demo.service.UserService;
import com.bbs.now.demo.utlis.JedisAdapter;
import com.bbs.now.demo.utlis.RedisKeyUtils;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/24
 **/
@Controller
public class FeedController {

    @Autowired
    private FeedService feedService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;
    @Autowired
    HostHolder hostHolder;


    @RequestMapping("/pushFeed")
    public String pushFeed(Model model){
       int userId = hostHolder.get() == null ? 0 : hostHolder.get().getId();
        // 去redis中查
        String timeLineKey = RedisKeyUtils.getTimeLineKey(userId);
        List<String> feedIds = jedisAdapter.lrange(timeLineKey, 0, 10);
        List<Feed> feeds =  new ArrayList<>();
        for (String feedId : feedIds) {
            Feed byId = feedService.getById(Integer.valueOf(feedId));
            if(byId != null)
            feeds.add(byId);
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping("/pullFeed")
    public String pullFeed(Model model){

        int localId = hostHolder.get() == null ? 0 : hostHolder.get().getId();
        // 查询我的关注列表
        List<Integer> followee = new ArrayList<>();
        if( localId != 0) {
          followee = followService.getFollowee(localId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Feed> userFeeds = feedService.getUserFeeds(Integer.MAX_VALUE, followee, 10);
        model.addAttribute("feeds",userFeeds);
        return "feeds";
    }

}
