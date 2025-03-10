package com.hurryclear.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hurryclear.usercenterbackend.model.domain.User;
import com.hurryclear.usercenterbackend.service.UserService;
import com.hurryclear.usercenterbackend.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author hurjiang
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-03-10 14:58:01
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. check not null --> using StringUtils.isAnyBlank from Apache Commons Lang
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }

        // 2. check userAccount not shorter than 4 digits
        if (userAccount.length() < 4 ) {
            return -1;
        }
        // 3. check userPassword not shorter than 8 digits
        if (userPassword.length() < 8) {
            return -1;
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
            return -1;
        }
//        if (userRepository.existsByUserAccount(userAccount)) {
//            return -1;
//        }

        // 7. encrypt password --> md5
        final String SALT = "hurryclear";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 8. save new user in database
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (! saveResult) {
            return -1;
        }

        return user.getId();
    }
}




