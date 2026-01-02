package com.ph.springbootinit.mapper;

import com.ph.springbootinit.model.ao.user.UserQueryAO;
import com.ph.springbootinit.model.entity.UserPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据库操作
 *
 */
public interface UserMapper {

    /**
     * 插入用户
     *
     * @param userPo 用户信息
     * @return 影响行数
     */
    int insert(UserPo userPo);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserPo selectById(@Param("id") Long id);

    /**
     * 根据条件查询单个用户
     *
     * @param queryAO 查询条件
     * @return 用户信息
     */
    UserPo selectOne(UserQueryAO queryAO);

    /**
     * 根据条件查询用户数量
     *
     * @param queryAO 查询条件
     * @return 用户数量
     */
    int selectCount(UserQueryAO queryAO);

    /**
     * 根据ID更新用户
     *
     * @param userPo 用户信息
     * @return 影响行数
     */
    int updateById(UserPo userPo);

    /**
     * 根据条件查询用户列表
     *
     * @param queryAO 查询条件
     * @return 用户列表
     */
    List<UserPo> selectList(UserQueryAO queryAO);

    /**
     * 根据ID删除用户（逻辑删除）
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
