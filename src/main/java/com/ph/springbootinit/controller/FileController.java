package com.ph.springbootinit.controller;

import com.ph.springbootinit.annotation.AuthCheck;
import com.ph.springbootinit.common.BaseResponse;
import com.ph.springbootinit.common.ResultUtils;
import com.ph.springbootinit.constant.UserConstant;
import com.ph.springbootinit.model.vo.FileUploadVo;
import com.ph.springbootinit.service.QiniuService;
import com.qiniu.util.Auth;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private QiniuService qiniuService;
    /**
     * 上传文件
     *
     * @param file 要上传的文件
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public BaseResponse<FileUploadVo> uploadFile( @RequestParam("file") MultipartFile file) {

        String fileUrl = qiniuService.uploadFile(file);

        FileUploadVo fileUploadVo = new FileUploadVo(
                fileUrl,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        );

        return ResultUtils.success(fileUploadVo, "文件上传成功");
    }

    /**
     * 获取上传凭证（用于前端直传）
     *
     * @return 上传凭证
     */
    @GetMapping("/upload-token")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)

    public BaseResponse<String> getUploadToken() {
        String uploadToken = qiniuService.getUploadToken();
        return ResultUtils.success(uploadToken, "获取上传凭证成功");
    }

    /**
     * 批量上传文件
     *
     * @param files 要上传的文件列表
     * @return 文件访问URL列表
     */
    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)

    public BaseResponse<Map<String, String>> uploadFiles(
           @RequestParam("files") MultipartFile[] files) {

        log.info("开始批量上传文件，文件数量: {}", files.length);

        Map<String, String> fileUrls = new HashMap<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String fileUrl = qiniuService.uploadFile(file);
                fileUrls.put(file.getOriginalFilename(), fileUrl);
            }
        }

        log.info("批量上传完成，成功上传 {} 个文件", fileUrls.size());

        return ResultUtils.success(fileUrls, "批量上传成功");
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像访问URL
     */
    @PostMapping("/upload/avatar")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)

    public BaseResponse<FileUploadVo> uploadAvatar(
           @RequestParam("file") MultipartFile file) {

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResultUtils.error(400, "文件类型错误,只能上传图片文件");
        }

        // 检查文件大小（限制为5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResultUtils.error(400, "文件大小超限,文件大小不能超过5MB");
        }

        log.info("开始上传头像: {}", file.getOriginalFilename());

        String avatarUrl = qiniuService.uploadFile(file);

        FileUploadVo fileUploadVo = new FileUploadVo(
                avatarUrl,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        );

        log.info("头像上传成功: {}", avatarUrl);

        return ResultUtils.success(fileUploadVo, "头像上传成功");
    }
}