package com.ph.springbootinit.service.impl;

import static com.ph.springbootinit.constant.UserConstant.USER_LOGIN_STATE;

import cn.hutool.core.collection.CollUtil;
import com.ph.springbootinit.common.ErrorCode;
import com.ph.springbootinit.exception.BusinessException;
import com.ph.springbootinit.mapper.UserMapper;
import com.ph.springbootinit.model.ao.user.UserQueryAO;
import com.ph.springbootinit.model.entity.UserPo;
import com.ph.springbootinit.model.enums.UserRoleEnum;
import com.ph.springbootinit.model.vo.LoginUserVO;
import com.ph.springbootinit.model.vo.UserVO;
import com.ph.springbootinit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现
 *
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "mysalt";

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            UserQueryAO queryAO = new UserQueryAO();
            queryAO.setUserAccount(userAccount);
            long count = userMapper.selectCount(queryAO);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            UserPo userPo = new UserPo();
            userPo.setUserAccount(userAccount);
            userPo.setUserPassword(encryptPassword);
            userPo.setUserRole(UserRoleEnum.USER.getValue());
            userPo.setIsDelete(0);

            long currentTime = System.currentTimeMillis();
            userPo.setCreateTime(currentTime);
            userPo.setUpdateTime(currentTime);

            int result = userMapper.insert(userPo);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return userPo.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        UserQueryAO queryAO = new UserQueryAO();
        queryAO.setUserAccount(userAccount);
        queryAO.setUserPassword(encryptPassword);
        UserPo userPo = userMapper.selectOne(queryAO);
        // 用户不存在
        if (userPo == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userPo);
        return this.getLoginUserVO(userPo);
    }

    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            UserQueryAO queryAO = new UserQueryAO();
            queryAO.setUnionId(unionId);
            UserPo userPo = userMapper.selectOne(queryAO);
            // 被封号，禁止登录
            if (userPo != null && UserRoleEnum.BAN.getValue().equals(userPo.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (userPo == null) {
                userPo = new UserPo();
                userPo.setUnionId(unionId);
                userPo.setMpOpenId(mpOpenId);
                userPo.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                userPo.setUserName(wxOAuth2UserInfo.getNickname());
                userPo.setUserRole(UserRoleEnum.USER.getValue());
                userPo.setIsDelete(0);

                long currentTime = System.currentTimeMillis();
                userPo.setCreateTime(currentTime);
                userPo.setUpdateTime(currentTime);

                int result = userMapper.insert(userPo);
                if (result <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, userPo);
            return getLoginUserVO(userPo);
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public UserPo getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserPo currentUserPo = (UserPo) userObj;
        if (currentUserPo == null || currentUserPo.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUserPo.getId();
        currentUserPo = userMapper.selectById(userId);
        if (currentUserPo == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUserPo;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public UserPo getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserPo currentUserPo = (UserPo) userObj;
        if (currentUserPo == null || currentUserPo.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUserPo.getId();
        return userMapper.selectById(userId);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserPo userPo = (UserPo) userObj;
        return isAdmin(userPo);
    }

    @Override
    public boolean isAdmin(UserPo userPo) {
        return userPo != null && UserRoleEnum.ADMIN.getValue().equals(userPo.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(UserPo userPo) {
        if (userPo == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(userPo, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(UserPo userPo) {
        if (userPo == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userPo, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<UserPo> userPoList) {
        if (CollUtil.isEmpty(userPoList)) {
            return new ArrayList<>();
        }
        return userPoList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public UserPo getById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return userMapper.selectById(id);
    }

    @Override
    public List<UserPo> list(UserQueryAO userQueryAO) {
        if (userQueryAO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        return userMapper.selectList(userQueryAO);
    }

    @Override
    public long count(UserQueryAO userQueryAO) {
        if (userQueryAO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        return userMapper.selectCount(userQueryAO);
    }

    @Override
    public boolean updateById(UserPo userPo) {
        if (userPo == null || userPo.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        userPo.setUpdateTime(System.currentTimeMillis());
        int result = userMapper.updateById(userPo);
        return result > 0;
    }

    @Override
    public boolean save(UserPo userPo) {
        if (userPo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userPo.getIsDelete() == null) {
            userPo.setIsDelete(0);
        }
        long currentTime = System.currentTimeMillis();
        userPo.setCreateTime(currentTime);
        userPo.setUpdateTime(currentTime);
        int result = userMapper.insert(userPo);
        return result > 0;
    }

    @Override
    public boolean removeById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        int result = userMapper.deleteById(id);
        return result > 0;
    }
}