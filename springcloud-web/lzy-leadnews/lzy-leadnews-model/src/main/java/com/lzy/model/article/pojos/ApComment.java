package com.lzy.model.article.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ap_comment")
public class ApComment {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("article_id")
    private Long articleId;

    @TableField("author_id")
    private Long authorId;

    @TableField("comment_name")
    private String commentName;

    @TableField("comment")
    private String comment;

    @TableField("comment_id")
    private Integer commentId;

    @TableField("created_time")
    private Date createdTime;

    @TableField("is_deleted")
    private Short isDeleted;
}
