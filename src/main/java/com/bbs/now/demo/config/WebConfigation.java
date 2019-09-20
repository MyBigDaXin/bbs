package com.bbs.now.demo.config;

import com.bbs.now.demo.intercept.LoginIntercept;
import com.bbs.now.demo.intercept.LoginJumpIntercet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * * @description:
 * * @author: Tonghuan
 * * @create: 2019/5/12
 **/
@Configuration
public class WebConfigation extends WebMvcConfigurerAdapter {

    @Autowired
    LoginIntercept loginIntercept;

    @Autowired
    LoginJumpIntercet loginJumpIntercet;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginIntercept);
        registry.addInterceptor(loginJumpIntercet).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
