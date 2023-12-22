package com.lzy.article.test;



import com.lzy.article.ArticleApplication;

import com.lzy.article.mapper.ApArticleMapper;
import com.lzy.article.service.ApArticleService;
import com.lzy.common.constants.RedisConstans;
import com.lzy.common.redis.CacheService;
import freemarker.template.Configuration;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ApArticleService apArticleService;

    @Test
    public void test01(){
        int userId = 1; //点赞人
        Long likedId = 1732649434388500482l; //被点赞的文章
//        Long likedId = 1732652984741007361l; //被点赞的文章
//        Long likedId = 2l;
//        int likedId = 36;

        Boolean aBoolean = cacheService.sIsMember(RedisConstans.LIKE_KEY + likedId, userId+"");
        if (BooleanUtils.isFalse(aBoolean)){
            stringRedisTemplate.opsForSet().add(RedisConstans.LIKE_KEY + likedId,userId+"");
        }else {
            stringRedisTemplate.opsForSet().remove(RedisConstans.LIKE_KEY + likedId,userId+"");
        }
    }

    @Test
    public void test02(){
        int userId = 4;
        Long likedId = 1732649434388500482l; //被点赞的文章

        Boolean aBoolean = cacheService.sIsMember(RedisConstans.LIKE_KEY + likedId, userId+"");
        if (BooleanUtils.isFalse(aBoolean)){
            String token = cacheService.tryLock("LIKE_LOCK", 1000 * 30);
            if (StringUtils.isNotBlank(token)){
                stringRedisTemplate.opsForSet().add(RedisConstans.LIKE_KEY + likedId,userId+"");
                apArticleService.update().setSql("likes = likes + 1").eq("id",likedId).update();
            }
        }else {
            String token = cacheService.tryLock("LIKE_LOCK", 1000 * 30);
            if (StringUtils.isNotBlank(token)){
                stringRedisTemplate.opsForSet().remove(RedisConstans.LIKE_KEY + likedId,userId+"");
                apArticleService.update().setSql("likes = likes - 1").eq("id",likedId).update();
            }
        }
    }
}
