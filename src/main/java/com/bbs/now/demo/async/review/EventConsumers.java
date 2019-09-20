package com.bbs.now.demo.async.review;

import com.alibaba.fastjson.JSON;
import com.bbs.now.demo.async.EventConsumer;
import com.bbs.now.demo.async.EventHandler;
import com.bbs.now.demo.async.EventModel;
import com.bbs.now.demo.async.EventType;
import com.bbs.now.demo.utlis.JedisAdapter;
import com.bbs.now.demo.utlis.RedisKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/27
 **/
@Service
public class EventConsumers implements ApplicationContextAware,InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    private Map<EventType,List<EventHandler>>  config = new HashMap<>();

    private ApplicationContext applicationContext;

    @Autowired
    private JedisAdapter jedisAdapter;
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null){
        for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
            List<EventType> types = entry.getValue().getSupportEventTypes();
            for (EventType key : types) {
                if(!config.containsKey(key)){
                    config.put(key,new ArrayList<EventHandler>());
                }
                config.get(key).add(entry.getValue());
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    while(true){
                        String key = RedisKeyUtils.getEventQueueKey();
                        List<String> msg = jedisAdapter.brpop(0, key);

                        for (String s : msg) {
                            if(s.equals(key)){
                                continue;
                            }
                            EventModel eventModel = JSON.parseObject(s, EventModel.class);
                            if(!config.containsKey(eventModel.getType())){
                                LOGGER.error("不能识别的");
                                continue;
                            }
                            for (EventHandler eventHandler : config.get(eventModel.getType())) {
                                eventHandler.doHandle(eventModel);
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
