package com.bbs.now.demo.utlis;

import jdk.nashorn.internal.scripts.JD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import javax.print.attribute.standard.MediaSize;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/13
 **/
@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://192.168.25.128:6379");
    }


    public long sadd(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.info("发生异常");
        }finally {
            if(jedis !=null){
                jedis.close();
            }
        }
        return 0;
    }
    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
           return jedis.scard(key);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }

        return 0;
    }

    public long srem(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.info("发生异常");
        }finally {
            if(jedis !=null){
                jedis.close();
            }
        }
        return 0;
    }
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public void zadd(String key,String value,double score){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.zadd(key,score,value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(jedis !=null){
                jedis.close();
            }
        }
    }

    public double zscore(String key,String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(jedis !=null){
                jedis.close();
            }
        }
        return 0;
    }

    public long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
           return jedis.zcard(key);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(jedis !=null){
                jedis.close();
            }
        }
        return 0;
    }

    public Jedis getJedis(){
        return pool.getResource();
    }

   public Transaction getTx(Jedis jedis){
       try {
           return jedis.multi();
       } catch (Exception e) {
           e.printStackTrace();
       }finally {
       }
       return null;
   }
    public List<Object> exec(Transaction tx, Jedis jedis){
        try {
            return tx.exec();
        } catch (Exception e) {
            e.printStackTrace();
            tx.discard();
        }finally {
            if(tx !=null){
                try {
                    tx.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }


    public Double zscore(String key,int member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, String.valueOf(member));
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
    public List<String> lrange(String key,int start,int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key,start,end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }finally{
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
}
