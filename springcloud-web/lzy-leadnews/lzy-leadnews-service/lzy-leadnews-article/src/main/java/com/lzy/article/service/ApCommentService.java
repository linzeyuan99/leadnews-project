package com.lzy.article.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lzy.model.article.pojos.ApComment;
import com.lzy.model.common.dtos.ResponseResult;

public interface ApCommentService extends IService<ApComment> {

    ResponseResult insert(ApComment comment);

    ResponseResult commentList(String idStr);

    ResponseResult deleteByid(Integer id);

}
