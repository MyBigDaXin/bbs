package com.bbs.now.demo.controller;

import com.bbs.now.demo.async.EventModel;
import com.bbs.now.demo.async.EventProducer;
import com.bbs.now.demo.async.EventType;
import com.bbs.now.demo.pojo.Comment;
import com.bbs.now.demo.pojo.EntityType;
import com.bbs.now.demo.pojo.HostHolder;
import com.bbs.now.demo.service.CommentService;
import com.bbs.now.demo.service.LikeService;
import com.bbs.now.demo.utlis.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.color.CMMException;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/14
 **/
@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @RequestMapping("/like")
    @ResponseBody
    public String like(@RequestParam("commentId")int commentId){
        if(hostHolder.get() == null){
            return WendaUtil.getJSONString(999);
        }
        Comment comment = commentService.getCommentsById(commentId);

        eventProducer.fireEvent(new EventModel(EventType.LIKE)
        .setActorId(hostHolder.get().getId()).setEntityId(commentId)
        .setEntityType(EntityType.ENTITY_COMMENT)
        .setEntityOwnerId(comment.getUserId())
        .setExt("questionID",String.valueOf(comment.getEntityId())));
        // 进行点赞
        long l = likeService.addLike(commentId, EntityType.ENTITY_COMMENT, hostHolder.get().getId());
        return WendaUtil.getJSONString(0,String.valueOf(l));
    }



    @RequestMapping("/dislike")
    @ResponseBody
    public String disLike(@RequestParam("commentId")int commentId){
        // 没登录 则直接返回
        if(hostHolder.get() == null){
            return WendaUtil.getJSONString(999);
        }
        // 登录了 就去likeService里更改一波
        long l = likeService.addDisLike(commentId, EntityType.ENTITY_COMMENT, hostHolder.get().getId());
        return WendaUtil.getJSONString(0,String.valueOf(l));
    }
}
