package com.hurryclear.usercenterbackend.model.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -8266575285837957904L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
