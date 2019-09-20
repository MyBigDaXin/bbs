package com.bbs.now.demo.mapper;

import com.bbs.now.demo.pojo.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface MessageMapper {

    String TABLE_NAME = " message ";
    String INSERT_FILED = "from_id,to_id,content,created_date,has_read,conversation_id";
    String ALL_FILED = "id," + INSERT_FILED;


   @Insert({"insert into ",TABLE_NAME,"( ",INSERT_FILED," ) values(#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
   int addMessage(Message message);


    @Select({"select ",INSERT_FILED, " , count(id) as id from  ( select * from ",TABLE_NAME," where from_id =#{userId} or to_id =#{userId} order by created_date desc  ) as ms group by conversation_id order by created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset, @Param("limit") int limit);


    @Select({"select count(id) ",TABLE_NAME ," where has_read = 0 and from_id = #{userId} and conversation_id = #{conversationId}"})
    int getConversationUnRead(@Param("userId") int userId,@Param("conversationId") String conversationId);

    @Select({"select ",ALL_FILED," from ",TABLE_NAME," where conversation_id = #{conversationId} limit #{offset},#{limit}"})
    List<Message> getMessageByConversation(@Param("conversationId") String conversationId, int offset, int limit);

}
