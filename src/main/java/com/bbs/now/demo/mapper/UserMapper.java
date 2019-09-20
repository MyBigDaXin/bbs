package com.bbs.now.demo.mapper;

import com.bbs.now.demo.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.websocket.server.ServerEndpoint;

@Mapper
public interface UserMapper {

    String TABLE_NAME = " user ";
    String INSERT_FILED = " name, password, salt, head_url ";
    String ALL_FILED = " id," + INSERT_FILED;
    @Insert({"insert into ", TABLE_NAME, "(", ALL_FILED,
            ") values (#{id},#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Delete({"delete from ",TABLE_NAME, "where id = #{id}"})
    void deleteById(@Param("id") int id);

    @Update({"update ",TABLE_NAME,"set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    @Select({"select ",ALL_FILED,"from ",TABLE_NAME,"where id = #{id}"})
    User selectById(@Param("id") int id);

    @Select({"select ",ALL_FILED,"from ",TABLE_NAME,"where name=#{name}"})
    User selectByusername(String username);
}
