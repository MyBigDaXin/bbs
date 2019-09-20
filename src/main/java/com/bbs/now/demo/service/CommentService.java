package com.bbs.now.demo.service;

import com.bbs.now.demo.mapper.CommentMapper;
import com.bbs.now.demo.pojo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/13
 **/
@Service
public class CommentService {

    @Autowired
    CommentMapper commentMapper;


    public List<Comment> getCommentsByEntity(int qid, int entityQuestion) {
        return commentMapper.selectByEntity(qid,entityQuestion);
    }

    public int addComment(Comment comment) {
         return commentMapper.addComment(comment);
    }

    public int getCommentCount(int questionId, int entityType) {
        return commentMapper.getCommentCount(questionId,entityType);
    }

    public Comment getCommentsById(int commentId) {
        return commentMapper.selectById(commentId);
    }
}
