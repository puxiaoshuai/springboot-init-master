package com.ph.springbootinit.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传返回信息VO类
 *
 */
@Data
public class FileUploadVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 上传时间
     */
    private String uploadTime;
}
