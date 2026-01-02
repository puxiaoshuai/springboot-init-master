package com.ph.springbootinit.model.ao.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询应用对象
 * 合并了 UserQueryRequest 和原 UserQueryAO
 *
 */
@Data
public class UserQueryAO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码（仅用于登录查询）
     */
    private String userPassword;

    /**
     * 微信开放平台ID
     */
    private String unionId;

    /**
     * 公众号OpenID
     */
    private String mpOpenId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 校验密码（仅用于注册）
     */
    private String checkPassword;
}
