package com.gadhub.overseasproduct.controller;

import com.gadhub.overseasproduct.common.annotation.RateLimit;
import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.dto.UpdateUserInfoDTO;
import com.gadhub.overseasproduct.dto.UserLoginDTO;
import com.gadhub.overseasproduct.dto.UserRegisterDTO;
import com.gadhub.overseasproduct.service.UserService;
import com.gadhub.overseasproduct.util.UserContextUtil;
import com.gadhub.overseasproduct.vo.UserInfoVO;
import com.gadhub.overseasproduct.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/backend/user")
@Tag(name = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @RateLimit(time = 300, count = 3, message = "注册次数过多，请5分钟后再试")
    public Result register(@RequestBody @Valid UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    @RateLimit(key = "#userLoginDTO.username", time = 60, count = 5, message = "登录尝试次数过多，请60秒后再试")
    public Result login(@RequestBody @Valid UserLoginDTO dto) {
        UserLoginVO loginVO = userService.login(dto);
        return Result.success(loginVO);
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        Long userId = UserContextUtil.getCurrentUserId();
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @PutMapping("/info")
    @RateLimit(time = 60, count = 10)
    public Result updateUserInfo(@RequestBody @Valid UpdateUserInfoDTO dto) {
        Long userId = UserContextUtil.getCurrentUserId();
        userService.updateUserInfo(userId, dto);
        return Result.success();
    }


}
