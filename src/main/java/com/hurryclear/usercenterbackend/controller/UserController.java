package com.hurryclear.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
   public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
       if (userRegisterRequest == null) {
           return null;
       }
       String userAccount = userRegisterRequest.getUserAccount();
       String userPassword = userRegisterRequest.getUserPassword();
       String checkPassword = userRegisterRequest.getCheckPassword();
       if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
           return null;
       }
       if (!userPassword.equals(checkPassword)) {
           return null;
       }
       return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    // Login API
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
       if (userLoginRequest == null) {
           return null;
       }
       String userPassword = userLoginRequest.getUserPassword();
       String userAccount = userLoginRequest.getUserAccount();
       if (StringUtils.isAnyBlank(userAccount, userPassword)) {
           return null;
       }
       return userService.userLogin(userAccount, userPassword, request);
    }

    // Search
    @PostMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
       // check role, only admin can search user
        if (!isAdmin(request)) {
            return new ArrayList<>();
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
        return userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }
    // Delete
    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) { // why @RequestBody for id? #todo
       // check admin
        if (!isAdmin(request) || id < 0) {
            return false;
        }
        return userService.removeById(id);
    }

    // To get Login-State (for server to remember user)
    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {
       
       Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
       User currentUser = (User) userObj;
       if (currentUser == null) {
           return null;
       }
       // what's the reason to do the following steps? We've had the currentUser, but you did get id of the current user and then get user with the id
        // is not the same user then?
       long userId = currentUser.getId();
       // TODO: is user valid?
       User user = userService.getById(userId);
       return userService.getSafetyUser(user);
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
