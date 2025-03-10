package com.hurryclear.usercenterbackend.service;
import java.util.Date;

import com.hurryclear.usercenterbackend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {

        User user = new User();

        user.setUsername("hurryclear");
        user.setUserAccount("123");
        user.setAvatarUrl("https://avatars.githubusercontent.com/u/115779170?v=4");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("124");
        System.out.println(user.getId());
        boolean result = userService.save(user);

        Assertions.assertTrue(result);
    }
}