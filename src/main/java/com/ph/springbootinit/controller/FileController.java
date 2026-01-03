package com.ph.springbootinit.controller;

import com.ph.springbootinit.annotation.AuthCheck;
import com.ph.springbootinit.common.BaseResponse;
import com.ph.springbootinit.common.ErrorCode;
import com.ph.springbootinit.common.ResultUtils;
import com.ph.springbootinit.exception.BusinessException;
import com.ph.springbootinit.model.entity.UserPo;
import com.ph.springbootinit.model.vo.FileUploadVo;
import com.ph.springbootinit.service.QiniuService;
import com.ph.springbootinit.service.UserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 *
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private static final long FIVE_MB = 5 * 1024 * 1024L;

    @Resource
    private QiniuService qiniuService;

    @Resource
    private UserService userService;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param request HTTP请求
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    @AuthCheck
    public BaseResponse<FileUploadVo> uploadFile(@RequestPart("file") MultipartFile file,
            HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        UserPo loginUserPo = userService.getLoginUser(request);

        try {
            String fileUrl = qiniuService.uploadFile(file);

            FileUploadVo fileUploadVo = new FileUploadVo();
            fileUploadVo.setFileUrl(fileUrl);
            fileUploadVo.setFileName(file.getOriginalFilename());
            fileUploadVo.setFileSize(file.getSize());
            fileUploadVo.setFileType(file.getContentType());
            fileUploadVo.setUploadTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            log.info("用户 {} 上传文件成功: {}", loginUserPo.getId(), fileUrl);
            return ResultUtils.success(fileUploadVo);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @param request HTTP请求
     * @return 文件访问URL
     */
    @PostMapping("/upload/avatar")
    @AuthCheck
    public BaseResponse<FileUploadVo> uploadAvatar(@RequestPart("file") MultipartFile file,
            HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只能上传图片文件");
        }

        // 验证文件大小
        if (file.getSize() > FIVE_MB) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过5MB");
        }

        UserPo loginUserPo = userService.getLoginUser(request);

        try {
            String fileUrl = qiniuService.uploadFile(file);

            FileUploadVo fileUploadVo = new FileUploadVo();
            fileUploadVo.setFileUrl(fileUrl);
            fileUploadVo.setFileName(file.getOriginalFilename());
            fileUploadVo.setFileSize(file.getSize());
            fileUploadVo.setFileType(file.getContentType());
            fileUploadVo.setUploadTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            log.info("用户 {} 上传头像成功: {}", loginUserPo.getId(), fileUrl);
            return ResultUtils.success(fileUploadVo, "头像上传成功");
        } catch (Exception e) {
            log.error("头像上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "头像上传失败");
        }
    }

    /**
     * 批量上传文件
     *
     * @param files 文件数组
     * @param request HTTP请求
     * @return 文件名和URL的映射
     */
    @PostMapping("/upload/batch")
    @AuthCheck
    public BaseResponse<Map<String, String>> uploadFiles(@RequestPart("files") MultipartFile[] files,
            HttpServletRequest request) {
        if (files == null || files.length == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        UserPo loginUserPo = userService.getLoginUser(request);

        try {
            Map<String, String> resultMap = new HashMap<>(16);
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String fileUrl = qiniuService.uploadFile(file);
                    resultMap.put(file.getOriginalFilename(), fileUrl);
                }
            }

            log.info("用户 {} 批量上传文件成功，共 {} 个文件", loginUserPo.getId(), resultMap.size());
            return ResultUtils.success(resultMap, "批量上传成功");
        } catch (Exception e) {
            log.error("批量上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "批量上传失败");
        }
    }

    /**
     * 获取上传凭证
     *
     * @param request HTTP请求
     * @return 上传凭证
     */
    @GetMapping("/upload-token")
    @AuthCheck
    public BaseResponse<String> getUploadToken(HttpServletRequest request) {
        UserPo loginUserPo = userService.getLoginUser(request);

        try {
            String uploadToken = qiniuService.getUploadToken();
            log.info("用户 {} 获取上传凭证成功", loginUserPo.getId());
            return ResultUtils.success(uploadToken, "获取上传凭证成功");
        } catch (Exception e) {
            log.error("获取上传凭证失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取上传凭证失败");
        }
    }
}
