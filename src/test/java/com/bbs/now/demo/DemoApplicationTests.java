package com.bbs.now.demo;

import com.bbs.now.demo.mapper.LoginTicketMapper;
import com.bbs.now.demo.mapper.QuestionMapper;
import com.bbs.now.demo.mapper.UserMapper;
import com.bbs.now.demo.pojo.LoginTicket;
import com.bbs.now.demo.pojo.Question;
import com.bbs.now.demo.pojo.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
public class DemoApplicationTests {

	@Autowired
	QuestionMapper questionDAO;

	@Autowired
	UserMapper userDAO;

	@Autowired
	LoginTicketMapper ticketMapper;


	@Test
	public void contextLoads() {
		Random random = new Random();
		for (int i = 0; i < 11; ++i) {
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("asd");
			user.setSalt("asd");
			userDAO.addUser(user);

			user.setPassword("newpassword");
			userDAO.updatePassword(user);

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
			question.setCreatedDate(date);
			question.setUserId(i + 1);
			question.setTitle(String.format("TITLE{%d}", i));
			question.setContent(String.format("Balaababalalalal Content %d", i));
			questionDAO.addQuestion(question);
		}

		Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
		userDAO.deleteById(1);
		Assert.assertNull(userDAO.selectById(1));
	}

	@Test
	public void testQueestion(){
		List<Question> questions = questionDAO.selectLatestQuestions(0, 10, 10);
		System.out.println(questions.size());
		for (Question question : questions) {
			System.out.println(question.getId());
		}
	}

	@Test
	public void test(){
		LoginTicket loginTicket = new LoginTicket();
		loginTicket.setTicket(UUID.randomUUID().toString().replace("-",""));
		loginTicket.setUserId(1);
		Date date = new Date();
		date.setTime(date.getTime() + 1000*3600*24);
		loginTicket.setExpired(date);
		loginTicket.setStatus(0);
		int i = ticketMapper.addLoginTicket(loginTicket);
		Assert.assertEquals(i,1);
	}

}
