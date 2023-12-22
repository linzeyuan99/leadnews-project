package com.lzy.user.controller.lin;


import com.lzy.model.common.dtos.ResponseResult;
import com.lzy.model.user.dtos.LlinLoginDto;
import com.lzy.model.user.pojos.ApUser;
import com.lzy.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lin/user")
@Api(value = "lin用户登录",tags = "lin用户登录")

public class UserLoginController {
    @Autowired
    private ApUserService apUserService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public ResponseResult login(@RequestBody LlinLoginDto dto){
        return apUserService.linLogin(dto);
    }

    @GetMapping("/list")
    @ApiOperation("查詢用戶信息")
    public ResponseResult list(@RequestParam("id") Integer id){
        ResponseResult result = apUserService.ApUserList(id);
        return result;
    }

    @PostMapping("/updatePwd")
    @ApiOperation("修改密码")
    public ResponseResult changePwd(@RequestParam("userId") Integer userId,@RequestParam("oldPwd") String oldPwd,@RequestParam("newPwd") String newsPwd){
        return apUserService.updatePwd(userId,oldPwd,newsPwd);
    }

    @GetMapping("/getCode")
    public ResponseResult getCode(@RequestParam("userId") Integer id){
        return apUserService.getPhoneCode(id);
    }

    @PostMapping("/bindPhoneNum")
    public ResponseResult bindPhoneNum(@RequestBody LlinLoginDto dto){
        return apUserService.bindPhoneNum(dto);
    }
}
