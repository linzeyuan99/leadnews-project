package com.lzy.apis.article.fallback;

import com.lzy.apis.article.IArticleClient;
import com.lzy.model.article.dtos.ArticleDto;
import com.lzy.model.article.pojos.ApArticle;
import com.lzy.model.common.dtos.ResponseResult;
import com.lzy.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"获取数据失败");
    }

    @Override
    public ResponseResult doAddArticle(ArticleDto dto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"获取数据失败");
    }

    @Override
    public List<ApArticle> list() {
        return null;
    }

    @Override
    public String doUpdateSyn(ApArticle article) {
        return "update---article---timeoutFailed";
    }

    @Override
    public String doSuccess(long taskId) {
        return "update---article----Success---timeoutFailed";
    }
}
