package com.bbs.now.demo.service;

import com.bbs.now.demo.utlis.JedisAdapter;
import com.bbs.now.demo.utlis.RedisKeyUtils;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import jdk.nashorn.internal.scripts.JD;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/14
 **/
@Service
public class FollowService {

    @Autowired
    JedisAdapter jedisAdapter;


    public boolean addFollower(int entityType,int entityId,int userId){
        // 先得到两个key,因为是添加了关注人嘛
        String addFollower = RedisKeyUtils.addFollower(entityId,entityType);
        String addFollowee = RedisKeyUtils.addFollowee(userId,entityType);
        // 开启一个事务 因为是要同时处理两个
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.getTx(jedis);
        // 中间用于执行多个操作
        Date date = new Date();
        tx.zadd(addFollower,date.getTime(),String.valueOf(userId));
        tx.zadd(addFollowee, date.getTime(),String.valueOf(entityId));
        // 判断它返回的是不是两个,并且每一个是不是大于一
        List<Object> exec = jedisAdapter.exec(tx, jedis);
        return exec.size() == 2 && (Long) exec.get(0) > 0 && (Long)exec.get(1) > 0;
    }

   public boolean unFollow(int entityType,int entityId,int userId){
        // 还是同理 得到两个key
       String addFollower = RedisKeyUtils.addFollower(entityId,entityType);
       String addFollowee = RedisKeyUtils.addFollowee(userId,entityType);
       //开启事务
       Jedis jedis = jedisAdapter.getJedis();
       Transaction tx = jedisAdapter.getTx(jedis);
       //删除
       tx.zrem(addFollower,String.valueOf(userId));
       tx.zrem(addFollowee,String.valueOf(entityId));
       List<Object> exec = jedisAdapter.exec(tx, jedis);
       //判断是否删除成功
       return exec.size() == 2 && (Long) exec.get(0) > 0 && (Long)exec.get(1) > 0;
   }


   public long getFollowerCount(int entityType,int entityId){
       String addFollower = RedisKeyUtils.addFollower(entityId,entityType);
       return jedisAdapter.zcard(addFollower);
   }

   public long getFolloweeCount(int userId,int entityType){
       String addFollowee = RedisKeyUtils.addFollower(userId,entityType);
       return jedisAdapter.zcard(addFollowee);
   }
   // 对自己的粉丝进行分页 -->选定具体几个
   public List<Integer> getFollower(int entityId,int entityType,int count){
       String addFollower = RedisKeyUtils.addFollower(entityId,entityType);
       Set<String> set = jedisAdapter.zrevrange(addFollower, 0, count);
        return getIds(set);
   }

   // 自己选定从头到尾
    public List<Integer> getFollower(int entityId,int entityType,int start,int end){
        String addFollower = RedisKeyUtils.addFollower(entityId,entityType);
        Set<String> zrevrange = jedisAdapter.zrevrange(addFollower, start, end);
        return getIds(zrevrange);
    }


   // 对自己的关注东西进行分页
   public List<Integer> getFollowee(int userId,int entityType,int count){
       String addFollower = RedisKeyUtils.addFollower(userId, entityType);
       Set<String> zrevrange = jedisAdapter.zrevrange(addFollower, 0, count);
       return getIds(zrevrange);
   }

   public List<Integer> getFollowee(int userId,int entityType,int start,int end){
       String addFollowee = RedisKeyUtils.addFollowee(userId, entityType);
       Set<String> zrevrange = jedisAdapter.zrevrange(addFollowee, start, end);
       if(zrevrange != null) return getIds(zrevrange);
       return new ArrayList<>();

   }


   private List<Integer> getIds(Set<String> set){
       List<Integer> ids = new ArrayList<>();
       for (String s : set) {
           ids.add(Integer.parseInt(s));
       }
       return ids;
   }

   //判断用户是否关注某个东西
    public boolean isFollower(int userId,int entityType,int entityId){
        String addFollower = RedisKeyUtils.addFollower(entityId, entityType);
        return jedisAdapter.zscore(addFollower,userId) != null;
    }

}
