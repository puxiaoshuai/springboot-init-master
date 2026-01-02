package com.ph.springbootinit.model.ao.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新应用对象
 *
 */
@Data
public class UserUpdateAO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
}
