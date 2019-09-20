package com.bbs.now.demo.async.review;

import com.bbs.now.demo.pojo.EntityType;
import com.bbs.now.demo.pojo.Feed;
import com.bbs.now.demo.pojo.HostHolder;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.service.FeedService;
import com.bbs.now.demo.service.FollowService;
import com.bbs.now.demo.utlis.JedisAdapter;
import com.bbs.now.demo.utlis.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/27
 **/
@Controller
public class FeedController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @RequestMapping("push")
    public String pushFeed(Model model) {
        User user = hostHolder.get();
        int localId = user == null ? 0 : user.getId();
        String timeLineKey = RedisKeyUtils.getTimeLineKey(localId);
        List<String> feedIds = jedisAdapter.lrange(timeLineKey, 0, 10);

        List<Feed> feeds = new ArrayList<>();
        if (feedIds != null) {
            for (String feedId : feedIds) {
                Feed byId = feedService.getById(Integer.parseInt(feedId));
                if (byId != null) {
                    feeds.add(byId);
                }
            }
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping("/pull")
    public String pullFeed(Model model) {
        int localhostId = hostHolder.get() == null ? 0 : hostHolder.get().getId();
        List<Integer> followees = new ArrayList<>();
        if( localhostId != 0){
            // 查找关注的人
            followees = followService.getFollowee(localhostId, EntityType.ENTITY_USER,10);
        }
        // 再根据查找出来的去找feed
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }


}
