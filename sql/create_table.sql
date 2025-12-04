# 数据库初始化



-- 创建库
create database if not exists initboot;

-- 切换库
use initboot;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '用户ID' PRIMARY KEY,
                                    userAccount VARCHAR(191) NOT NULL COMMENT '账号',
                                    userPassword VARCHAR(255) NOT NULL COMMENT '密码哈希值',
                                    unionId VARCHAR(128) NULL COMMENT '微信开放平台ID',
                                    mpOpenId VARCHAR(128) NULL COMMENT '公众号OpenID',
                                    userName VARCHAR(100) NULL COMMENT '用户昵称',
                                    userAvatar VARCHAR(500) NULL COMMENT '用户头像URL',
                                    userProfile VARCHAR(300) NULL COMMENT '用户简介',
                                    userRole ENUM('user', 'admin', 'ban') DEFAULT 'user' NOT NULL COMMENT '用户角色',
                                    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
                                    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
                                    isDelete TINYINT DEFAULT 0 NOT NULL COMMENT '软删除标记：0-正常，1-删除',

                                    UNIQUE KEY uk_userAccount (userAccount),
                                    UNIQUE KEY uk_unionId (unionId),
                                    UNIQUE KEY uk_mpOpenId (mpOpenId),
                                    INDEX idx_userRole (userRole),
                                    INDEX idx_createTime (createTime),
                                    INDEX idx_isDelete (isDelete)
) COMMENT='用户表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';
