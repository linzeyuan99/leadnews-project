package com.lzy.article.interceptor;

import com.lzy.model.article.pojos.ApArticle;
import com.lzy.utils.thread.AtThreadLocalUtil;
import org.apache.commons.lang.StringUtils;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ArticleInterceptor implements HandlerInterceptor {

    /**
     * 得到 handler 用戶信息存入當前的綫程中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if (StringUtils.isNotBlank(userId)){
            ApArticle article = new ApArticle();
            //存入到當前綫程
            article.setId(Long.valueOf(userId));
            AtThreadLocalUtil.setUser(article);
        }
        return true;
    }

    /**
     * 清理綫程
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        AtThreadLocalUtil.clear();
    }
}
