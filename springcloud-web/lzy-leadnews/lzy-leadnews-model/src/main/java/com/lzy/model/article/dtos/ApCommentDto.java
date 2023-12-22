package com.lzy.model.article.dtos;

import com.lzy.model.article.pojos.ApComment;
import lombok.Data;

@Data
public class ApCommentDto extends ApComment {
    private String createdTimeStr;

    private String authorName;
}
