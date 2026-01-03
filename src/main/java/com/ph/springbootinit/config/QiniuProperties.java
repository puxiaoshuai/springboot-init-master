package com.ph.springbootinit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 七牛云配置属性类
 *
 */
@Component
@ConfigurationProperties(prefix = "oss.qiniu")
@Data
public class QiniuProperties {

    /**
     * 域名
     */
    private String domain;

    /**
     * AccessKey
     */
    private String accessKey;

    /**
     * SecretKey
     */
    private String secretKey;

    /**
     * 存储空间名称
     */
    private String bucketName;
}
