package com.lzy.model.user.dtos;

import com.lzy.model.user.pojos.ApUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LlinLoginDto extends ApUser {
    @ApiModelProperty(value = "密碼",required = true)
    private String password;

    @ApiModelProperty(value = "用戶名",required = true)
    private String name;

    @ApiModelProperty(value = "驗證碼",required = true)
    private String code;
}
