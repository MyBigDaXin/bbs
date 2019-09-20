package com.bbs.now.demo.service;

import com.bbs.now.demo.utlis.JedisAdapter;
import com.bbs.now.demo.utlis.RedisKeyUtils;
import jdk.nashorn.internal.scripts.JD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/14
 **/
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    public long getLikeCount(int entityId,int entityType){
        String likeKey = RedisKeyUtils.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }

    public int getLikeStatus(int entityType,int entityId,int userId){
        String likeKey = RedisKeyUtils.getLikeKey(entityType, entityId);
        if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }
        String disLikeKey = RedisKeyUtils.getDisLikeKey(entityType, entityId);
        if(jedisAdapter.sismember(disLikeKey,String.valueOf(userId))){
            return -1;
        } else {
            return 0;
        }
    }

    public long getDislikeCount(int entityId,int entityType){
        String disLikeKey = RedisKeyUtils.getDisLikeKey(entityType, entityId);
        return jedisAdapter.scard(disLikeKey);
    }

    public long addLike(int entityId,int entityType,int userId){
        // 因为是喜欢 所以要在喜欢的集合忠添加
        String likeKey = RedisKeyUtils.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));

        // 从不喜欢的集合中提出
        String disLikeKey = RedisKeyUtils.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public long addDisLike(int entityId,int entityType,int userId){
        String disLikeKey = RedisKeyUtils.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));

        String likeKey = RedisKeyUtils.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        return jedisAdapter.scard(disLikeKey);
    }


}
