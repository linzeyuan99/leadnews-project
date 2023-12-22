package com.lzy.utils.thread;

import com.lzy.model.user.pojos.ApUser;

public class UserThreadUtil {
    private static final ThreadLocal<ApUser> AP_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static ApUser getUser(){
        return AP_USER_THREAD_LOCAL.get();
    }

    public static void setUser(ApUser apUser){
        AP_USER_THREAD_LOCAL.set(apUser);
    }

    public static void clear(){
        AP_USER_THREAD_LOCAL.remove();
    }
}
