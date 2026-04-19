package com.gadhub.overseasproduct.service.impl;
import com.gadhub.overseasproduct.common.constant.ErrorCode;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gadhub.overseasproduct.common.exception.BusinessException;
import com.gadhub.overseasproduct.dto.UserLoginDTO;
import com.gadhub.overseasproduct.dto.UserRegisterDTO;
import com.gadhub.overseasproduct.entity.User;
import com.gadhub.overseasproduct.mapper.UserMapper;
import com.gadhub.overseasproduct.service.UserService;
import com.gadhub.overseasproduct.util.JwtUtil;
import com.gadhub.overseasproduct.vo.UserLoginVO;
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
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 2. 检查邮箱是否已存在

        LambdaQueryWrapper<User> wrapperA = new LambdaQueryWrapper<>();// 用LambdaQueryWrapper来构造查询条件 防止注入

        wrapperA.eq(User::getEmail, userRegisterDTO.getEmail());// 构造查询条件判断邮箱是否已存在
        boolean existingUser = userMapper.exists(wrapperA); // 拿查询结果
        if (existingUser) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapperB = new LambdaQueryWrapper<>();
        wrapperB.eq(User::getName, userRegisterDTO.getUsername());
        boolean repeatNames = userMapper.exists(wrapperB);
        if (repeatNames){
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
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

    public UserLoginVO login(UserLoginDTO userLoginDTO){

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>(); //LambdaQueryWrapper实例
        wrapper.eq(User::getName, userLoginDTO.getUsername()); //生成语句

        User user = userMapper.selectOne(wrapper); //拿返回
        if (user == null){ // 有数据才返回，没数据则返回null，空代表没这个人
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 用bycrypt加密，所以要passwordEncoder.matches()一样为true，取反不让进
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())){
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

//        验证通过 拿数据赋值返回
        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getName());
        loginVO.setEmail(user.getEmail());
        loginVO.setToken(JwtUtil.generateToken(user.getId()));
        return loginVO;

    }



    private void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException(ErrorCode.PASSWORD_TOO_SHORT);
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new BusinessException(ErrorCode.PASSWORD_FORMAT_ERROR);
        }

    }
}
