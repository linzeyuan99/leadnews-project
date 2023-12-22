package com.lzy.model.article.dtos;

import com.lzy.model.article.pojos.ApArticle;
import lombok.Data;

@Data
public class ArticleDto  extends ApArticle {

    /**
     * 文章内容
     */
    private String content;

    private String name;

    private String publishTimeStr;

    private String idStr;//articleId

    private Boolean isLiked;

    private Boolean isCollected;
}
