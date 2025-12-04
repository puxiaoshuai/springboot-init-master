# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个**企业级Spring Boot项目初始模板**，整合了现代Web开发的主流技术栈，适合快速搭建内容管理系统和社交平台应用。

### 技术栈
- **核心框架**: Spring Boot 2.7.2 + MyBatis Plus 3.5.2
- **数据存储**: MySQL + Redis + Elasticsearch
- **第三方集成**: 腾讯云COS + 微信开发平台
- **开发工具**: Knife4j API文档 + 代码生成器

## 快速启动

```bash
# 1. 配置数据库
# 修改 application.yml 中的数据库连接信息

# 2. 执行SQL脚本
mysql -u root -p < sql/create_table.sql

# 3. 启动项目
mvn spring-boot:run

# 4. 访问API文档
# http://localhost:8101/api/doc.html
```

## 常用命令

### 开发命令
```bash
# 本地开发启动
mvn spring-boot:run

# 打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 运行测试
mvn test

# 运行单个测试类
mvn test -Dtest=UserServiceTest

# 代码格式检查
mvn checkstyle:check
```

### 数据库相关
```bash
# 重置数据库
mysql -u root -p < sql/create_table.sql

# 查看数据库表结构
desc user;
desc post;
```

## 项目架构

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
2. **AOP切面**: 权限检查(@AuthCheck)和操作日志记录
3. **统一异常处理**: GlobalExceptionHandler + BusinessException
4. **统一响应格式**: BaseResponse + ResultUtils
5. **数据传输对象**: DTO/VO分离，避免直接暴露实体

### 主要功能模块

#### 1. 用户管理系统
- **注册/登录**: 支持账号密码和微信登录
- **权限控制**: 基于角色的权限管理
- **用户信息**: 完整的用户CRUD操作

#### 2. 内容管理系统
- **帖子管理**: 帖子的增删改查，支持富文本
- **点赞收藏**: 用户对帖子的互动功能
- **全文搜索**: 基于Elasticsearch的搜索功能

#### 3. 文件管理系统
- **对象存储**: 腾讯云COS集成
- **多业务支持**: 头像、帖子图片等不同业务场景
- **文件验证**: 文件类型和大小限制

#### 4. 微信集成
- **公众号**: 消息处理和菜单管理
- **开放平台**: 微信登录集成

## 关键配置

### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/springboot_init
    username: root
    password: # todo: 配置数据库密码
```

### Redis配置
```yaml
  redis:
    host: localhost
    port: 6379
    password: # todo: 配置Redis密码
```

### 腾讯云COS配置
```yaml
cos:
  client:
    accessKey: # todo: 配置腾讯云访问密钥
    secretKey: # todo: 配置腾讯云密钥
    region: # todo: 配置地域
    bucket: # todo: 配置存储桶
```

## 开发工作流

### 1. 新增功能模块
1. 在`model/entity/`创建实体类
2. 在`model/dto/`创建DTO类
3. 在`model/vo/`创建VO类
4. 在`mapper/`创建Mapper接口和XML
5. 在`service/`创建Service接口和实现
6. 在`controller/`创建控制器
7. 编写单元测试

### 2. 使用代码生成器
```java
// 修改 CodeGenerator.java 中的表名和配置
// 运行 main 方法自动生成CRUD代码
```

### 3. API开发规范
- 使用`@PostMapping`进行增删改操作
- 使用`@GetMapping`进行查询操作
- 统一返回`BaseResponse<T>`格式
- 使用`@AuthCheck`进行权限验证
- 参数使用`@RequestBody`接收JSON数据

### 4. 异常处理
```java
// 抛出业务异常
ThrowUtils.throwIf(condition, ErrorCode.PARAMS_ERROR);

// 自定义异常信息
throw new BusinessException(ErrorCode.SYSTEM_ERROR, "自定义错误信息");
```

## 测试策略

### 单元测试
- 所有Service层方法需要单元测试
- 使用`@SpringBootTest`进行集成测试
- 测试数据使用`@TestPropertySource`指定测试配置

### API测试
- 访问 http://localhost:8101/api/doc.html 进行API测试
- 使用Knife4j提供的在线测试功能

## 部署说明

### 本地部署
```bash
# 1. 打包
mvn clean package

# 2. 运行
java -jar target/springboot-init-0.0.1-SNAPSHOT.jar
```

### Docker部署
```dockerfile
# 项目根目录已包含Dockerfile
docker build -t springboot-init .
docker run -p 8101:8101 springboot-init
```

## 注意事项

1. **配置项**: 所有`# todo`标记的配置项都需要根据实际环境配置
2. **数据库**: 确保MySQL版本 >= 8.0
3. **Redis**: 建议使用Redis 6.0+
4. **Elasticsearch**: 搜索功能需要ES 7.x支持
5. **跨域**: 已配置CORS支持，开发时无需额外处理
6. **API文档**: 启动后访问`/api/doc.html`查看完整API文档

## 扩展建议

1. **缓存优化**: 可添加Redis缓存层提高性能
2. **监控集成**: 可集成Spring Boot Actuator进行监控
3. **日志管理**: 可集成ELK进行日志分析
4. **消息队列**: 可集成RabbitMQ处理异步任务
5. **多环境**: 可配置dev/test/prod多环境支持