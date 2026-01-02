package com.ph.springbootinit.model.ao.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户注册应用对象
 *
 */
@Data
public class UserRegisterAO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
