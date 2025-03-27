package com.hurryclear.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hurryclear.usercenterbackend.common.BaseResponse;
import com.hurryclear.usercenterbackend.common.ErrorCode;
import com.hurryclear.usercenterbackend.common.ResultUtils;
import com.hurryclear.usercenterbackend.exception.BusinessException;
import com.hurryclear.usercenterbackend.model.domain.User;
import com.hurryclear.usercenterbackend.model.domain.UserLoginRequest;
import com.hurryclear.usercenterbackend.model.domain.UserRegisterRequest;
import com.hurryclear.usercenterbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hurryclear.usercenterbackend.constant.UserConstant.ADMIN_ROLE;
import static com.hurryclear.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService; // UserService is an interface, why can you use interface instead of class of
    // interface

    // Register API
   @PostMapping("/register")
   public BaseResponse<Long > userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
       if (userRegisterRequest == null) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
       String userAccount = userRegisterRequest.getUserAccount();
       String userPassword = userRegisterRequest.getUserPassword();
       String checkPassword = userRegisterRequest.getCheckPassword();
       String planetCode = userRegisterRequest.getPlanetCode();

       if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
           return null;
       }
       if (!userPassword.equals(checkPassword)) {
           return null;
       }
       long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
       return ResultUtils.success(result);
    }

    // Login API
    @PostMapping("/login")
    public BaseResponse<User > userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
       if (userLoginRequest == null) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
       String userPassword = userLoginRequest.getUserPassword();
       String userAccount = userLoginRequest.getUserAccount();
       if (StringUtils.isAnyBlank(userAccount, userPassword)) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
       User user = userService.userLogin(userAccount, userPassword, request);
       return ResultUtils.success(user);
    }

    /**
     * User Logout
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer > userLogout(HttpServletRequest request) {
       if (request == null) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
       int result = userService.userLogout(request);
       return ResultUtils.success(result);
    };

    // Search
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
       // check role, only admin can search user
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "Not admin");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username); // find user with username in database
        }

        // please consider return user without sensitive information
//        return userService.list(queryWrapper); // using method from UserServie of MyBatis-plus
        // don't understand this code #todo
        // remove sensitive information
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }
    // Delete
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) { // why @RequestBody for id? #todo
       // check admin
        if (!isAdmin(request) || id < 0) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    // To get Login-State (for server to remember user)
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
       
       Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
       User currentUser = (User) userObj;
       if (currentUser == null) {
           throw new BusinessException(ErrorCode.NOT_LOGIN);
       }
       // what's the reason to do the following steps? We've had the currentUser, but you did get id of the current user and then get user with the id
        // is not the same user then?
       long userId = currentUser.getId();
       // TODO: is user valid?
       User user = userService.getById(userId);
       User safetyUser = userService.getSafetyUser(user);
       return ResultUtils.success(safetyUser);
    }

    // check admin role
    private boolean isAdmin (HttpServletRequest request) {

       Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }

        return true;
    }

}
