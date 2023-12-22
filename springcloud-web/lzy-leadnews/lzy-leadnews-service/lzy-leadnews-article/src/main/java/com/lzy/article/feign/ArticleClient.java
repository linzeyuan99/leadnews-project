package com.lzy.article.feign;

import com.lzy.apis.article.IArticleClient;
import com.lzy.article.service.ApArticleService;
import com.lzy.model.article.dtos.ArticleDto;
import com.lzy.model.article.pojos.ApArticle;
import com.lzy.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/api/v1/article/save")
    @Override
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }

    @PostMapping("/lin/article/addArtical")
    public ResponseResult doAddArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArt(dto);
    }

    @GetMapping("/lin/article/list")
    public List<ApArticle> list() {
        return apArticleService.selectList();
    }

    @PostMapping("/lin/article/updateSyn")
    public String doUpdateSyn(@RequestBody ApArticle article) {
        return apArticleService.update(article);
    }

    @PostMapping("/lin/article/updateSuccess")
    public String doSuccess(@RequestParam long taskId) {
        return apArticleService.updateSuccess(taskId);
    }


}

