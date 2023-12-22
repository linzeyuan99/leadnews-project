package com.lzy.article.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.JsonObject;
import com.lzy.article.mapper.ApArticleConfigMapper;
import com.lzy.article.mapper.ApArticleContentMapper;
import com.lzy.article.mapper.ApArticleMapper;
import com.lzy.article.mapper.ApImageMapper;
import com.lzy.article.service.ApArticleService;
import com.lzy.common.constants.ArticleConstants;
import com.lzy.common.constants.RedisConstans;
import com.lzy.common.redis.CacheService;
import com.lzy.model.article.dtos.ArticleDto;
import com.lzy.model.article.dtos.ArticleHomeDto;
import com.lzy.model.article.pojos.*;
import com.lzy.model.common.dtos.ResponseResult;
import com.lzy.model.common.enums.AppHttpCodeEnum;
import com.lzy.utils.common.DateUtils;
import com.lzy.utils.thread.AtThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Watchable;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApImageMapper apImageMapper;


    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private final static short MAX_PAGE_SIZE = 50;

    /**
     * 加载文章列表
     *
     * @param dto
     * @param type 1 加载更多   2 加载最新
     * @return
     */
    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {
        //1.检验参数
        //分页条数的校验
        Integer size = dto.getSize();
        if (size == null || size == 0) {
            size = 10;
        }
        //分页的值不超过50
        size = Math.min(size, MAX_PAGE_SIZE);


        //校验参数  -->type
        if (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        //频道参数校验
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if (dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        if (dto.getMinBehotTime() == null) dto.setMinBehotTime(new Date());

        //2.查询
        List<ApArticle> articleList = apArticleMapper.loadArticleList(dto, type);
        //3.结果返回
        return ResponseResult.okResult(articleList);
    }


    /**
     * 保存app端相关文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {

//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        //2.判断是否存在id
        if (dto.getId() == null) {
            //2.1 不存在id  保存  文章  文章配置  文章内容

            //保存文章
            save(apArticle);

            //保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //保存 文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);

        } else {
            //2.2 存在id   修改  文章  文章内容

            //修改  文章
            updateById(apArticle);

            //修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        //3.结果返回  文章的id
        return ResponseResult.okResult(apArticle.getId());
    }

    @Override
    public ResponseResult saveArt(ArticleDto dto) {
        String time = dto.getPublishTimeStr();
        Date createdTime = Optional.ofNullable(time)
                .filter(StringUtils::isNoneBlank)
                .map(s -> {
                    try {
                        Date date = DateUtils.str2DateTime(s);
                        return date;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).orElse(null);
        try {
            ApArticle apArticle = new ApArticle();
            apArticle.setAuthorId(dto.getAuthorId());
            apArticle.setAuthorName(dto.getAuthorName());
            apArticle.setCreatedTime(createdTime);
            apArticle.setSyncStatus(1);//未同步
            apArticle.setIsSuccess((short) 0);//未审核
            apArticle.setIsDeleted((short) 0);
            save(apArticle);

            Long articleId = apArticle.getId();
            ApArticleContent ac = new ApArticleContent();
            ac.setContent(dto.getContent());
            ac.setArticleId(articleId);
            apArticleContentMapper.insert(ac);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("發佈文章失敗----");
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public List<ApArticle> selectList() {
        QueryWrapper<ApArticle> wrapper = new QueryWrapper<>();
        wrapper.eq("sync_status", 1);
        List<ApArticle> list = apArticleMapper.selectList(wrapper);
        return list;
    }

    @Override
    public String update(ApArticle article) {
        try {
            article.setSyncStatus(0);
            updateById(article);
            return "success for article id = " + article.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "update---ApArticle---success";
    }

    @Override
    public String updateSuccess(long taskId) {
        try {
            ApArticle apArticle = getOne(Wrappers.<ApArticle>lambdaQuery().eq(ApArticle::getId, taskId));
            apArticle.setIsSuccess((short) 1);
            updateById(apArticle);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "failed----isSuccess";
    }

    @Override
    public ResponseResult listAll(int authorId) {
        List<ApArticle> list = list(Wrappers.<ApArticle>lambdaQuery()
                .eq(ApArticle::getAuthorId, authorId)
                .eq(ApArticle::getIsDeleted, 0)
                .orderByDesc(ApArticle::getCreatedTime)
        );

        List<ArticleDto> newList = new ArrayList<>();
        for (ApArticle entity : list) {
            ArticleDto dto = new ArticleDto();
            BeanUtils.copyProperties(entity, dto);
            ArticleDto articleDto = setlikeOrCollect(entity, dto);
            Long id = entity.getId();
            List<ApArticleContent> contentsList =
                    apArticleContentMapper.selectList(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, id));
            if (contentsList != null && contentsList.size() > 0) {
                ApArticleContent aapc = contentsList.get(0);
                String cntent = aapc.getContent();
                articleDto.setContent(cntent);
            }
            Date createdTime = entity.getCreatedTime();
            String publishStr = Optional.ofNullable(createdTime)
                    .map(s -> DateUtils.dateToDateTime(s)).orElse(null);
            articleDto.setPublishTimeStr(publishStr);
            articleDto.setIdStr(entity.getId().toString());
            newList.add(articleDto);
        }
        return ResponseResult.okResult(newList);
    }

    private ArticleDto setlikeOrCollect(ApArticle entity, ArticleDto dto) {
        Long articleId = entity.getId();//文章id
        Long userId = AtThreadLocalUtil.getUser().getId();
        Boolean isMemL = stringRedisTemplate.opsForSet().isMember(RedisConstans.LIKE_KEY + articleId, userId + "");
        dto.setIsLiked(BooleanUtils.isTrue(isMemL));
        Boolean isMemC = stringRedisTemplate.opsForSet().isMember(RedisConstans.COLLECT_KEY + articleId, userId + "");
        dto.setIsCollected(BooleanUtils.isTrue(isMemC));
        return dto;
    }

    @Override
    public ResponseResult doDelete(long id) {
        ApArticle one = getOne(Wrappers.<ApArticle>lambdaQuery().eq(ApArticle::getId, id));
        if (one != null) {
            one.setIsDeleted((short) 1);
            try {
                updateById(one);
                return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult doUpdate(ArticleDto dto) {
        try {
            boolean flag = false;
            String idStr = dto.getIdStr();
            Long id = Long.valueOf(idStr);
            String content = dto.getContent();
            Date createdTime = Optional.ofNullable(dto.getPublishTimeStr())
                    .filter(StringUtils::isNotBlank)
                    .map(s -> DateUtils.str2DateTime(s))
                    .orElse(null);

            ApArticleContent one = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                    .eq(ApArticleContent::getArticleId, id));
            if (one != null) {
                one.setContent(content);
                apArticleContentMapper.updateById(one);
                flag = true;
            }

            if (flag) {
                //更改发布时间
                ApArticle apArticle = new ApArticle();
                BeanUtils.copyProperties(dto, apArticle);
                apArticle.setId(id);//前端传递id有误,丢失精度所以用这个
                apArticle.setCreatedTime(createdTime);
                apArticle.setSyncStatus(1);//未同步
                apArticle.setIsSuccess((short) 0);//未审核
                apArticleMapper.updateById(apArticle);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("更新文章操作失败");
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult doNotification(int authorId) {
        ApArticle user = AtThreadLocalUtil.getUser();

        List<ApArticle> articleList = apArticleMapper.selectList(Wrappers.<ApArticle>lambdaQuery()
                .eq(ApArticle::getAuthorId, authorId)
        );
        ArrayList<Long> idList = new ArrayList<>();
        ArrayList<Map> resList = new ArrayList<>();

        if (articleList != null && articleList.size() > 0) {
            for (ApArticle ap : articleList) {
                if (ap.getIsSuccess() == 1) {
                    idList.add(ap.getId());
                }
            }
        }

        QueryWrapper<ApArticleContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("article_id", idList);
        List<ApArticleContent> contentList = apArticleContentMapper.selectList(queryWrapper);
        for (ApArticleContent ap : contentList) {
            HashMap<String, String> mapRes = new HashMap<>();
            String content = ap.getContent();
            String substring = content.substring(0, 10);
            String res = "您的文章内容為：" + substring + "......已經通過審核~";
            mapRes.put("noti", res);
            resList.add(mapRes);
        }
        return ResponseResult.okResult(resList);
    }

    @Override
    public ResponseResult doUploadFile(MultipartFile file) {
        if (file == null && file.isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_IMAGE_FORMAT_ERROR);
        }
        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "." + originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String path = "D:\\Desktop\\admin-web\\admin-react-web\\src\\images\\";//前端文件夹
        File dest = new File(path + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }
        try {
            file.transferTo(dest);
            addImage2Db(fileName);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public ResponseResult listImage() {
        QueryWrapper<ApImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("image_url");
        List<ApImage> imageList = apImageMapper.selectList(queryWrapper);
        return ResponseResult.okResult(imageList);
    }

    @Override
    public ResponseResult browseList() {
        QueryWrapper<ApArticle> wrapper = new QueryWrapper<>();
        wrapper.lambda();
        List<ApArticle> list = apArticleMapper.selectList(wrapper);

        List<ArticleDto> newList = new ArrayList<>();
        for (ApArticle entity : list) {
            ArticleDto dto = new ArticleDto();
            BeanUtils.copyProperties(entity, dto);
            ArticleDto articleDto = setlikeOrCollect(entity, dto);
            Long id = entity.getId();
            List<ApArticleContent> contentsList =
                    apArticleContentMapper.selectList(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, id));
            if (contentsList != null && contentsList.size() > 0) {
                ApArticleContent aapc = contentsList.get(0);
                String cntent = aapc.getContent();
                articleDto.setContent(cntent);
            }
            Date createdTime = entity.getCreatedTime();
            String publishStr = Optional.ofNullable(createdTime)
                    .map(s -> DateUtils.dateToDateTime(s)).orElse(null);
            articleDto.setPublishTimeStr(publishStr);
            articleDto.setIdStr(entity.getId().toString());

            newList.add(articleDto);
        }
        return ResponseResult.okResult(newList);
    }

    @Override
    public ResponseResult liked(String idStr, String userId) {
        if (StringUtils.isNotBlank(idStr)) {
            boolean flag = false;
            Long likedId = Long.valueOf(idStr);//被點贊的文章id
            Long id = Optional.ofNullable(userId)
                    .filter(StringUtils::isNotBlank)
                    .map(s -> Long.valueOf(s)).orElse(null);

            Boolean aBoolean = cacheService.sIsMember(RedisConstans.LIKE_KEY + likedId, id + "");

            try {
                if (BooleanUtils.isFalse(aBoolean)) {
                    String token = cacheService.tryLock("LIKE_LOCK", 1000 * 2);
                    if (StringUtils.isNotBlank(token)) {
                        stringRedisTemplate.opsForSet().add(RedisConstans.LIKE_KEY + likedId, id + "");
                        update().setSql("likes = likes + 1").eq("id", likedId).update();
                        flag = true;
                    }
                } else {
                    String token = cacheService.tryLock("LIKE_LOCK", 1000 * 2);
                    if (StringUtils.isNotBlank(token)) {
                        stringRedisTemplate.opsForSet().remove(RedisConstans.LIKE_KEY + likedId, id + "");
                        update().setSql("likes = likes - 1").eq("id", likedId).update();
                        flag = false;
                    }
                }
            } catch (Exception e) {
                log.info("------更新點贊數據失敗------");
                e.printStackTrace();
            }
            return ResponseResult.okResult(flag);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult collect(String idStr, String userId) {
        if (StringUtils.isNotBlank(idStr)) {
            boolean flag = false;
            Long collectId = Long.valueOf(idStr);//被點贊的文章id
            Long id = Optional.ofNullable(userId)
                    .filter(StringUtils::isNotBlank)
                    .map(s -> Long.valueOf(s)).orElse(null);

            Boolean aBoolean = cacheService.sIsMember(RedisConstans.COLLECT_KEY + collectId, id + "");

            try {
                if (BooleanUtils.isFalse(aBoolean)) {
                    String token = cacheService.tryLock("COLLECTION_LOCK", 1000 * 2);
                    if (StringUtils.isNotBlank(token)) {
                        stringRedisTemplate.opsForSet().add(RedisConstans.COLLECT_KEY + collectId, id + "");
                        update().setSql("collection = collection + 1").eq("id", collectId).update();
                        flag = true;
                    }
                } else {
                    String token = cacheService.tryLock("COLLECTION_LOCK", 1000 * 2);
                    if (StringUtils.isNotBlank(token)) {
                        stringRedisTemplate.opsForSet().remove(RedisConstans.COLLECT_KEY + collectId, id + "");
                        update().setSql("collection = collection - 1").eq("id", collectId).update();
                        flag = false;
                    }
                }
            } catch (Exception e) {
                log.info("------更新收藏數據失敗------");
                e.printStackTrace();
            }

            return ResponseResult.okResult(flag);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }


    private void addImage2Db(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            ApImage apImage = new ApImage();
            apImage.setImageUrl(fileName);
            apImageMapper.insert(apImage);
        }
    }
}
