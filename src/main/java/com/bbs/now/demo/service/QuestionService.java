package com.bbs.now.demo.service;

import com.bbs.now.demo.mapper.QuestionMapper;
import com.bbs.now.demo.pojo.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by nowcoder on 2016/7/15.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionMapper questionDAO;

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int addQuestion(Question question) {
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        return questionDAO.addQuestion(question);
    }

    public Question getById(int qid) {
        return questionDAO.getById(qid);
    }

    public void updateCommentCount(int questionId, int count) {
        questionDAO.updateCommentCount(questionId,count);
    }
}
