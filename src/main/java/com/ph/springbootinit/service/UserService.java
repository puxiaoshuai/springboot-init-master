package com.ph.springbootinit.service;

import com.ph.springbootinit.model.ao.user.UserQueryAO;
import com.ph.springbootinit.model.entity.UserPo;
import com.ph.springbootinit.model.vo.LoginUserVO;
import com.ph.springbootinit.model.vo.UserVO;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * 用户服务
 *
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户登录（微信开放平台）
     *
     * @param wxOAuth2UserInfo 从微信获取的用户信息
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    UserPo getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    UserPo getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param userPo
     * @return
     */
    boolean isAdmin(UserPo userPo);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(UserPo userPo);

    /**
     * 获取脱敏的用户信息
     *
     * @param userPo
     * @return
     */
    UserVO getUserVO(UserPo userPo);

    /**
     * 获取脱敏的用户信息
     *
     * @param userPoList
     * @return
     */
    List<UserVO> getUserVO(List<UserPo> userPoList);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserPo getById(Long id);

    /**
     * 根据查询条件获取用户列表
     *
     * @param userQueryAO 查询条件
     * @return 用户列表
     */
    List<UserPo> list(UserQueryAO userQueryAO);

    /**
     * 统计用户数量
     *
     * @param userQueryAO 查询条件
     * @return 用户数量
     */
    long count(UserQueryAO userQueryAO);

    /**
     * 更新用户
     *
     * @param userPo 用户信息
     * @return 是否成功
     */
    boolean updateById(UserPo userPo);

    /**
     * 保存用户
     *
     * @param userPo 用户信息
     * @return 是否成功
     */
    boolean save(UserPo userPo);

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 是否成功
     */
    boolean removeById(Long id);
}