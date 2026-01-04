package com.ph.springbootinit.service;

import com.ph.springbootinit.common.ErrorCode;
import com.ph.springbootinit.config.QiniuProperties;
import com.ph.springbootinit.exception.BusinessException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 七牛云文件上传服务
 */
@Slf4j
@Service
public class QiniuService {

    @Autowired
    private QiniuProperties qiniuProperties;


    /**
     * 上传文件到七牛云
     *
     * @param file 要上传的文件
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        // 检查配置是否正确
        if (qiniuProperties.getAccessKey() == null || qiniuProperties.getSecretKey() == null) {
            log.error("七牛云配置错误: AccessKey 或 SecretKey 为空");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "七牛云配置错误");
        }

        try {
            // 获取文件字节数组
            byte[] fileData = file.getBytes();
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();

            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;

            // 构造一个带指定 Region 对象的配置类
            Configuration cfg = new Configuration(Region.autoRegion());

            // 创建上传管理器
            UploadManager uploadManager = new UploadManager(cfg);

            // 生成上传凭证
            Auth auth = Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
            String upToken = auth.uploadToken(qiniuProperties.getBucketName());
            // 上传文件
            Response response = uploadManager.put(fileData, fileName, upToken);

            if (response.isOK()) {
                String fileUrl = qiniuProperties.getDomain() + "/" + fileName;
                return fileUrl;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "七牛云相应失败");
            }

        } catch (QiniuException e) {
            log.error("七牛云上传异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败: " + e.getMessage());
        } catch (IOException e) {
            log.error("文件读取异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件读取失败");
        }
    }

    /**
     * 上传字节数组到七牛云
     *
     * @param fileData 文件字节数组
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadFile(byte[] fileData, String fileName) {
        if (fileData == null || fileData.length == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件数据不能为空");
        }

        try {
            // 构造一个带指定 Region 对象的配置类
            Configuration cfg = new Configuration(Region.autoRegion());

            // 创建上传管理器
            UploadManager uploadManager = new UploadManager(cfg);

            // 生成上传凭证
            Auth auth = Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
            String upToken = auth.uploadToken(qiniuProperties.getBucketName());

            log.info("开始上传文件: {}", fileName);

            // 上传文件
            Response response = uploadManager.put(fileData, fileName, upToken);

            if (response.isOK()) {
                String fileUrl = qiniuProperties.getDomain() + "/" + fileName;
                log.info("文件上传成功: {}", fileUrl);
                return fileUrl;
            } else {
                log.error("文件上传失败: {}", response.error);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
            }

        } catch (QiniuException e) {
            log.error("七牛云上传异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取上传凭证（用于前端直传）
     *
     * @return 上传凭证
     */
    public String getUploadToken() {
        Auth auth = Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
        return auth.uploadToken(qiniuProperties.getBucketName());
    }
}
