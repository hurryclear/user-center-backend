package com.hurryclear.usercenterbackend.service;

import com.hurryclear.usercenterbackend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


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

    @Test
    void userRegister() {

        // not null
        String userAccount = "hurry";
        String userPassword = "";
        String checkPassword = "12345678";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        // userAccount not shorter than 4
        userAccount = "hu";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "1";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        // userPassword not shorter than 8
        userAccount = "hurry";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        // unique user account
//        userAccount = "hurryclear";
//        userPassword = "12345678";
//        checkPassword = "12345678";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, result);

        // no special character in account
        userAccount = "hu ry";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        // userPassword and checkPassword same
        userAccount = "hurry";
        userPassword = "12345678";
        checkPassword = "1234568";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        // save in database
        userAccount = "mercylin";
        userPassword = "123456789";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertTrue(result > 0);

    }
}