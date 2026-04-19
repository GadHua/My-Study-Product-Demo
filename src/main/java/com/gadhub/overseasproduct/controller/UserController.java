package com.gadhub.overseasproduct.controller;

import com.gadhub.overseasproduct.common.result.Result;
import com.gadhub.overseasproduct.dto.UserLoginDTO;
import com.gadhub.overseasproduct.dto.UserRegisterDTO;
import com.gadhub.overseasproduct.service.UserService;
import com.gadhub.overseasproduct.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/backend/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody @Valid UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestBody @Valid UserLoginDTO dto) {
        UserLoginVO loginVO = userService.login(dto);
        return Result.success(loginVO);
    }

}
