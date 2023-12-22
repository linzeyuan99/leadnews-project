package com.lzy.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.lzy.model.article.pojos.ApComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApCommentMapper extends BaseMapper<ApComment> {
}
