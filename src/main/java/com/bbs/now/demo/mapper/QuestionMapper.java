package com.bbs.now.demo.mapper;

import com.bbs.now.demo.pojo.Question;
import com.bbs.now.demo.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.management.Query;
import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Mapper
public interface QuestionMapper {
    String TABLE_NAME = "question";
    String INSERT_FILED = "title,content,user_id,created_date,comment_count";
    String ALL_FILED = "id," + INSERT_FILED;

    @Insert({"insert into ",TABLE_NAME ,"(" ,INSERT_FILED + ") values(#{title},#{content},#{userId},#{createdDate}" +
            ",#{commentCount})"})
    int addQuestion(Question question);

    List<Question> selectLatestQuestions(@Param("userId") int userid,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select ",ALL_FILED," from ",TABLE_NAME," where id = #{qid}"})
    Question getById(int qid);

    @Update({"update ",TABLE_NAME, " set comment_count =#{count} where id = #{questionId}"})
    void updateCommentCount(@Param("questionId") int questionId,@Param("count") int count);

}
