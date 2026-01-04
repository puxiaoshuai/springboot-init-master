package com.ph.springbootinit.converter;

import com.ph.springbootinit.model.entity.UserPo;
import com.ph.springbootinit.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户对象转换器
 *
 */
@Component
public class UserConverter {

    /**
     * UserPo 转 UserVO
     *
     * @param userPo 用户实体
     * @return 用户视图对象
     */
    public UserVO toVo(UserPo userPo) {
        if (userPo == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userPo, userVO);
        return userVO;
    }

    /**
     * UserPo 列表转 UserVO 列表
     *
     * @param userPoList 用户实体列表
     * @return 用户视图对象列表
     */
    public List<UserVO> toVoList(List<UserPo> userPoList) {
        if (userPoList == null || userPoList.isEmpty()) {
            return Collections.emptyList();
        }
        return userPoList.stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }
}
