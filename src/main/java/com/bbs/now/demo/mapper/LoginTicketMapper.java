package com.bbs.now.demo.mapper;

import com.bbs.now.demo.pojo.LoginTicket;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LoginTicketMapper {

    String TABLE_NAME = " login_ticket ";
    String INSERT_FILED = " user_id,ticket,expired,status ";
    String ALL_FILED = "id," + INSERT_FILED;

   @Insert({"insert into ",TABLE_NAME,"(",INSERT_FILED," ) values(#{userId},#{ticket},#{expired},#{status})"})
   int addLoginTicket(LoginTicket loginTicket);

   @Select({"select ",ALL_FILED," from ",TABLE_NAME," where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

   @Update({"update ",TABLE_NAME ,"set status = 1 where ticket= #{ticket}"})
    void logout(String ticket);
}
