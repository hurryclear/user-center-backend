package com.hurryclear.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hurryclear.usercenterbackend.common.ErrorCode;
import com.hurryclear.usercenterbackend.exception.BusinessException;
import com.hurryclear.usercenterbackend.model.domain.User;
import com.hurryclear.usercenterbackend.service.UserService;
import com.hurryclear.usercenterbackend.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hurryclear.usercenterbackend.constant.UserConstant.USER_LOGIN_STATE;


/**
* @author hurjiang
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-03-10 14:58:01
*/
@Service
@Log
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private static final String SALT = "hurryclear";


    @Resource
    private UserMapper userMapper;



    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. check not null --> using StringUtils.isAnyBlank from Apache Commons Lang
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "parameter is null");
        }

        // 2. check userAccount not shorter than 4 digits
        if (userAccount.length() < 4 ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "user account too short");
        }
        // 3. check userPassword not shorter than 8 digits
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "user password too short");
        }

        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "planet code too long");
        }

        // 5. no special character in userAccount
        String invalidPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(invalidPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }

        // 6. userPassword same with checkPassword
        if (! userPassword.equals(checkPassword)) {
            return -1;
        }

        // 4. unique userAccount --> method 1: QueryWrapper in MyBatis-plus
        // Why put this step at last? because we do query once here in database, if something wrong before this, then no need to query, save resource
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "account exit!");
        }

        // planet code must be unique
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "planet code exit!");
        }

        // 7. encrypt password --> md5
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 8. save new user in database
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (! saveResult) {
            return -1;
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        // 1. check userAccount, userPassword
        // 1.1 not null
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        // 1.2 userAccount not shorter than 4
        if (userAccount.length() < 4) {
            return null;
        }
        // 1.3 userPassword not shorter than 8
        if (userPassword.length() < 8) {
            return null;
        }
        // 1.4 no special characters
        String invalidPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(invalidPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        // 2. check password (password and encryptPassword in database)
        // so this is to user MyBatis-plus (QueryWrapper) to interact with database
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("Login fails, account doesn't match password");
            return null;
        }

        // 3. hide sensitive information
        User safetyUser = getSafetyUser(user);

        // 4. save session (user states)
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * User Logout Logic
     * remove attribute of session
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public User getSafetyUser(User originalUser) {

        if (originalUser == null) {
            return null;
        }
        User safetyUser = new User();

        safetyUser.setId(originalUser.getId());
        safetyUser.setUsername(originalUser.getUsername());
        safetyUser.setUserAccount(originalUser.getUserAccount());
        safetyUser.setAvatarUrl(originalUser.getAvatarUrl());
        safetyUser.setGender(originalUser.getGender());
        safetyUser.setPhone(originalUser.getPhone());
        safetyUser.setEmail(originalUser.getEmail());
        safetyUser.setUserRole(originalUser.getUserRole());
        safetyUser.setPlanetCode(originalUser.getPlanetCode());
        safetyUser.setUserStatus(originalUser.getUserStatus());
        safetyUser.setCreateTime(originalUser.getCreateTime());

        return safetyUser;
    }
}




