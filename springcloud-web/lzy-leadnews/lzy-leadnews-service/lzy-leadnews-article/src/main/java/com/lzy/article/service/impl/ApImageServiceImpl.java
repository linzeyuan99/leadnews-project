package com.lzy.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzy.article.mapper.ApImageMapper;
import com.lzy.article.service.ApImageService;
import com.lzy.model.article.pojos.ApImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ApImageServiceImpl extends ServiceImpl<ApImageMapper, ApImage> implements ApImageService {

}
