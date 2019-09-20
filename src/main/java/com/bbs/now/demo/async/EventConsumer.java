package com.bbs.now.demo.async;


import com.alibaba.fastjson.JSON;
import com.bbs.now.demo.pojo.EntityType;
import com.bbs.now.demo.utlis.JedisAdapter;
import com.bbs.now.demo.utlis.RedisKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.jws.Oneway;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/14
 **/
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    JedisAdapter jedisAdapter;

    private Map<EventType,List<EventHandler>> config = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null){
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType type : eventTypes) {
                    if(!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
            // 重新开启一个线程
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        String key = RedisKeyUtils.getEventQueueKey();
                        List<String> events = jedisAdapter.brpop(0, key);

                        for (String message : events) {
                            // 返回值的原因《返回的第一个参数可能是他的key
                            if (message.equals(key)) {
                                continue;
                            }

                            EventModel eventModel = JSON.parseObject(message, EventModel.class);
                            if (!config.containsKey(eventModel.getType())) {
                                LOGGER.error("不能识别的事件");
                                continue;
                            }

                            for (EventHandler handler : config.get(eventModel.getType())) {
                                handler.doHandle(eventModel);
                            }
                        }
                    }
                }
            });
            thread.start();
        }


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
