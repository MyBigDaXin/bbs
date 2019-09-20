package com.bbs.now.demo.controller;
import java.util.Date;

import com.bbs.now.demo.pojo.Comment;
import com.bbs.now.demo.pojo.EntityType;
import com.bbs.now.demo.pojo.HostHolder;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.service.CommentService;
import com.bbs.now.demo.service.QuestionService;
import com.bbs.now.demo.utlis.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.plaf.basic.BasicScrollPaneUI;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/13
 **/
@Controller
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;

    @RequestMapping("/addComment")
    public String addComment(@RequestParam("content") String content,
                             @RequestParam("questionId") int questionId) {
        Comment comment = new Comment();
        User user = hostHolder.get();
        if(user ==null ){
            comment.setUserId(0);
        }else{
            comment.setUserId(user.getId());
        }
        comment.setEntityId(questionId);
        comment.setEntityType(EntityType.ENTITY_QUESTION);
        comment.setContent(content);
        comment.setCreatedDate(new Date());
        comment.setStatus(0);
        int i = commentService.addComment(comment);
        int count = commentService.getCommentCount(questionId,comment.getEntityType());
        questionService.updateCommentCount(questionId,count);

        return "redirect:/question/"+String.valueOf(questionId);
    }

}
