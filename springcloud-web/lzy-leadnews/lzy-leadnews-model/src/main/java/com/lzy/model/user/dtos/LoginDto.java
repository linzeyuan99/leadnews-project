package com.lzy.model.user.dtos;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginDto {

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号",required = true)
    private String phone;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码",required = true)
    private String password;

    @ApiModelProperty(value = "用户名",required = true)
    private String name;
}
