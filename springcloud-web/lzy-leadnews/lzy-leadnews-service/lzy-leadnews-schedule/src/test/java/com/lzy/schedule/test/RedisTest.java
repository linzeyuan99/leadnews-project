package com.lzy.schedule.test;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzy.apis.article.IArticleClient;
import com.lzy.common.constants.RedisConstans;
import com.lzy.common.constants.ScheduleConstants;
import com.lzy.common.redis.CacheService;
import com.lzy.model.article.pojos.ApArticle;
import com.lzy.model.schedule.dtos.Task;
import com.lzy.model.schedule.pojos.LikesInfo;
import com.lzy.model.wemedia.pojos.WmNews;
import com.lzy.schedule.ScheduleApplication;
import com.lzy.schedule.mapper.LikesInfoMapper;
import com.lzy.schedule.service.LikesInfoService;
import com.lzy.schedule.service.TaskService;
import com.lzy.utils.common.ProtostuffUtil;
import com.sun.istack.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;


@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IArticleClient iArticleClient;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LikesInfoMapper likesInfoMapper;


    @Test
    public void test02(){
        System.out.println("---------找出所有的需要同步文章----------");
        QueryWrapper<ApArticle> wrapper = new QueryWrapper<ApArticle>();
        wrapper.eq("sync_status",1);//未同步
        List<ApArticle> list = iArticleClient.list();
        if (list != null && list.size() > 0){
            //关联 Taskinfo 和 Article的关系
            for (ApArticle article : list) {
                Task task = new Task();
                task.setExecuteTime(article.getCreatedTime().getTime());//转成Long
                task.setTaskId(article.getId());
                taskService.doAddTask(task);
                iArticleClient.doUpdateSyn(article);//更新状态
            }
        }
    }

    @Test
    public void test03(){
        try {
            Task task = taskService.newPoll();
            iArticleClient.doSuccess(task.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test04(){

        String token = cacheService.tryLock("FUTRUE_TASK_SYNC", 1000 * 30);

        if (StringUtils.isNotBlank(token)) {
            System.out.println("未来数据定时刷新---定时任务");

            //获取所有未来数据的集合key
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) {//future_100_50

                //获取当前数据的key  topic
                String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];

                //按照key和分值查询符合条件的数据
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());

                //同步数据
                if (!tasks.isEmpty()) {
                    cacheService.refreshWithPipeline(futureKey, topicKey, tasks);
                    System.out.println(("成功的将" + futureKey + "刷新到了" + topicKey));
                }
            }
        }
    }

//    @Test
//    public void testList(){
//
//        //在list的左边添加元素
////        cacheService.lLeftPush("list_001","hello,redis");
//
//        //在list的右边获取元素，并删除
//        String list_001 = cacheService.lRightPop("list_001");
//        System.out.println(list_001);
//    }
//
//    @Test
//    public void testZset(){
//        //添加数据到zset中  分值
//        /*cacheService.zAdd("zset_key_001","hello zset 001",1000);
//        cacheService.zAdd("zset_key_001","hello zset 002",8888);
//        cacheService.zAdd("zset_key_001","hello zset 003",7777);
//        cacheService.zAdd("zset_key_001","hello zset 004",999999);*/
//
//        //按照分值获取数据
//        Set<String> zset_key_001 = cacheService.zRangeByScore("zset_key_001", 0, 8888);
//        System.out.println(zset_key_001);
//    }
//
//    @Test
//    public void testKeys(){
//        Set<String> keys = cacheService.keys("future_*");
//        System.out.println(keys);
//
//        Set<String> scan = cacheService.scan("future_*");
//        System.out.println(scan);
//    }
//
//    //耗时6151
//    @Test
//    public  void testPiple1(){
//        long start =System.currentTimeMillis();
//        for (int i = 0; i <10000 ; i++) {
//            Task task = new Task();
//            task.setTaskType(1001);
//            task.setPriority(1);
//            task.setExecuteTime(new Date().getTime());
//            cacheService.lLeftPush("1001_1", JSON.toJSONString(task));
//        }
//        System.out.println("耗时"+(System.currentTimeMillis()- start));
//    }
//
//
//    @Test
//    public void testPiple2(){
//        long start  = System.currentTimeMillis();
//        //使用管道技术
//        List<Object> objectList = cacheService.getstringRedisTemplate().executePipelined(new RedisCallback<Object>() {
//            @Nullable
//            @Override
//            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
//                for (int i = 0; i <10000 ; i++) {
//                    Task task = new Task();
//                    task.setTaskType(1001);
//                    task.setPriority(1);
//                    task.setExecuteTime(new Date().getTime());
//                    redisConnection.lPush("1001_1".getBytes(), JSON.toJSONString(task).getBytes());
//                }
//                return null;
//            }
//        });
//        System.out.println("使用管道技术执行10000次自增操作共耗时:"+(System.currentTimeMillis()-start)+"毫秒");
//    }
//

    @Test
    public void test05(){
        LikesInfo info = new LikesInfo();
        info.setArticleId(10L);
        likesInfoMapper.insert(info);
    }
}
