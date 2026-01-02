# CLAUDE.md
## 项目概述

这是一个**企业级Spring Boot项目初始模板**，整合了现代Web开发的主流技术栈，适合快速搭建内容管理系统和社交平台应用。

### 技术栈
- **核心框架**: Spring Boot 2.7.2 + MyBatis Plus 3.5.2
- **数据存储**: MySQL + Redis + Elasticsearch
- **第三方集成**: 腾讯云COS + 微信开发平台 (wx-java-mp-spring-boot-starter 4.4.0)
- **开发工具**: Knife4j 4.4.0 API文档 + Freemarker代码生成器
- **工具库**: Hutool 5.8.8 + EasyExcel 3.1.1 + Apache Commons Lang3

### 目录结构
```
src/main/java/com/ph/springbootinit/
├── annotation/          # 自定义注解（权限检查、日志）
├── aop/                # AOP切面实现
├── common/             # 通用类（响应、分页、异常）
├── config/             # 配置类（跨域、JSON、MyBatis等）
├── constant/           # 常量定义
├── controller/         # REST控制器
├── exception/          # 异常处理
├── generate/           # 代码生成器
├── job/                # 定时任务（ES同步）
├── manager/            # 业务管理层
├── mapper/             # MyBatis映射器
├── model/              # 数据模型
│   ├── dto/           # 数据传输对象
│   ├── entity/        # 实体类
│   ├── enums/         # 枚举类
│   └── vo/            # 视图对象
├── service/            # 服务层
├── utils/              # 工具类
└── wxmp/               # 微信公众号相关
```

### 核心设计模式

1. **分层架构**: Controller → Service → Manager → Mapper
2. **AOP切面**:
   - `@AuthCheck`: 方法级权限检查，配合 `mustRole` 参数指定必需角色
   - `LogInterceptor`: 自动记录请求日志
   - `AuthInterceptor`: 全局权限拦截器
3. **统一异常处理**: GlobalExceptionHandler + BusinessException
4. **统一响应格式**: BaseResponse + ResultUtils
5. **数据传输对象**: DTO/VO分离，避免直接暴露实体


### 添加新功能

1. **后端**: 创建 entity->AO → DTO → VO → Mapper → Service → Controller
2. **前端**: 运行 `npm run openapi` 重新生成 API 客户端
3. **前端**: 使用 ProComponents 创建页面组件
4. **添加路由** 在 `config/routes.ts` 中配置相应的访问控制



