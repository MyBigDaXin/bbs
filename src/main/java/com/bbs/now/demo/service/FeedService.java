package com.bbs.now.demo.service;

import com.bbs.now.demo.mapper.FeedMapper;
import com.bbs.now.demo.pojo.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/24
 **/
@Service
public class FeedService {

    @Autowired
    FeedMapper feedMapper;

    public List<Feed> getUserFeeds(int maxId,List<Integer> userIds,int count ){
        return feedMapper.selectUserFeeds(maxId,userIds,count);
    }

    public Feed getById(int id){
        return feedMapper.getFeedById(id);
    }

    public boolean addFeed(Feed feed){
        feedMapper.addFeed(feed);
        return feed.getId() > 0;
    }

}
