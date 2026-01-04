package com.ph.springbootinit.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传返回信息VO类
 *
 */
import java.util.Date;

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
    private Date uploadTime;

    public FileUploadVo(String fileUrl, String fileName, Long fileSize, String fileType) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.uploadTime = new Date();
    }


}
