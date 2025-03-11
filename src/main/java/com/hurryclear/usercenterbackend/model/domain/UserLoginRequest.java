package com.hurryclear.usercenterbackend.model.domain;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -8266575285837957904L;

    private String userAccount;
    private String userPassword;
}
