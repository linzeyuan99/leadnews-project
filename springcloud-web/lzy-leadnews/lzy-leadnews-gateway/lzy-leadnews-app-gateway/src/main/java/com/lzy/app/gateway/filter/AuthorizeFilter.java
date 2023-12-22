package com.lzy.app.gateway.filter;


import com.lzy.utils.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.判断是否是登录
        if (request.getURI().getPath().contains("/login") || request.getURI().getPath().contains("/delete")) {
            //放行
            return chain.filter(exchange);
        }

        //3.获取token
        String token = request.getHeaders().getFirst("token");
        //兼容图片上传的方式,不太合理 token用url的形式傳遞過來
        String tokenVal = null;
        if (StringUtils.isBlank(token)) {
            String query = request.getURI().getQuery();
            tokenVal = Optional.ofNullable(query).filter(StringUtils::isNotBlank)
                    .map(s -> {
                        return s.substring(6, s.length() - 1);
                    }).orElse(null);
        }
        //4.判断token是否存在
        if (StringUtils.isBlank(token) && StringUtils.isBlank(tokenVal)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //5.判断token是否有效
        try {
            Claims claimsBody = null;
            if (StringUtils.isNotBlank(token)){
                claimsBody = AppJwtUtil.getClaimsBody(token);
            }else {
                claimsBody = AppJwtUtil.getClaimsBody(tokenVal);
            }

            //是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                String redirectUrl = "http://localhost:3000/";
                response.getHeaders().set(HttpHeaders.LOCATION, redirectUrl);
                //303状态码表示由于请求对应的资源存在着另一个URI，应使用GET方法定向获取请求的资源
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            Object userId = claimsBody.get("id");
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", userId + "");
            }).build();
            //重置请求
            exchange.mutate().request(serverHttpRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //6.放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置  值越小  优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
