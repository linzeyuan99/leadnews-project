package com.lzy.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzy.model.article.dtos.ArticleDto;
import com.lzy.model.article.dtos.ArticleHomeDto;
import com.lzy.model.article.pojos.ApArticle;
import com.lzy.model.article.pojos.ApComment;
import com.lzy.model.common.dtos.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApArticleService extends IService<ApArticle> {

    /**
     * 加载文章列表
     * @param dto
     * @param type  1 加载更多   2 加载最新
     * @return
     */
    public ResponseResult load(ArticleHomeDto dto,Short type);

    /**
     * 保存app端相关文章
     * @param dto
     * @return
     */
    public ResponseResult saveArticle(ArticleDto dto);

    ResponseResult saveArt(ArticleDto dto);

    List<ApArticle> selectList();

    String update(ApArticle article);

    String updateSuccess(long taskId);

    ResponseResult listAll(int authorId);

    ResponseResult doDelete(long id);

    ResponseResult doUpdate(ArticleDto dto);

    ResponseResult doNotification(int authorId);

    ResponseResult doUploadFile(MultipartFile file);

    ResponseResult listImage();

    ResponseResult browseList();

    ResponseResult liked(String idStr,String userId);

    ResponseResult collect(String idStr,String userId);
}
