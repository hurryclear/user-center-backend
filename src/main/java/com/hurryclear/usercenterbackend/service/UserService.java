package com.hurryclear.usercenterbackend.service;

import com.hurryclear.usercenterbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author hurjiang
* @description 针对表【user】的数据库操作Service
* @createDate 2025-03-10 14:58:01
*/
public interface UserService extends IService<User> {

    /**
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * @param userAccount
     * @param userPassword
     * @param request
     * @return User user
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * @param user
     * @return safetyUser
     */
    User getSafetyUser(User user);
}
