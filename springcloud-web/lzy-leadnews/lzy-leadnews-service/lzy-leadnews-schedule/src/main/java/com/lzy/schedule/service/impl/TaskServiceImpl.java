package com.lzy.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lzy.apis.article.IArticleClient;
import com.lzy.common.constants.ScheduleConstants;
import com.lzy.common.redis.CacheService;
import com.lzy.model.article.dtos.ArticleDto;
import com.lzy.model.article.pojos.ApArticle;
import com.lzy.model.article.pojos.ApArticleConfig;
import com.lzy.model.schedule.dtos.Task;
import com.lzy.model.schedule.pojos.Taskinfo;
import com.lzy.model.schedule.pojos.TaskinfoLogs;
import com.lzy.schedule.mapper.TaskinfoLogsMapper;
import com.lzy.schedule.mapper.TaskinfoMapper;
import com.lzy.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {
    public static void main(String[] args) {
        String s = "future_100_50";
        String[] x = s.split("future_");
        for (String x1 : x) {
            System.out.println(x1 + "p");
        }
        System.out.println(s.split("future_")[1]);
    }

    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IArticleClient iArticleClient;


    /**
     * 添加延迟任务
     *
     * @param task
     * @return
     */
    @Override
    public long addTask(Task task) {
        //1.添加任务到数据库中

        boolean success = addTaskToDb(task);

        if (success) {
            //2.添加任务到redis
            addTaskToCache(task);
        }


        return task.getTaskId();
    }

    @Override
    public long doAddTask(Task task) {
        boolean flag = add2Db(task);
        if (flag) {
            add2Redis(task);
        }
        return task.getTaskId();
    }

    private void add2Redis(Task task) {
        String key = "lin";
        if (task.getTaskType() == null || task.getPriority() == null) {
            key = "re_re";
        } else {
            key = "le_le";
        }
        //获取5分钟之后的时间  毫秒值
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();

        //2.1 如果任务的执行时间小于等于当前时间，存入list
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else {
            //2.2 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
        }
    }

    private boolean add2Db(Task task) {
        boolean flag = false;
        if (task != null) {
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);

            //设置taskID
            task.setTaskId(taskinfo.getTaskId());

            //保存任务日志数据
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            TaskinfoLogs one = taskinfoLogsMapper.selectOne(Wrappers.<TaskinfoLogs>lambdaQuery()
                    .eq(TaskinfoLogs::getTaskId, task.getTaskId()));
            if (one == null){
                taskinfoLogs.setVersion(1);
                taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
                taskinfoLogsMapper.insert(taskinfoLogs);
            }else{
                taskinfoLogs.setVersion(1);
                taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
                taskinfoLogsMapper.updateById(taskinfoLogs);
            }

            flag = true;
        }
        return flag;
    }

    //定时审核----：任务推送 task => mysql => redis 生产者
    @Scheduled(cron = "0/5 * * * * ?")
    public void synTask() {
        log.info("---------找出所有的文章文章----------");
        QueryWrapper<ApArticle> wrapper = new QueryWrapper<ApArticle>();
        wrapper.eq("sync_status", 1);//未同步
        List<ApArticle> list = iArticleClient.list();
        log.info("---------需要处理的文章一共有：" + list.size() + "条---------");
        if (list != null && list.size() > 0) {
            //关联 Taskinfo 和 Article的关系
            for (ApArticle article : list) {
                Task task = new Task();
                task.setExecuteTime(article.getCreatedTime().getTime());//转成Long
                task.setTaskId(article.getId());
                this.doAddTask(task);
                iArticleClient.doUpdateSyn(article);//更新状态
            }
        }
    }

    //消费者
    @Scheduled(cron = "0/10 * * * * ?")
    public void fixTask() {
        log.info("---------找出在list中的要消费的任务----------");
        Task task = this.newPoll();
        if (task != null) {
            try {
                iArticleClient.doSuccess(task.getTaskId());
            } catch (Exception e) {
                e.printStackTrace();
                log.info("----消费的任务----failed:---");
            }
            log.info("----消费的任务----finish:---" + task.getTaskId());
        }
        log.info("----无消费任务--nothing---");
    }

    /**
     * 把任务添加到redis中
     *
     * @param task
     */
    private void addTaskToCache(Task task) {

        String key = task.getTaskType() + "_" + task.getPriority();

        //获取5分钟之后的时间  毫秒值
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();

        //2.1 如果任务的执行时间小于等于当前时间，存入list
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            //2.2 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(task), task.getExecuteTime());
        }


    }

    /**
     * 添加任务到数据库中
     *
     * @param task
     * @return
     */
    private boolean addTaskToDb(Task task) {

        boolean flag = false;

        try {
            //保存任务表
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);

            //设置taskID
            task.setTaskId(taskinfo.getTaskId());

            //保存任务日志数据
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);

            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }


    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    @Override
    public boolean cancelTask(long taskId) {

        boolean flag = false;

        //删除任务，更新任务日志
        Task task = updateDb(taskId, ScheduleConstants.CANCELLED);

        //删除redis的数据
        if (task != null) {
            removeTaskFromCache(task);
            flag = true;

        }
        return flag;
    }

    /**
     * 删除redis中的数据
     *
     * @param task
     */
    private void removeTaskFromCache(Task task) {

        String key = task.getTaskType() + "_" + task.getPriority();
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(task));
        }

    }

    /**
     * 删除任务，更新任务日志
     *
     * @param taskId
     * @param status
     * @return
     */
    private Task updateDb(long taskId, int status) {

        Task task = null;

        try {
            //删除任务
            taskinfoMapper.deleteById(taskId);

            //更新任务日志
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);

            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs, task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (Exception e) {
            log.error("task cancel exception taskId={}", taskId);
        }


        return task;
    }

    private Task newUpdateDb(long taskId, int status) {
        Task task = null;

        try {
            //删除任务
            taskinfoMapper.deleteById(taskId);

            //更新任务日志
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);

            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs, task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (Exception e) {
            log.error("new task cancel exception taskId={}", taskId);
        }


        return task;
    }

    /**
     * 按照类型和优先级拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    @Override
    public Task poll(int type, int priority) {
        Task task = null;

        try {
            String key = type + "_" + priority;

            //从redis中拉取数据  pop
            String task_json = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
            if (StringUtils.isNotBlank(task_json)) {
                task = JSON.parseObject(task_json, Task.class);

                //修改数据库信息
                updateDb(task.getTaskId(), ScheduleConstants.EXECUTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("poll task exception");
        }
        return task;
    }

    @Override
    public Task newPoll() {
        Task task = null;

        try {
            String key = "re_re";
            //从redis中拉取数据  pop
            String task_json = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
            if (StringUtils.isNotBlank(task_json)) {
                task = JSON.parseObject(task_json, Task.class);

                //修改数据库信息
                newUpdateDb(task.getTaskId(), ScheduleConstants.EXECUTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("new poll task exception");
        }


        return task;
    }

    /**
     * 未来数据定时刷新 刷新五分钟之后的未来数据
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh() {

        String token = cacheService.tryLock("FUTRUE_TASK_SYNC", 1000 * 30);

        if (StringUtils.isNotBlank(token)) {
            log.info("未来数据定时刷新---定时任务");

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
                    log.info("成功的将" + futureKey + "刷新到了" + topicKey);
                }
            }
        }
    }

}
