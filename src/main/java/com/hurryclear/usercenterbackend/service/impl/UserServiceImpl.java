package com.hurryclear.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hurryclear.usercenterbackend.model.domain.User;
import com.hurryclear.usercenterbackend.service.UserService;
import com.hurryclear.usercenterbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author hurjiang
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-03-10 14:58:01
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




