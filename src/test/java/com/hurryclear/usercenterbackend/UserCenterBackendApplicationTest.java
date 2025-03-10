package com.hurryclear.usercenterbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserCenterBackendApplicationTest {

    @Test
    void testEncrypt() throws NoSuchAlgorithmException {
        String password = DigestUtils.md5DigestAsHex(("abcd" + "hur").getBytes());
        System.out.println(password);
    }

}