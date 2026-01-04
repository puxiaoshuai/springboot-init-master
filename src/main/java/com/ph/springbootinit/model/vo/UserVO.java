package com.ph.springbootinit.model.vo;

import com.ph.springbootinit.serializer.LongSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *


 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    @JsonSerialize(using = LongSerializer.class)
    private Long createTime;

    @JsonSerialize(using = LongSerializer.class)
    private Long updateTime;

    private static final long serialVersionUID = 1L;
}