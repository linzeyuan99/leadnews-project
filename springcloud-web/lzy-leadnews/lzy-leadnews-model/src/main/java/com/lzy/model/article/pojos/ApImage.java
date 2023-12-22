package com.lzy.model.article.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@TableName("ap_image")
public class ApImage {
    @TableId(value = "id",type = IdType.ID_WORKER)
    private Long id;

    @TableField("article_id")
    private Long articleId;


    @TableField("image_url")
    private String imageUrl;
}
