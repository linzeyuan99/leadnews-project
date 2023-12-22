package com.lzy.common.context;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;

public class UserContext {

    public static Long getCurrentUserId(){
        ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getHeader("token");
        return null;
    }
}
