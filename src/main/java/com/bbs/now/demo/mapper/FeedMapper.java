package com.bbs.now.demo.mapper;

import com.bbs.now.demo.pojo.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/23
 **/
@Mapper
public interface FeedMapper {
    String TABLE_NAME = " feed ";
    String INSERT_FILED = " created_date,user_id,data,type ";
    String ALL_FILED = "id," + INSERT_FILED;

    @Insert({" insert into" , TABLE_NAME, " (" ,INSERT_FILED, " ) values(#{createDate},#{userId},#{data},#{type})"})
    void addFeed(Feed feed);

    @Select({"select ", ALL_FILED, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);

}
