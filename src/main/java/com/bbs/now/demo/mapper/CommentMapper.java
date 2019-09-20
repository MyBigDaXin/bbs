package com.bbs.now.demo.mapper;

import com.bbs.now.demo.pojo.Comment;
import org.apache.commons.digester.annotations.rules.SetTop;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import javax.servlet.annotation.ServletSecurity;
import java.util.List;

@Mapper
public interface CommentMapper {
    String TABLE_NAME = " comment ";
    String INSERT_FILED = " content,user_id,entity_id,entity_type,created_date,status ";
    String ALL_FILED = "id," + INSERT_FILED;


    @Insert
    ({"insert into ",TABLE_NAME,"(",INSERT_FILED,") values(#{content},#{userId},#{entityId},#{entityType},#{createdDate},#{status})"})
    int addComment(Comment comment);

    @Update({"update ",TABLE_NAME," set status = #{status}  where entity_id = #{entityId} and entity_type = #{entityType} "})
    void updateStatus(@Param("entityId") int entityId,@Param("entityType") int entityType,@Param("status") int status );


    @Select({"select",ALL_FILED ," from ",TABLE_NAME ," where entity_id = #{entityId} and entity_type = #{entityType}"})
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ",TABLE_NAME," where entity_id = #{entityId} and entity_type = #{entityType}"})
    int getCommentCount(@Param("entityId")int entityId,@Param("entityType") int entityType);


    @Select({"select ",ALL_FILED," from ",TABLE_NAME," where id = #{commentId}"})
    Comment selectById(int commentId);
}
