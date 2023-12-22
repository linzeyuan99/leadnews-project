package com.lzy.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzy.model.common.dtos.ResponseResult;
import com.lzy.model.user.dtos.LlinLoginDto;
import com.lzy.model.user.dtos.LoginDto;
import com.lzy.model.user.pojos.ApUser;
import io.swagger.models.auth.In;
import org.apache.zookeeper.Login;

import java.util.List;

public interface ApUserService extends IService<ApUser> {
    /**
     * app端登录功能
     * @param dto
     * @return
     */
    public ResponseResult login(LoginDto dto);

    public ResponseResult linLogin(LlinLoginDto dto);

    ResponseResult ApUserList(Integer id);

    ResponseResult updatePwd(Integer userId,String oldPwd, String newPwd);

    ResponseResult getPhoneCode(Integer id);

    ResponseResult bindPhoneNum(LlinLoginDto dto);
}
