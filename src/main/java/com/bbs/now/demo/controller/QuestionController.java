package com.bbs.now.demo.controller;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bbs.now.demo.pojo.Comment;
import com.bbs.now.demo.pojo.EntityType;
import com.bbs.now.demo.pojo.HostHolder;
import com.bbs.now.demo.pojo.Question;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.pojo.ViewObject;
import com.bbs.now.demo.service.CommentService;
import com.bbs.now.demo.service.LikeService;
import com.bbs.now.demo.service.QuestionService;
import com.bbs.now.demo.service.UserService;
import com.bbs.now.demo.utlis.WendaUtil;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.text.normalizer.NormalizerBase;

import javax.websocket.server.PathParam;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Controller
public class QuestionController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/question/add",method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content")String content){
        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setCreatedDate(new Date());
        if(hostHolder.get() == null){
            question.setUserId(0);
        }else{
            question.setUserId(hostHolder.get().getId());
        }
        question.setCommentCount(0);
      int i =  questionService.addQuestion(question);
      if(i >0){
          return WendaUtil.getJSONString(0);
      }
      return WendaUtil.getJSONString(1,"添加失败");
    }

    @RequestMapping("/question/{qid}")
    public String questionDetail(Model model,@PathVariable("qid") int qid){
        Question question = questionService.getById(qid);
        model.addAttribute("question",question);
        List<Comment> comments = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        if(comments != null){
            User u = hostHolder.get();
            for (Comment comment : comments) {
                ViewObject vo = new ViewObject();
                User user =  userService.selectById(comment.getUserId());
                vo.set("comment",comment);
                if(u == null){
                    vo.set("liked",0);
                }else{
                    int likeStatus = likeService.getLikeStatus(EntityType.ENTITY_COMMENT, comment.getId(), u.getId());
                    vo.set("liked",likeStatus);
                }
                long likeCount = likeService.getLikeCount(comment.getId(), EntityType.ENTITY_COMMENT);
                vo.set("likeCount",likeCount);
                vo.set("user",user);
                vos.add(vo);
            }
        }
        model.addAttribute("comments",vos);
        return "detail";
    }

}
