package com.lzy.model.schedule.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("collectinfo")
public class CollectInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("execute_time")
    private Date executeTime;
    @TableField("parameters")
    private byte[] parameters;
    @TableField("article_id")
    private Long articleId;
    @TableField("collect_user_id")
    private Long collectUserId;
}
