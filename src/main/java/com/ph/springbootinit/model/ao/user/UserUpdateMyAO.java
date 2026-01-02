package com.ph.springbootinit.model.ao.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新个人信息应用对象
 *
 */
@Data
public class UserUpdateMyAO implements Serializable {

    private static final long serialVersionUID = 1L;

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
}
