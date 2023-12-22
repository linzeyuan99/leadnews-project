package com.lzy.utils.thread;

import com.lzy.model.article.pojos.ApArticle;

public class AtThreadLocalUtil {
    private static final ThreadLocal<ApArticle> AT_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(ApArticle article) {
        AT_THREAD_LOCAL.set(article);
    }

    public static ApArticle getUser (){
        return AT_THREAD_LOCAL.get();
    }

    public static void clear(){
        AT_THREAD_LOCAL.remove();
    }
}
