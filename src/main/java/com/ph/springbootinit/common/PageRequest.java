package com.ph.springbootinit.common;

import com.ph.springbootinit.constant.CommonConstant;
import lombok.Data;

/**
 * 分页请求
 *


 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

}
