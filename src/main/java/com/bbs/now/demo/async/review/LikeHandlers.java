package com.bbs.now.demo.async.review;
import java.util.Date;

import com.bbs.now.demo.async.EventHandler;
import com.bbs.now.demo.async.EventModel;
import com.bbs.now.demo.async.EventType;
import com.bbs.now.demo.pojo.Message;
import com.bbs.now.demo.pojo.User;
import com.bbs.now.demo.service.MessageService;
import com.bbs.now.demo.service.UserService;
import com.bbs.now.demo.utlis.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/27
 **/
public class LikeHandlers implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;
    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());

        User user = userService.selectById(eventModel.getActorId());
        message.setContent("用户" + user.getName() + "赞了你以下");
        message.setCreatedDate(new Date());

        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
