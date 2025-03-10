package com.hurryclear.usercenterbackend.service;

import com.hurryclear.usercenterbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author hurjiang
* @description 针对表【user】的数据库操作Service
* @createDate 2025-03-10 14:58:01
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);
}
