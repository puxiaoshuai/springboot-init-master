package com.ph.springbootinit.model.ao.file;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传应用对象
 *
 */
@Data
public class UploadFileAO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务类型
     */
    private String biz;
}
