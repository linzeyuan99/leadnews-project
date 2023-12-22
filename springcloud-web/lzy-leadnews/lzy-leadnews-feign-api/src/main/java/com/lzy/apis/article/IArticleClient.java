package com.lzy.apis.article;

import com.lzy.apis.article.fallback.IArticleClientFallback;
import com.lzy.model.article.dtos.ArticleDto;
import com.lzy.model.article.pojos.ApArticle;
import com.lzy.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "leadnews-article",fallback = IArticleClientFallback.class)
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto);

    @PostMapping("/lin/article/addArtical")
    public ResponseResult doAddArticle(@RequestBody ArticleDto dto);

    @GetMapping("/lin/article/list")
    public List<ApArticle> list();

    @PostMapping("/lin/article/updateSyn")
    public String doUpdateSyn(@RequestBody ApArticle article);

    @PostMapping("/lin/article/updateSuccess")
    public String doSuccess(@RequestParam long taskId);
}
