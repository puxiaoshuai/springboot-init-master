package com.ph.springbootinit.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置
 *
 */
@Configuration
@MapperScan("com.ph.springbootinit.mapper")
public class MyBatisConfig {
}
