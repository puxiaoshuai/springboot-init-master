package com.ph.springbootinit.model.ao.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户创建应用对象
 *
 */
@Data
public class UserAddAO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色: user, admin
     */
    private String userRole;
}
