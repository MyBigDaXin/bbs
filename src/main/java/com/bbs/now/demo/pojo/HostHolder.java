package com.bbs.now.demo.pojo;

import org.springframework.stereotype.Component;

import javax.jws.soap.SOAPBinding;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Component
public class HostHolder {

    private static ThreadLocal<User> user = new ThreadLocal<>();

    public void set(User u){
        user.set(u);
    }
    public User get(){
        return user.get();
    }
    public void clear(){
        user.remove();
    }

}
