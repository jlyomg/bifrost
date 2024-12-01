# ************************************************************
# Sequel Pro SQL dump
# Version 5446
#
# https://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 3.106.208.246 (MySQL 8.0.40-0ubuntu0.24.04.1)
# Database: admin
# Generation Time: 2024-12-01 11:02:24 AM +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table bifrost_dict_key
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_dict_key`;

CREATE TABLE `bifrost_dict_key` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `key` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '字典key',
  `tags` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '["system"]' COMMENT 'system-系统',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  `tenant_id` bigint unsigned NOT NULL DEFAULT '1' COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_key_tenant` (`key`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典key';



# Dump of table bifrost_dict_value
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_dict_value`;

CREATE TABLE `bifrost_dict_value` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典key',
  `value` varchar(10240) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典value',
  `sort_no` int DEFAULT '0' COMMENT '排序',
  `is_default` int unsigned NOT NULL DEFAULT '0' COMMENT '是否默认值',
  `attributes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '扩展字段',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  `tenant_id` bigint unsigned NOT NULL DEFAULT '1' COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_key_tenant` (`key`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典value';



# Dump of table bifrost_menu_function
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_menu_function`;

CREATE TABLE `bifrost_menu_function` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `permission_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限Code',
  `parent_id` bigint DEFAULT NULL COMMENT '上级菜单id',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '3' COMMENT '1-System,2-Group,3-Menu,4-Submenu;5-Function',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '菜单功能名称',
  `en_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '菜单功能英文名称',
  `sort_no` int DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '图标',
  `attributes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '扩展字段',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  `tenant_id` int unsigned DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_perm_cde_tenant` (`permission_code`,`tenant_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_name` (`name`),
  KEY `idx_en_name` (`en_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单功能表';



# Dump of table bifrost_operation_log
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_operation_log`;

CREATE TABLE `bifrost_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `menu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'Menu',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'Method',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作内容',
  `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'System' COMMENT '操作人',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  `tenant_id` int DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_operator` (`created_by`),
  KEY `idx_menu` (`menu`),
  KEY `idx_method` (`method`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作历史记录表';



# Dump of table bifrost_org
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_org`;

CREATE TABLE `bifrost_org` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `parent_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '上级机构编码',
  `org_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '机构唯一编码',
  `level` int NOT NULL DEFAULT '1' COMMENT '层级:1-企业,2-部门,3-小组',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '机构名称',
  `en_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '机构英文名称',
  `attributes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '扩展字段',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  `tenant_id` int unsigned NOT NULL DEFAULT '1' COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_org_tenant` (`org_code`,`tenant_id`),
  KEY `idx_parent_code` (`parent_code`),
  KEY `idx_name` (`name`),
  KEY `idx_en_name` (`en_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构表';

LOCK TABLES `bifrost_org` WRITE;
/*!40000 ALTER TABLE `bifrost_org` DISABLE KEYS */;

INSERT INTO `bifrost_org` (`id`, `parent_code`, `org_code`, `level`, `name`, `en_name`, `attributes`, `description`, `gmt_create`, `gmt_update`, `created_by`, `updated_by`, `is_deleted`, `tenant_id`)
VALUES
	(1,NULL,'Dataour',1,'Dataour','Dataour',NULL,NULL,'2024-11-15 14:41:40','2024-11-15 14:41:48','System','System',0,1);

/*!40000 ALTER TABLE `bifrost_org` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table bifrost_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_role`;

CREATE TABLE `bifrost_role` (
  `uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主键',
  `org_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '机构唯一编码',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '机构名称',
  `en_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '机构英文名称',
  `attributes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '扩展字段',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  `tenant_id` int unsigned NOT NULL DEFAULT '1' COMMENT '租户ID',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uni_org_code` (`org_code`),
  KEY `idx_name` (`name`),
  KEY `idx_en_name` (`en_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';



# Dump of table bifrost_role_perms
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_role_perms`;

CREATE TABLE `bifrost_role_perms` (
  `uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主键',
  `org_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '机构唯一编码',
  `role_uuid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '角色uuid',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `tenant_id` int NOT NULL DEFAULT '1' COMMENT '租户ID',
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `uni_org_code` (`org_code`),
  KEY `idx_en_name` (`role_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限表';



# Dump of table bifrost_system_config
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_system_config`;

CREATE TABLE `bifrost_system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `config_key` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '配置项',
  `config_value` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '配置值',
  `data_type` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'String' COMMENT 'String、Boolean、Json',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  `tenant_id` int unsigned NOT NULL DEFAULT '1' COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_config_key_tenant` (`config_key`,`tenant_id`),
  KEY `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统参数配置表';



# Dump of table bifrost_tenant
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_tenant`;

CREATE TABLE `bifrost_tenant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `tenant_id` int unsigned NOT NULL COMMENT '租户ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '租户名称',
  `en_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '租户英文名称',
  `attributes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '扩展字段',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_tenant_id` (`tenant_id`),
  KEY `idx_name` (`name`),
  KEY `idx_en_name` (`en_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户表';



# Dump of table bifrost_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bifrost_user`;

CREATE TABLE `bifrost_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录账号(建议用邮箱地址)',
  `pwd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名称',
  `en_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户英文名称',
  `org_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '机构号',
  `role_uuids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色 [uuid1,uuid2]',
  `avatar` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '头像',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号码',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱地址',
  `gender` int DEFAULT NULL COMMENT '性别 0:未知;1:男;2:女',
  `app_code` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拥有应用:[app1,app2]',
  `status` int NOT NULL DEFAULT '0' COMMENT '用户状态:0-正常;1-冻结',
  `exp_time` datetime DEFAULT NULL COMMENT '过期时间',
  `lang` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '语言:EN,CN',
  `theme` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主题',
  `layout` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '布局',
  `pwd_salt` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '密码盐值(偏移量)',
  `pwd_updated_time` timestamp NULL DEFAULT NULL COMMENT '密码修改时间',
  `first_login_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '第一次登陆时间',
  `last_login_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '最后一次登陆时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '描述',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '创建用户',
  `updated_by` varchar(128) NOT NULL DEFAULT 'System' COMMENT '更新用户',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除： 1是,0否',
  `tenant_id` int unsigned NOT NULL DEFAULT '1' COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account` (`account`) USING BTREE,
  KEY `idx_name` (`name`),
  KEY `idx_en_name` (`en_name`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`),
  KEY `idx_exp_time` (`exp_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
