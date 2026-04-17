package com.gadhub.overseasproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gadhub.overseasproduct.dto.UserRegisterDTO;
import com.gadhub.overseasproduct.entity.User;
import com.gadhub.overseasproduct.mapper.UserMapper;
import com.gadhub.overseasproduct.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired  // 注入 Mapper
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Override
    public void register(UserRegisterDTO userRegisterDTO) {

//        密码格式校验
        validatePassword(userRegisterDTO.getPassword());
//        邮箱校验
        validateEmail(userRegisterDTO.getEmail());

        // 1. 校验密码和确认密码是否一致
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            throw new RuntimeException("两次密码输入不一致");
        }

        // 2. 检查邮箱是否已存在

        LambdaQueryWrapper<User> wrapperA = new LambdaQueryWrapper<>();// 用LambdaQueryWrapper来构造查询条件 防止注入

        wrapperA.eq(User::getEmail, userRegisterDTO.getEmail());// 构造查询条件判断邮箱是否已存在
        boolean existingUser = userMapper.exists(wrapperA); // 拿查询结果
        if (existingUser) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 2. 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapperB = new LambdaQueryWrapper<>();
        wrapperB.eq(User::getName, userRegisterDTO.getUsername());
        boolean repeatNames = userMapper.exists(wrapperB);
        if (repeatNames){
            throw new RuntimeException("用户名已存在");
        }


        // 3. DTO 转 Entity
        User user = new User();
        user.setName(userRegisterDTO.getUsername());
        user.setEmail(userRegisterDTO.getEmail());

        // 4. BCrypt密码加密使用
        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        user.setPassword(encodedPassword);

        // 5. 保存到数据库
        userMapper.insert(user);
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new RuntimeException("邮箱格式不正确");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("密码长度不能少于8位");
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new RuntimeException("密码必须包含字母和数字");
        }

    }
}
