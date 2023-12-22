package com.lzy.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzy.common.constants.RedisConstans;
import com.lzy.model.common.dtos.ResponseResult;
import com.lzy.model.common.enums.AppHttpCodeEnum;
import com.lzy.model.user.dtos.LlinLoginDto;
import com.lzy.model.user.dtos.LoginDto;
import com.lzy.model.user.pojos.ApUser;
import com.lzy.user.mapper.ApUserMapper;
import com.lzy.user.service.ApUserService;
import com.lzy.utils.common.AppJwtUtil;
import com.lzy.utils.thread.UserThreadUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    /**
     * app端登录功能
     *
     * @param dto
     * @return
     */

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResponseResult login(LoginDto dto) {
        //1.正常登录 用户名和密码
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())) {
            //1.1 根据手机号查询用户信息
            ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if (dbUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户信息不存在");
            }

            //1.2 比对密码
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pswd.equals(dbUser.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

            //1.3 返回数据  jwt  user
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            dbUser.setSalt("");
            dbUser.setPassword("");
            map.put("user", dbUser);

            return ResponseResult.okResult(map);
        } else {
            //2.游客登录
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }


    }

    @Override
    public ResponseResult linLogin(LlinLoginDto dto) {
        //不考虑手机号
        if (StringUtils.isNotBlank(dto.getName()) && StringUtils.isNotBlank(dto.getPassword())) {
            String isLockUser = stringRedisTemplate.opsForValue().get(RedisConstans.LOGIN_LOCK_KEY + dto.getName());
            if (StringUtils.isNotBlank(isLockUser)) {
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_LOCK, AppHttpCodeEnum.USER_LOCK.getresMsg());
            }

            ApUser one = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getName, dto.getName()));
            if (one == null) {
                //自动注册
                Random random = new Random();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < 3; i++) {
                    char randomChar = (char) (random.nextInt(26) + 'a');
                    sb.append(randomChar);
                }
                String salt = sb.toString();
                String pwd = dto.getPassword();
                String password = DigestUtils.md5DigestAsHex((pwd + salt).getBytes());
                ApUser apUser = new ApUser();
                apUser.setSalt(salt);
                apUser.setName(dto.getName());
                apUser.setPassword(password);
                apUser.setFlag((short) 0);
                apUser.setCreatedTime(new Date());
                save(apUser);
                //1.3 返回数据  jwt  user
                String token = AppJwtUtil.getToken(apUser.getId().longValue());
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);
                apUser.setSalt("");
                apUser.setPassword("");
                map.put("user", apUser);
                map.put("auto_tag", AppHttpCodeEnum.AUTO_LOGIN);
                return ResponseResult.okResult(map);
            } else {
                String salt = one.getSalt();
                String pwd = dto.getPassword();
                String digPwd = DigestUtils.md5DigestAsHex((pwd + salt).getBytes());
                if (!digPwd.equals(one.getPassword())) {
                    return checkAndLock(dto);
                }

                //1.3 返回数据  jwt  user
                String token = AppJwtUtil.getToken(one.getId().longValue());
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);
                one.setSalt("");
                one.setPassword("");
                map.put("user", one);
                return ResponseResult.okResult(map);
            }

        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "未知错误,请联系管理员！");
        }
    }

    @Override
    public ResponseResult ApUserList(Integer id) {
        if (id != null) {
            List<ApUser> list = list(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getId, id));
            if (list == null || list.size() == 0) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "未知错误,请联系管理员！");
            } else {
                return ResponseResult.okResult(list);
            }
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "服務器炸了!");
    }

    @Override
    public ResponseResult updatePwd(Integer userId, String oldPwd, String newPwd) {
//        ApUser user = UserThreadUtil.getUser();
//        Integer userId = user.getId();
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //redis中找是否有這個被鎖的賬號
        String value = stringRedisTemplate.opsForValue().get(RedisConstans.USER_LOCK_KEY + userId);
        if (value != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_LOCK);
        }
        if (StringUtils.isNotBlank(oldPwd) && StringUtils.isNotBlank(newPwd)) {
            ApUser apUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getId, userId));
            //判斷舊密碼是否正確
            if (apUser != null) {
                String salt = apUser.getSalt();
                String oldMdPwd = DigestUtils.md5DigestAsHex((oldPwd + salt).getBytes());
                if (!oldMdPwd.equals(apUser.getPassword())) {
                    return checkAndLockPwd(userId);
                }

                //修改密碼
                String digPwd = DigestUtils.md5DigestAsHex((newPwd + salt).getBytes());
                apUser.setPassword(digPwd);
                updateById(apUser);
                return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
            }
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "未知错误,请联系管理员！");
    }


    @Override
    public ResponseResult getPhoneCode(Integer id) {
        ApUser user = UserThreadUtil.getUser();
        Integer userId = user.getId();
        //先檢查用戶是否還有驗證碼
        String hasCode = stringRedisTemplate.opsForValue().get(RedisConstans.PHONE_CODE_KEY + userId);
        if (StringUtils.isNotBlank(hasCode)) {
            return ResponseResult.okResult(AppHttpCodeEnum.HAVE_CODE.getresMsg() + hasCode);
        }
        //沒有驗證碼發送一個并且設置過期時間
        int randomCode = RandomUtils.nextInt(100000, 999999);
        stringRedisTemplate.opsForValue().set(RedisConstans.PHONE_CODE_KEY + userId, randomCode + "", 60, TimeUnit.SECONDS);
        String res = "驗證碼是" + randomCode + ",有效期為一分鐘";
        return ResponseResult.okResult(res);
    }

    @Override
    public ResponseResult bindPhoneNum(LlinLoginDto dto) {
        ApUser user = UserThreadUtil.getUser();
        Integer userId = user.getId();
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getCode())) {
            String hasCode = stringRedisTemplate.opsForValue().get(RedisConstans.PHONE_CODE_KEY + userId);
            if (!dto.getCode().equals(hasCode) || StringUtils.isBlank(hasCode)) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "驗證碼失效！");
            }

            ApUser one = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getId, userId));
            if (one != null) {
                one.setPhone(dto.getPhone());
                updateById(one);
                return ResponseResult.okResult(dto.getPhone());
            }
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    //登錄
    private ResponseResult checkAndLock(LlinLoginDto dto) {
        String haveUser = stringRedisTemplate.opsForValue().get(RedisConstans.LOGIN_KEY + dto.getName());
        if (StringUtils.isBlank(haveUser)) {
            stringRedisTemplate.opsForValue().set(RedisConstans.LOGIN_KEY + dto.getName(), "1");
        } else {
            if (Integer.valueOf(haveUser) > 5) {
                stringRedisTemplate.delete(RedisConstans.LOGIN_KEY + dto.getName());
                stringRedisTemplate.opsForValue().set(RedisConstans.LOGIN_LOCK_KEY + dto.getName(), dto.getPassword(), 60, TimeUnit.SECONDS);
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_LOCK, AppHttpCodeEnum.USER_LOCK.getresMsg());
            }
            Long increVal = stringRedisTemplate.opsForValue().increment(RedisConstans.LOGIN_KEY + dto.getName(), 1);
            if (increVal > 5) {
                return ResponseResult.errorResult(AppHttpCodeEnum.OVER_REPEAT, AppHttpCodeEnum.OVER_REPEAT.getresMsg());
            }
            return ResponseResult.errorResult(AppHttpCodeEnum.REPEAT_LOCK, AppHttpCodeEnum.REPEAT_LOCK.getresMsg() + increVal);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, AppHttpCodeEnum.LOGIN_PASSWORD_ERROR.getresMsg());
    }

    private ResponseResult checkAndLockPwd(Integer userId) {
        //五分鐘都不讓修改密碼
        String isExist = stringRedisTemplate.opsForValue().get(RedisConstans.USER_KEY + userId);
        if (StringUtils.isBlank(isExist)) {
            stringRedisTemplate.opsForValue().set(RedisConstans.USER_KEY + userId, 1 + "");//第一次密码输入错误
        } else {
            if (Integer.valueOf(isExist) > 5) {
                stringRedisTemplate.opsForValue().set(RedisConstans.USER_LOCK_KEY + userId, userId + "", 60, TimeUnit.SECONDS);
                stringRedisTemplate.delete(RedisConstans.USER_KEY + userId);//刪除原來的key
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_LOCK, AppHttpCodeEnum.USER_LOCK.getresMsg());
            }
            Long increVal = stringRedisTemplate.opsForValue().increment(RedisConstans.USER_KEY + userId, 1);
            if (increVal > 5){
                return ResponseResult.errorResult(AppHttpCodeEnum.OVER_REPEAT, AppHttpCodeEnum.OVER_REPEAT.getresMsg());
            }
            return ResponseResult.errorResult(AppHttpCodeEnum.REPEAT_LOCK, AppHttpCodeEnum.REPEAT_LOCK.getresMsg() + increVal);
        }

        return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "原密碼錯誤！");
    }

}
