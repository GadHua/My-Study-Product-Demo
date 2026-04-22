package com.gadhub.overseasproduct.service;

import com.gadhub.overseasproduct.dto.UserLoginDTO;
import com.gadhub.overseasproduct.dto.UserRegisterDTO;
import com.gadhub.overseasproduct.vo.UserInfoVO;
import com.gadhub.overseasproduct.vo.UserLoginVO;

public interface UserService {
    void register(UserRegisterDTO userRegisterDTO);

    UserLoginVO login(UserLoginDTO userLoginDTO);

    UserInfoVO getUserInfo(Long userId);
}
