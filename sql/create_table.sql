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
                                    createTime BIGINT NOT NULL COMMENT '创建时间（时间戳）',
                                    updateTime BIGINT NOT NULL COMMENT '更新时间（时间戳）',
                                    isDelete TINYINT DEFAULT 0 NOT NULL COMMENT '软删除标记：0-正常，1-删除',

                                    UNIQUE KEY uk_userAccount (userAccount),
                                    UNIQUE KEY uk_unionId (unionId),
                                    UNIQUE KEY uk_mpOpenId (mpOpenId),
                                    INDEX idx_userRole (userRole),
                                    INDEX idx_createTime (createTime),
                                    INDEX idx_isDelete (isDelete)
) COMMENT='用户表' ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
