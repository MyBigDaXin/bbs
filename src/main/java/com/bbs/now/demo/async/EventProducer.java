package com.bbs.now.demo.async;

import com.alibaba.fastjson.JSONObject;
import com.bbs.now.demo.utlis.JedisAdapter;
import com.bbs.now.demo.utlis.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/14
 **/
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        // 将model进行序列化
        try {
            String jsonString = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtils.getEventQueueKey();
            jedisAdapter.lpush(key,jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
