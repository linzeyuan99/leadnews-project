package com.lzy.article.controller.lin;

import com.baomidou.mybatisplus.extension.api.R;
import com.lzy.article.service.ApArticleService;

import com.lzy.article.service.ApCommentService;
import com.lzy.article.service.ApImageService;
import com.lzy.model.article.dtos.ArticleDto;
import com.lzy.model.article.pojos.ApArticle;
import com.lzy.model.article.pojos.ApComment;
import com.lzy.model.common.dtos.ResponseResult;
import com.lzy.model.common.enums.AppHttpCodeEnum;
import lombok.AllArgsConstructor;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/lin/article")
public class ArticleController {
    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private ApCommentService apCommentService;

    @GetMapping("/listAll")
    public ResponseResult listAll(@RequestParam int authorId) {
        return apArticleService.listAll(authorId);
    }

    @PostMapping("/deleted")
    public ResponseResult doDelete(@RequestParam long id) {
        return apArticleService.doDelete(id);
    }

    @PostMapping("/update")
    public ResponseResult doUpdate(@RequestBody ArticleDto dto) {
        return apArticleService.doUpdate(dto);
    }

    @GetMapping("/notification")
    public ResponseResult notification(@RequestParam int authorId) {
        return apArticleService.doNotification(authorId);
    }

    @PostMapping("/uploadImg")
    public ResponseResult uploadImg(@RequestParam(value = "file", required = false) MultipartFile file) {
        return apArticleService.doUploadFile(file);
    }

    @GetMapping("/imageList")
    public ResponseResult listImage(){
        return apArticleService.listImage();
    }

    @GetMapping("/browseList")
    public ResponseResult browseList(){
        return apArticleService.browseList();
    }

    @PostMapping("/like")
    public ResponseResult liked(@RequestParam String idStr,@RequestParam String userId){
        return apArticleService.liked(idStr,userId);
    }

    @PostMapping("/collect")
    public ResponseResult collect(@RequestParam String idStr,@RequestParam String userId){
        return apArticleService.collect(idStr,userId);
    }

    @PostMapping("/comment")
    public ResponseResult comment(@RequestBody ApComment comment){
        return apCommentService.insert(comment);
    }

    @GetMapping("/commentList")
    public ResponseResult commentList(@RequestParam String idStr){
        return apCommentService.commentList(idStr);
    }

    @PostMapping("/delete")
    public ResponseResult delete(@RequestParam Integer id){
        return apCommentService.deleteByid(id);
    }


}
