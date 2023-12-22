package com.lzy.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzy.article.mapper.ApArticleMapper;
import com.lzy.article.mapper.ApCommentMapper;
import com.lzy.article.service.ApCommentService;
import com.lzy.model.article.dtos.ApCommentDto;
import com.lzy.model.article.pojos.ApArticle;
import com.lzy.model.article.pojos.ApComment;
import com.lzy.model.common.dtos.ResponseResult;
import com.lzy.model.common.enums.AppHttpCodeEnum;
import com.lzy.utils.common.DateUtils;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ApCommentServiceImpl extends ServiceImpl<ApCommentMapper, ApComment> implements ApCommentService {
    @Autowired
    private ApCommentMapper apCommentMapper;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Override
    public ResponseResult insert(ApComment comment) {
        if (comment != null) {
            comment.setCreatedTime(new Date());
            comment.setIsDeleted((short) 0);
            boolean save = save(comment);
            if (save) {
                Long articleId = comment.getArticleId();
                ApArticle one =
                        apArticleMapper.selectOne(Wrappers.<ApArticle>lambdaQuery().eq(ApArticle::getId, articleId));
                Optional.ofNullable(one)
                        .ifPresent(o -> {
                            o.setComment(o.getComment() + 1);
                            apArticleMapper.updateById(o);
                        });
            }
            return ResponseResult.okResult(comment);
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public ResponseResult commentList(String idStr) {
        Long articleId = Optional.ofNullable(idStr)
                .filter(StringUtils::isNoneBlank)
                .map(s -> Long.valueOf(s))
                .orElse(null);
        if (articleId != null) {
            QueryWrapper<ApComment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", articleId).eq("is_deleted", 0);
            List<ApComment> list = apCommentMapper.selectList(queryWrapper);
            List<ApCommentDto> resList = new ArrayList<>();
            //处理时间格式
            for (ApComment ap : list) {
                Date time = ap.getCreatedTime();
                ApCommentDto dto = new ApCommentDto();
                String timeStr = Optional.ofNullable(time)
                        .map(s -> DateUtils.dateToDateTime(s)).orElse(null);
                BeanUtils.copyProperties(ap,dto);
                dto.setCreatedTimeStr(timeStr);
                resList.add(dto);
            }
            return ResponseResult.okResult(resList);
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public ResponseResult deleteByid(Integer id) {
        if (id != null) {
            ApComment one = getOne(Wrappers.<ApComment>lambdaQuery().eq(ApComment::getId, id));
            if (one != null) {
                one.setIsDeleted((short) 1);
                boolean update = updateById(one);
                if (update){
                    Long articleId = one.getArticleId();
                    ApArticle ap =
                            apArticleMapper.selectOne(Wrappers.<ApArticle>lambdaQuery().eq(ApArticle::getId, articleId));

                    ap.setComment(ap.getComment() - 1);
                    apArticleMapper.updateById(ap);
                }
            }
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
    }
}
