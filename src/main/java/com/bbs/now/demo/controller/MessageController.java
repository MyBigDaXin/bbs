package com.bbs.now.demo.controller;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bbs.now.demo.pojo.HostHolder;
import com.bbs.now.demo.pojo.Message;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.pojo.ViewObject;
import com.bbs.now.demo.service.CommentService;
import com.bbs.now.demo.service.MessageService;
import com.bbs.now.demo.service.UserService;
import com.bbs.now.demo.utlis.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.ssl.HandshakeOutStream;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/13
 **/
@Controller
public class MessageController {


    private static final  Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @RequestMapping("/msg/addMessage")
    @ResponseBody
    public String onMessage(@RequestParam("toName") String name, @RequestParam("content") String content) {
        // 判断当前用户是否登录 之前过滤器是没包含这个在内的
        if (hostHolder.get() == null) {
            // 这儿的js进行了处理
            return WendaUtil.getJSONString(1, "未登录");
        }
        // 通过name得到user
        User user = userService.selectByName(name);
        if (user == null) {
            return WendaUtil.getJSONString(1, "用户名不存在");
        }
        // 判断非空 存在
        Message message = new Message();
        message.setFromId(hostHolder.get().getId());
        message.setToId(user.getId());
        message.setContent(content);
        message.setCreatedDate(new Date());
        message.setHasRead(0);
        messageService.addMessage(message);
        // 创建对象保存
        return WendaUtil.getJSONString(0);
    }


    @RequestMapping("/msg/list")
    public String messageList(Model model) {
        // 开一个viewObject
        List<ViewObject> vos = new ArrayList<>();
        // 从hostHolder查到user
        try {
            User user = hostHolder.get();
            if (user != null) {
                List<Message> msg = messageService.getConversation(user.getId(), 0, 10);
                for (Message message : msg) {
                    ViewObject vo = new ViewObject();
                    vo.set("conversation", msg);
                    // 去comment中找到给user的Message
                    int targetId = message.getFromId() == user.getId() ? message.getToId() : user.getId();
                    User u = userService.selectById(targetId);
                    vo.set("user", user);
                    int unread = messageService.getConversationUnRead(user.getId(), message.getConversationId());
                    vo.set("unread", unread);
                    vos.add(vo);
                }
                model.addAttribute("conversations", vos);

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("站内昕出错");
        }
        return "letter";
    }

    @RequestMapping("/msg/detail")
    public String msgDetail(Model model,@RequestParam("conversationId")String conversationId){

        List<ViewObject> vos = new ArrayList<>();
        // 根据id获得message集合
        // 之后 遍历的Userid 查不到就跳过.. 再存headUrl.其他的.其实我很好奇为什么不用别的 。这里是分页要用的
        try {
            List<Message> msgs = messageService.getMessageByConversation(conversationId,0,10);
            for (Message msg : msgs) {
                ViewObject vo = new ViewObject();
                User user = userService.selectById(msg.getFromId());
                if(user == null) continue;
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                vos.add(vo);
            }
            model.addAttribute("messages",vos);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("细节显示失败");
        }
        return "letterDetail";
    }

}



