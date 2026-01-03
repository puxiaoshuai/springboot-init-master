package com.ph.springbootinit.service;

import com.google.gson.Gson;
import com.ph.springbootinit.config.QiniuProperties;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import java.io.InputStream;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 七牛云文件上传服务
 *
 */
@Service
@Slf4j
public class QiniuService {

    @Resource
    private QiniuProperties qiniuProperties;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = generateFileName(fileExtension);

            return uploadByStream(inputStream, fileName);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传字节数组
     *
     * @param bytes 字节数组
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String uploadBytes(byte[] bytes, String fileName) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("文件内容不能为空");
        }

        try {
            return uploadByBytes(bytes, fileName);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取上传凭证
     *
     * @return 上传凭证
     */
    public String getUploadToken() {
        return Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey())
                .uploadToken(qiniuProperties.getBucketName());
    }

    /**
     * 通过流上传文件
     *
     * @param inputStream 输入流
     * @param fileName 文件名
     * @return 文件访问URL
     */
    private String uploadByStream(InputStream inputStream, String fileName) {
        try {
            // 构造配置类
            Configuration cfg = new Configuration(Region.autoRegion());
            // 创建上传管理器
            UploadManager uploadManager = new UploadManager(cfg);

            // 获取上传凭证
            String upToken = getUploadToken();

            // 上传文件
            Response response = uploadManager.put(inputStream, fileName, upToken, null, null);
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            return getFileUrl(putRet.key);
        } catch (QiniuException e) {
            log.error("七牛云上传失败", e);
            throw new RuntimeException("七牛云上传失败: " + e.getMessage());
        }
    }

    /**
     * 通过字节数组上传文件
     *
     * @param bytes 字节数组
     * @param fileName 文件名
     * @return 文件访问URL
     */
    private String uploadByBytes(byte[] bytes, String fileName) {
        try {
            // 构造配置类
            Configuration cfg = new Configuration(Region.autoRegion());
            // 创建上传管理器
            UploadManager uploadManager = new UploadManager(cfg);

            // 获取上传凭证
            String upToken = getUploadToken();

            // 上传文件
            Response response = uploadManager.put(bytes, fileName, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            return getFileUrl(putRet.key);
        } catch (QiniuException e) {
            log.error("七牛云上传失败", e);
            throw new RuntimeException("七牛云上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param key 文件key
     * @return 文件访问URL
     */
    private String getFileUrl(String key) {
        return qiniuProperties.getDomain() + "/" + key;
    }

    /**
     * 生成唯一文件名
     *
     * @param extension 文件扩展名
     * @return 唯一文件名
     */
    private String generateFileName(String extension) {
        return System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8) + extension;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}
