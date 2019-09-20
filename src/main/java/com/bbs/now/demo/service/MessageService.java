package com.bbs.now.demo.service;

import com.bbs.now.demo.mapper.MessageMapper;
import com.bbs.now.demo.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/13
 **/
@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;


    public void addMessage(Message message){
        messageMapper.addMessage(message);
    }

    public List<Message> getConversation(int id, int offset, int limit) {
       return messageMapper.getConversationList(id,offset,limit);
    }

    public int getConversationUnRead(int userId, String conversationId) {
        return messageMapper.getConversationUnRead(userId,conversationId);
    }

    public List<Message> getMessageByConversation(String conversationId,int offset,int limit) {
        return messageMapper.getMessageByConversation(conversationId,offset,limit);
    }
}
