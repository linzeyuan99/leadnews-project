package com.lzy.schedule.feign;

import com.lzy.apis.article.IArticleClient;
import com.lzy.apis.schedule.IScheduleClient;
import com.lzy.model.common.dtos.ResponseResult;
import com.lzy.model.schedule.dtos.Task;
import com.lzy.schedule.service.TaskService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ScheduleClient implements IScheduleClient {

    @Autowired
    private TaskService taskService;

    @Autowired
    private IArticleClient iArticleClient;
    /**
     * 添加延迟任务
     *
     * @param task
     * @return
     */
    @PostMapping("/api/v1/task/add")
    public ResponseResult addTask(@RequestBody Task task) {
        return ResponseResult.okResult(taskService.addTask(task));
    }

    @PostMapping("/lin/task/add")
    public ResponseResult doAddTask(@RequestBody Task task) {
        return ResponseResult.okResult(taskService.doAddTask(task));
    }


    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    @GetMapping("/api/v1/task/{taskId}")
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId){
        return ResponseResult.okResult(taskService.cancelTask(taskId));
    }

    /**
     * 按照类型和优先级拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    @GetMapping("/api/v1/task/{type}/{priority}")
    public ResponseResult poll(@PathVariable("type") int type,@PathVariable("priority") int priority) {
        return ResponseResult.okResult(taskService.poll(type,priority));
    }

    @GetMapping("/lin/test")
    public ResponseResult test(){
        return ResponseResult.okResult(iArticleClient.list());
    }
}
