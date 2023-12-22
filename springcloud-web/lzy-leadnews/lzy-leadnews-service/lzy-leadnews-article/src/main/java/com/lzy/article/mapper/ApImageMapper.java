package com.lzy.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzy.model.article.pojos.ApImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApImageMapper extends BaseMapper<ApImage> {

}
