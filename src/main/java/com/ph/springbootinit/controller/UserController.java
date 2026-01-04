package com.ph.springbootinit.controller;

import com.ph.springbootinit.annotation.AuthCheck;
import com.ph.springbootinit.common.BaseResponse;
import com.ph.springbootinit.common.DeleteRequest;
import com.ph.springbootinit.common.ErrorCode;
import com.ph.springbootinit.common.ResultUtils;
import com.ph.springbootinit.config.WxOpenConfig;
import com.ph.springbootinit.constant.UserConstant;
import com.ph.springbootinit.exception.BusinessException;
import com.ph.springbootinit.exception.ThrowUtils;
import com.ph.springbootinit.model.ao.user.UserAddAO;
import com.ph.springbootinit.model.ao.user.UserLoginAO;
import com.ph.springbootinit.model.ao.user.UserQueryAO;
import com.ph.springbootinit.model.ao.user.UserRegisterAO;
import com.ph.springbootinit.model.ao.user.UserUpdateAO;
import com.ph.springbootinit.model.ao.user.UserUpdateMyAO;
import com.ph.springbootinit.model.entity.UserPo;
import com.ph.springbootinit.model.vo.LoginUserVO;
import com.ph.springbootinit.model.vo.UserVO;
import com.ph.springbootinit.converter.UserConverter;
import com.ph.springbootinit.mapper.UserMapper;
import com.ph.springbootinit.service.UserService;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ph.springbootinit.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口
 *
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserConverter userConverter;

    @Resource
    private WxOpenConfig wxOpenConfig;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterAO
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterAO userRegisterAO) {
        if (userRegisterAO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterAO.getUserAccount();
        String userPassword = userRegisterAO.getUserPassword();
        String checkPassword = userRegisterAO.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginAO
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginAO userLoginAO, HttpServletRequest request) {
        if (userLoginAO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginAO.getUserAccount();
        String userPassword = userLoginAO.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户登录（微信开放平台）
     */
    @GetMapping("/login/wx_open")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ResultUtils.success(userService.userLoginByMpOpen(userInfo, request));
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        UserPo userPo = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(userPo));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddAO
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddAO userAddAO, HttpServletRequest request) {
        if (userAddAO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userAddAO.getUserAccount();
        // 校验账号不能为空
        if (StringUtils.isBlank(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能为空");
        }
        // 账户不能重复
        synchronized (userAccount.intern()) {
            UserQueryAO queryAO = new UserQueryAO();
            queryAO.setUserAccount(userAccount);
            long count = userMapper.selectCount(queryAO);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
            }
            UserPo userPo = new UserPo();
            BeanUtils.copyProperties(userAddAO, userPo);
            // 默认密码 12345678
            String defaultPassword = "12345678";
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
            userPo.setUserPassword(encryptPassword);
            boolean result = userService.save(userPo);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(userPo.getId());
        }
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateAO
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateAO userUpdateAO,
            HttpServletRequest request) {
        if (userUpdateAO == null || userUpdateAO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserPo userPo = new UserPo();
        BeanUtils.copyProperties(userUpdateAO, userPo);
        boolean result = userService.updateById(userPo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserPo> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserPo userPo = userService.getById(id);
        ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userPo);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<UserPo> response = getUserById(id, request);
        UserPo userPo = response.getData();
        return ResultUtils.success(userService.getUserVO(userPo));
    }

    /**
     * 获取用户列表（仅管理员）
     *
     * @param userQueryAO
     * @param request
     * @return
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserVO>> listUser(@RequestBody UserQueryAO userQueryAO,
                                               HttpServletRequest request) {
        List<UserPo> userPoList = userService.list(userQueryAO);
        List<UserVO> userVOList = userConverter.toVoList(userPoList);
        return ResultUtils.success(userVOList);
    }

    /**
     * 获取用户封装列表
     *
     * @param userQueryAO
     * @param request
     * @return
     */
    @PostMapping("/list/vo")
    public BaseResponse<List<UserVO>> listUserVO(@RequestBody UserQueryAO userQueryAO,
            HttpServletRequest request) {
        if (userQueryAO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<UserPo> userPoList = userService.list(userQueryAO);
        List<UserVO> userVOList = userService.getUserVO(userPoList);
        return ResultUtils.success(userVOList);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyAO
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyAO userUpdateMyAO,
            HttpServletRequest request) {
        if (userUpdateMyAO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserPo loginUserPo = userService.getLoginUser(request);
        UserPo userPo = new UserPo();
        BeanUtils.copyProperties(userUpdateMyAO, userPo);
        userPo.setId(loginUserPo.getId());
        boolean result = userService.updateById(userPo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
