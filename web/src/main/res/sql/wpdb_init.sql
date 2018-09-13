/*
 Navicat Premium Data Transfer

 Source Server         : 海慧数据库
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : 140.143.10.128:53306
 Source Schema         : wpdb

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 13/09/2018 17:09:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for log_op
-- ----------------------------
DROP TABLE IF EXISTS `log_op`;
CREATE TABLE `log_op` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `op_name` varchar(50) DEFAULT NULL COMMENT '操作名',
  `op_channel` varchar(100) DEFAULT NULL COMMENT '渠道',
  `req_method` varchar(100) DEFAULT NULL COMMENT '访问路径',
  `req_ip` varchar(100) DEFAULT NULL COMMENT '来访ip',
  `req_at` datetime DEFAULT NULL COMMENT '创建时间',
  `req_param` text COMMENT '请求参数',
  `req_ret` text COMMENT '处理结果',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `req_at` (`req_at`) USING BTREE,
  KEY `op_name` (`op_name`) USING BTREE,
  KEY `op_channel` (`op_channel`) USING BTREE,
  KEY `req_method` (`req_method`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='操作日志记录表';

-- ----------------------------
-- Table structure for merchant_amount_log
-- ----------------------------
DROP TABLE IF EXISTS `merchant_amount_log`;
CREATE TABLE `merchant_amount_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merID` int(11) DEFAULT NULL COMMENT '商户ID',
  `merNo` varchar(255) DEFAULT NULL COMMENT '商户号',
  `merName` varchar(255) DEFAULT NULL COMMENT '商户名称',
  `amount` decimal(20,2) DEFAULT NULL COMMENT '变动金额',
  `tAmount` decimal(20,2) DEFAULT NULL COMMENT '变动后的金额',
  `amountType` varchar(1) DEFAULT NULL COMMENT '变动方式：1 ：提现',
  `cat` datetime DEFAULT NULL COMMENT '创建时间',
  `mat` datetime DEFAULT NULL COMMENT '修改时间',
  `dat` datetime DEFAULT NULL COMMENT '删除时间',
  `operID` varchar(255) DEFAULT NULL COMMENT '最后操作员ID',
  PRIMARY KEY (`id`),
  KEY `merchantNo_merchantName` (`merID`,`merNo`,`merName`,`amountType`,`cat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户表提现日志表';

-- ----------------------------
-- Table structure for merchant_info
-- ----------------------------
DROP TABLE IF EXISTS `merchant_info`;
CREATE TABLE `merchant_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchantNo` varchar(20) NOT NULL COMMENT '商户号',
  `merchantName` varchar(255) NOT NULL COMMENT '商户名称',
  `merchantType` varchar(3) NOT NULL COMMENT '商户类型（码表）',
  `perName` varchar(32) NOT NULL COMMENT '负责人姓名',
  `cardID` varchar(20) NOT NULL COMMENT '身份证号码',
  `mobile` varchar(20) NOT NULL COMMENT '负责人联系方式',
  `email` varchar(255) NOT NULL COMMENT '负责人EMail',
  `address` varchar(255) NOT NULL COMMENT '负责人联系地址',
  `mobile1` varchar(50) DEFAULT NULL COMMENT '商户识别码',
  `mobile2` varchar(50) DEFAULT NULL COMMENT '负责人联系方式2',
  `maxTradeAmount` decimal(10,2) DEFAULT NULL COMMENT '最大代扣金额',
  `feeAmount` decimal(20,2) DEFAULT '0.00' COMMENT '预付手续费余额',
  `feeCollectType` varchar(2) DEFAULT NULL COMMENT '手续费收取类型 0 实时按银行手续费加1 批量按银行手续费加0.5',
  `cardImg` varchar(255) DEFAULT NULL COMMENT '手持身份照片',
  `cardZ` varchar(255) DEFAULT NULL COMMENT '身份证正面',
  `cardF` varchar(255) DEFAULT NULL COMMENT '身份证反面',
  `status` varchar(2) NOT NULL COMMENT '商户状态：0正常 1停用',
  `cat` datetime DEFAULT NULL COMMENT '创建时间',
  `mat` datetime DEFAULT NULL COMMENT '修改时间',
  `dat` datetime DEFAULT NULL COMMENT '删除时间',
  `operID` varchar(255) DEFAULT NULL COMMENT '最后操作员ID',
  `bankNo` varchar(255) DEFAULT NULL COMMENT '银行卡号',
  `bankPhone` varchar(255) DEFAULT NULL COMMENT '预留手机号',
  `bankAccountName` varchar(255) DEFAULT NULL COMMENT '银行卡户名',
  `bankName` varchar(255) DEFAULT NULL COMMENT '开户行',
  `bankCode` varchar(255) DEFAULT NULL COMMENT '行号',
  PRIMARY KEY (`id`),
  KEY `merchantNo_merchantName` (`merchantNo`,`merchantName`,`merchantType`,`cardID`,`mobile`,`email`,`status`,`cat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户表';

-- ----------------------------
-- Table structure for merchant_user
-- ----------------------------
DROP TABLE IF EXISTS `merchant_user`;
CREATE TABLE `merchant_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `merchantID` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `merchantID_userID` (`merchantID`,`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户操作员关系表';

-- ----------------------------
-- Table structure for qrcode_info
-- ----------------------------
DROP TABLE IF EXISTS `qrcode_info`;
CREATE TABLE `qrcode_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `wxAcctID` int(11) DEFAULT NULL COMMENT '所属微信账号ID',
  `wxAcct` varchar(255) DEFAULT NULL COMMENT '微信账号',
  `qrcodeNo` varchar(255) DEFAULT NULL COMMENT '二维码唯一编号（备注编号）',
  `amount` decimal(20,2) DEFAULT NULL COMMENT '交易金额',
  `realAmount` decimal(20,2) DEFAULT NULL COMMENT '实际金额',
  `isLock` varchar(1) DEFAULT NULL COMMENT '是否锁定',
  `isVail` varchar(1) DEFAULT NULL COMMENT '当前是否可用',
  `imgName` varchar(255) DEFAULT NULL COMMENT '二维码图片名称',
  `cat` datetime DEFAULT NULL COMMENT '创建时间',
  `mat` datetime DEFAULT NULL COMMENT '修改时间',
  `dat` datetime DEFAULT NULL COMMENT '删除时间',
  `operID` varchar(255) DEFAULT NULL COMMENT '最后操作员ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `merID_merNo_custName_cardID_mobileBank_bankcardNo` (`qrcodeNo`,`amount`,`isLock`,`imgName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='二维码信息表';

-- ----------------------------
-- Table structure for qrcode_wxacct
-- ----------------------------
DROP TABLE IF EXISTS `qrcode_wxacct`;
CREATE TABLE `qrcode_wxacct` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `wxAcct` varchar(255) DEFAULT NULL COMMENT '微信账号',
  `isLogin` varchar(1) DEFAULT NULL COMMENT '登陆状态',
  `cat` datetime DEFAULT NULL COMMENT '创建时间',
  `mat` datetime DEFAULT NULL COMMENT '修改时间',
  `dat` datetime DEFAULT NULL COMMENT '删除时间',
  `operID` varchar(255) DEFAULT NULL COMMENT '最后操作员ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='二维码信息表';

-- ----------------------------
-- Table structure for s_attachment
-- ----------------------------
DROP TABLE IF EXISTS `s_attachment`;
CREATE TABLE `s_attachment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '文件名称',
  `path` varchar(200) DEFAULT NULL COMMENT '存储路径',
  `module` varchar(50) DEFAULT NULL COMMENT '所属模块',
  `objId` int(11) DEFAULT NULL COMMENT '关联对象id',
  `cAt` datetime DEFAULT NULL,
  `dAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fname` (`name`),
  KEY `module` (`module`),
  KEY `objId` (`objId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for s_content
-- ----------------------------
DROP TABLE IF EXISTS `s_content`;
CREATE TABLE `s_content` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` text COMMENT '标题',
  `text` longtext COMMENT '内容',
  `summary` text COMMENT '摘要',
  `linkTo` varchar(256) DEFAULT NULL COMMENT '引用的连接地址，用于区分是原创还是引用',
  `thumbnail` varchar(255) DEFAULT NULL COMMENT '缩略图',
  `module` varchar(32) DEFAULT NULL COMMENT '模型',
  `userid` bigint(20) unsigned DEFAULT NULL COMMENT '用户ID',
  `userip` varchar(128) DEFAULT NULL COMMENT 'IP地址',
  `userAgent` text COMMENT '发布浏览agent',
  `parentId` int(20) unsigned DEFAULT NULL COMMENT '父级内容ID',
  `status` varchar(32) DEFAULT NULL COMMENT '状态,00:正常，01，待审核，02，未通过审核',
  `voteUp` int(11) unsigned DEFAULT '0' COMMENT '支持人数',
  `voteDown` int(11) unsigned DEFAULT '0' COMMENT '反对人数',
  `rate` int(11) DEFAULT NULL COMMENT '评分分数',
  `ratecount` int(10) unsigned DEFAULT '0' COMMENT '评分次数',
  `commentStatus` varchar(2) DEFAULT NULL COMMENT '评论状态',
  `commentCount` int(11) unsigned DEFAULT '0' COMMENT '评论总数',
  `commentTime` datetime DEFAULT NULL COMMENT '最后评论时间',
  `viewCount` int(11) unsigned DEFAULT '0' COMMENT '访问量',
  `cAt` datetime DEFAULT NULL COMMENT '创建日期',
  `dAt` datetime DEFAULT NULL COMMENT '删除时间',
  `mAt` datetime DEFAULT NULL COMMENT '修改时间',
  `metaKeywords` varchar(256) DEFAULT NULL COMMENT 'SEO关键字',
  `metaDesc` varchar(256) DEFAULT NULL COMMENT 'SEO描述信息',
  `remarks` text COMMENT '备注信息',
  `good` varchar(1) DEFAULT NULL COMMENT '精华0：是，1：否',
  `pAt` datetime DEFAULT NULL COMMENT '发布时间',
  `top` varchar(1) DEFAULT NULL COMMENT '置顶0：是，1：否',
  `flag` varchar(2) DEFAULT '' COMMENT '00,正常，01，草稿',
  `collectCount` int(11) DEFAULT '0' COMMENT '收藏次数',
  `laudCount` int(11) DEFAULT '0' COMMENT '点赞次数',
  PRIMARY KEY (`id`),
  KEY `user_id` (`userid`),
  KEY `parent_id` (`parentId`),
  KEY `content_module` (`module`),
  KEY `created` (`cAt`),
  KEY `vote_down` (`voteDown`),
  KEY `vote_up` (`voteUp`),
  KEY `view_count` (`viewCount`),
  KEY `good` (`good`),
  KEY `top` (`top`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容表，用于存放比如文章、帖子、商品、问答等用户自定义模型内容。';

-- ----------------------------
-- Table structure for s_mapping
-- ----------------------------
DROP TABLE IF EXISTS `s_mapping`;
CREATE TABLE `s_mapping` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `cid` int(20) unsigned NOT NULL COMMENT '内容ID',
  `tid` int(20) unsigned NOT NULL COMMENT '分类ID',
  PRIMARY KEY (`id`),
  KEY `taxonomy_id` (`tid`),
  KEY `content_id` (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容和分类的多对多映射关系。';

-- ----------------------------
-- Table structure for s_param
-- ----------------------------
DROP TABLE IF EXISTS `s_param`;
CREATE TABLE `s_param` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `k` varchar(100) DEFAULT NULL COMMENT '键',
  `val` text COMMENT '值',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='系统参数表，设置系统参数信息';

-- ----------------------------
-- Records of s_param
-- ----------------------------
BEGIN;
INSERT INTO `s_param` VALUES (1, 'siteEmail', '', NULL);
INSERT INTO `s_param` VALUES (2, 'siteName', '二维码支付管理系统', NULL);
INSERT INTO `s_param` VALUES (3, 'siteTitle', '二维码支付管理系统', NULL);
INSERT INTO `s_param` VALUES (4, 'siteSubheading', '', NULL);
INSERT INTO `s_param` VALUES (5, 'siteDescription', '', NULL);
INSERT INTO `s_param` VALUES (6, 'siteDomain', 'http://127.0.0.1', NULL);
INSERT INTO `s_param` VALUES (7, 'siteSEO', '', NULL);
INSERT INTO `s_param` VALUES (8, 'smtp', '', NULL);
INSERT INTO `s_param` VALUES (9, 'emailName', '', NULL);
INSERT INTO `s_param` VALUES (10, 'emailPassword', '', NULL);
INSERT INTO `s_param` VALUES (11, 'clearTime', '23:30:00.000', NULL);
INSERT INTO `s_param` VALUES (12, 'ePath', '/WEB-INF/excel_tmp/', NULL);
INSERT INTO `s_param` VALUES (13, 'minAmt', '10', NULL);
INSERT INTO `s_param` VALUES (14, 'mpId', 'wxba8552ea75558efb;', NULL);
INSERT INTO `s_param` VALUES (15, 'clearEmailUser', '', NULL);
COMMIT;

-- ----------------------------
-- Table structure for s_res
-- ----------------------------
DROP TABLE IF EXISTS `s_res`;
CREATE TABLE `s_res` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '资源名',
  `url` varchar(255) DEFAULT NULL COMMENT '资源url',
  `description` varchar(100) DEFAULT NULL COMMENT '说明',
  `pid` int(11) DEFAULT NULL COMMENT '父id',
  `seq` int(11) DEFAULT NULL COMMENT '顺序',
  `logged` char(1) DEFAULT NULL COMMENT '是否需要记录日志1：是，0：否',
  `type` varchar(2) DEFAULT NULL COMMENT '资源类型,0:菜单，1：服务',
  `enabled` varchar(2) DEFAULT 'y' COMMENT '是否可用 y:是，n:否',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8 COMMENT='资源表，保存可访问资源数据';

-- ----------------------------
-- Records of s_res
-- ----------------------------
BEGIN;
INSERT INTO `s_res` VALUES (1, '系统管理', '/admin', NULL, 0, 1, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (2, '系统参数设置', '/admin/param', NULL, 1, 1, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (3, '用户管理', '/admin/user', NULL, 1, 2, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (4, '角色管理', '/admin/role', NULL, 1, 3, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (7, '分类管理', '/admin/tax', NULL, 1, 5, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (19, '系统管理服务', '/ad00', NULL, 0, 5, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (20, '系统参数设置服务', '/ad00/getSettingJSON', NULL, 19, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (21, '系统参数设置保存服务', '/ad00/save', NULL, 19, 2, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (22, '用户管理服务', '/ad01', NULL, 0, 6, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (23, '用户管理查询服务', '/ad01/list', NULL, 22, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (24, '用户管理新增服务', '/ad01/save', NULL, 22, 2, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (25, '用户管理更新服务', '/ad01/update', NULL, 22, 3, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (26, '用户管理删除服务', '/ad01/del', NULL, 22, 4, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (27, '用户管理禁用服务', '/ad01/forbidden', NULL, 22, 5, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (28, '用户管理恢复服务', '/ad01/resumed', NULL, 22, 6, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (29, '用户重置密码服务', '/ad01/resetPwd', NULL, 22, 7, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (30, '角色管理服务', '/ad02', NULL, 0, 7, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (31, '角色管理查询服务', '/ad02/list', NULL, 30, 1, '', '1', 'y');
INSERT INTO `s_res` VALUES (32, '角色管理保存服务', '/ad02/save', NULL, 30, 2, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (33, '角色管理更新服务', '/ad02/update', NULL, 30, 3, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (34, '角色管理删除服务', '/ad02/del', NULL, 30, 4, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (35, '角色管理资源设置服务', '/ad02/setRes', NULL, 30, 5, '', '1', 'y');
INSERT INTO `s_res` VALUES (36, '分类管理服务', '/ad05', NULL, 0, 8, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (37, '分类管理JSON数据查询服务', '/ad05/treeJsonArray', NULL, 36, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (38, '分类管理新增服务', '/ad05/save', NULL, 36, 2, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (39, '分类管理更新服务', '/ad05/update', NULL, 36, 3, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (40, '分类管理删除服务', '/ad05/del', NULL, 36, 4, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (94, '商户管理', '/mer', NULL, 0, 1, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (95, '商户信息管理', '/mer/merinfo', NULL, 94, 2, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (96, '商户客户管理', '/mer/custquery', NULL, 94, 3, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (98, '商户管理服务', '/mer00', NULL, 0, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (99, '商户信息查询服务', '/mer00/list', NULL, 98, 2, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (104, '商户信息保存服务', '/mer00/save', NULL, 98, 3, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (105, '商户信息更新服务', '/mer00/update', NULL, 98, 4, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (106, '商户信息删除服务', '/mer00/del', NULL, 98, 5, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (107, '商户信息禁用服务', '/mer00/forbidden', NULL, 98, 6, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (108, '商户信息激活服务', '/mer00/enable', NULL, 98, 7, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (116, '商户交易查询', '/merUser', NULL, 0, 1, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (117, '商户交易查询', '/merUser/tradeLog', NULL, 116, 2, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (124, '商户信息查询', '/mer/merquery', NULL, 94, 2, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (129, '商户客户保存服务', '/mer01/save', NULL, 109, 4, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (130, '商户客户更新服务', '/mer01/update', NULL, 109, 5, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (142, '微信管理', '/qrcode', NULL, 0, 3, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (143, '微信账号管理', '/qrcode/wx', NULL, 142, 1, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (144, '二维码管理', '/qrcode/info', NULL, 142, 2, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (145, '微信账号管理服务', '/qr00', NULL, 0, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (146, '微信账号查询服务', '/qr00/list', NULL, 145, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (147, '微信账号明细查询服务', '/qr00/info', NULL, 145, 2, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (148, '微信账号上传二维码文件服务', '/qr00/upQrZip', NULL, 145, 3, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (149, '微信账号新增服务', '/qr00/save', NULL, 145, 4, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (150, '微信账号删除服务', '/qr00/del', NULL, 145, 5, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (152, '二维码管理服务', '/qr01', NULL, 0, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (153, '二维码统计查询服务', '/qr01/list', NULL, 152, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (154, '交易管理服务', '/tt00', NULL, 0, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (155, '交易查询服务', '/tt00/list', NULL, 154, 1, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (156, '交易管理', '/trade', NULL, 0, 4, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (157, '交易查询', '/trade/log', NULL, 156, 1, NULL, '0', 'y');
INSERT INTO `s_res` VALUES (158, '交易凭证上传服务', '/tt00/upTradeImg', NULL, 154, 2, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (159, '交易状态更正服务', '/tt00/saveTrade', NULL, 154, 3, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (160, '微信账号登录服务', '/qr00/wxLogin', NULL, 145, 6, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (161, '获取微信登陆二维码服务', '/qr00/getLoginImg', NULL, 145, 7, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (162, '查询登陆状态服务', '/qr00/queryLoginStatus', NULL, 145, 8, NULL, '1', 'y');
INSERT INTO `s_res` VALUES (163, '商户账户提现', '/mer00/updateAmount', NULL, 98, 9, '', '1', 'y');
INSERT INTO `s_res` VALUES (164, '微信账号退出服务', '/qr00/wxLogout', NULL, 145, 9, NULL, '1', 'y');
COMMIT;

-- ----------------------------
-- Table structure for s_role
-- ----------------------------
DROP TABLE IF EXISTS `s_role`;
CREATE TABLE `s_role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '角色名',
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of s_role
-- ----------------------------
BEGIN;
INSERT INTO `s_role` VALUES (1, 'admin', '管理员');
INSERT INTO `s_role` VALUES (2, 'oper', '操作员');
INSERT INTO `s_role` VALUES (5, 'user', '商户操作用户');
COMMIT;

-- ----------------------------
-- Table structure for s_role_res
-- ----------------------------
DROP TABLE IF EXISTS `s_role_res`;
CREATE TABLE `s_role_res` (
  `roleId` int(11) DEFAULT NULL COMMENT '角色id',
  `resId` int(11) DEFAULT NULL COMMENT '资源id',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3704 DEFAULT CHARSET=utf8 COMMENT='角色，资源多对多关系表';

-- ----------------------------
-- Records of s_role_res
-- ----------------------------
BEGIN;
INSERT INTO `s_role_res` VALUES (1, 2, 3364);
INSERT INTO `s_role_res` VALUES (1, 1, 3365);
INSERT INTO `s_role_res` VALUES (1, 3, 3366);
INSERT INTO `s_role_res` VALUES (1, 4, 3367);
INSERT INTO `s_role_res` VALUES (1, 7, 3368);
INSERT INTO `s_role_res` VALUES (1, 19, 3369);
INSERT INTO `s_role_res` VALUES (1, 20, 3370);
INSERT INTO `s_role_res` VALUES (1, 21, 3371);
INSERT INTO `s_role_res` VALUES (1, 22, 3372);
INSERT INTO `s_role_res` VALUES (1, 23, 3373);
INSERT INTO `s_role_res` VALUES (1, 24, 3374);
INSERT INTO `s_role_res` VALUES (1, 25, 3375);
INSERT INTO `s_role_res` VALUES (1, 26, 3376);
INSERT INTO `s_role_res` VALUES (1, 27, 3377);
INSERT INTO `s_role_res` VALUES (1, 28, 3378);
INSERT INTO `s_role_res` VALUES (1, 29, 3379);
INSERT INTO `s_role_res` VALUES (1, 30, 3380);
INSERT INTO `s_role_res` VALUES (1, 31, 3381);
INSERT INTO `s_role_res` VALUES (1, 32, 3382);
INSERT INTO `s_role_res` VALUES (1, 33, 3383);
INSERT INTO `s_role_res` VALUES (1, 34, 3384);
INSERT INTO `s_role_res` VALUES (1, 35, 3385);
INSERT INTO `s_role_res` VALUES (1, 36, 3386);
INSERT INTO `s_role_res` VALUES (1, 37, 3387);
INSERT INTO `s_role_res` VALUES (1, 38, 3388);
INSERT INTO `s_role_res` VALUES (1, 39, 3389);
INSERT INTO `s_role_res` VALUES (1, 40, 3390);
INSERT INTO `s_role_res` VALUES (5, 116, 3639);
INSERT INTO `s_role_res` VALUES (5, 117, 3640);
INSERT INTO `s_role_res` VALUES (5, 155, 3641);
INSERT INTO `s_role_res` VALUES (5, 154, 3642);
INSERT INTO `s_role_res` VALUES (2, 95, 3673);
INSERT INTO `s_role_res` VALUES (2, 94, 3674);
INSERT INTO `s_role_res` VALUES (2, 98, 3675);
INSERT INTO `s_role_res` VALUES (2, 99, 3676);
INSERT INTO `s_role_res` VALUES (2, 104, 3677);
INSERT INTO `s_role_res` VALUES (2, 105, 3678);
INSERT INTO `s_role_res` VALUES (2, 106, 3679);
INSERT INTO `s_role_res` VALUES (2, 107, 3680);
INSERT INTO `s_role_res` VALUES (2, 108, 3681);
INSERT INTO `s_role_res` VALUES (2, 163, 3682);
INSERT INTO `s_role_res` VALUES (2, 145, 3683);
INSERT INTO `s_role_res` VALUES (2, 146, 3684);
INSERT INTO `s_role_res` VALUES (2, 147, 3685);
INSERT INTO `s_role_res` VALUES (2, 148, 3686);
INSERT INTO `s_role_res` VALUES (2, 149, 3687);
INSERT INTO `s_role_res` VALUES (2, 150, 3688);
INSERT INTO `s_role_res` VALUES (2, 160, 3689);
INSERT INTO `s_role_res` VALUES (2, 161, 3690);
INSERT INTO `s_role_res` VALUES (2, 162, 3691);
INSERT INTO `s_role_res` VALUES (2, 164, 3692);
INSERT INTO `s_role_res` VALUES (2, 152, 3693);
INSERT INTO `s_role_res` VALUES (2, 153, 3694);
INSERT INTO `s_role_res` VALUES (2, 154, 3695);
INSERT INTO `s_role_res` VALUES (2, 155, 3696);
INSERT INTO `s_role_res` VALUES (2, 158, 3697);
INSERT INTO `s_role_res` VALUES (2, 159, 3698);
INSERT INTO `s_role_res` VALUES (2, 142, 3699);
INSERT INTO `s_role_res` VALUES (2, 143, 3700);
INSERT INTO `s_role_res` VALUES (2, 144, 3701);
INSERT INTO `s_role_res` VALUES (2, 156, 3702);
INSERT INTO `s_role_res` VALUES (2, 157, 3703);
COMMIT;

-- ----------------------------
-- Table structure for s_taxonomy
-- ----------------------------
DROP TABLE IF EXISTS `s_taxonomy`;
CREATE TABLE `s_taxonomy` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(150) DEFAULT NULL COMMENT '标题',
  `text` text COMMENT '内容描述',
  `module` varchar(32) DEFAULT NULL COMMENT '对于的内容模型',
  `count` int(11) unsigned DEFAULT '0' COMMENT '该分类的内容数量',
  `idx` int(11) DEFAULT NULL COMMENT '排序编码',
  `parentId` int(20) unsigned DEFAULT NULL COMMENT '父级分类的ID',
  `cAt` datetime DEFAULT NULL COMMENT '创建日期',
  `dAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `title` (`title`),
  KEY `module` (`module`),
  KEY `parentid` (`parentId`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COMMENT='分类表。标签、专题、类别等都属于taxonomy。';

-- ----------------------------
-- Records of s_taxonomy
-- ----------------------------
BEGIN;
INSERT INTO `s_taxonomy` VALUES (17, '商户类型', NULL, 'merType', 0, NULL, 0, NULL, NULL);
INSERT INTO `s_taxonomy` VALUES (18, '内部商户', '1', 'merType', 0, NULL, 17, NULL, NULL);
INSERT INTO `s_taxonomy` VALUES (19, '外部商户', '2', 'merType', 0, NULL, 17, NULL, NULL);
INSERT INTO `s_taxonomy` VALUES (20, '手续费计算方式', NULL, 'feeCollectType', 0, NULL, 0, NULL, NULL);
INSERT INTO `s_taxonomy` VALUES (21, '正常', '1', 'feeCollectType', 0, NULL, 20, NULL, NULL);
INSERT INTO `s_taxonomy` VALUES (22, '手续费+1（济南金控）', '0', 'feeCollectType', 0, NULL, 20, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for s_ufile
-- ----------------------------
DROP TABLE IF EXISTS `s_ufile`;
CREATE TABLE `s_ufile` (
  `id` varchar(255) NOT NULL COMMENT '主键系统生成GUID',
  `type` varchar(20) DEFAULT NULL COMMENT '文件类型',
  `path` varchar(255) NOT NULL COMMENT '文件相对路径“模块/年月”',
  `cat` datetime NOT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for s_user
-- ----------------------------
DROP TABLE IF EXISTS `s_user`;
CREATE TABLE `s_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `idcard` varchar(50) DEFAULT NULL COMMENT '证件号',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `score` int(11) DEFAULT '0' COMMENT '积分',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `email` varchar(255) DEFAULT NULL COMMENT '邮件',
  `signature` varchar(1000) DEFAULT NULL COMMENT '个性签名',
  `third_id` varchar(100) DEFAULT NULL COMMENT '三方登陆id',
  `access_token` varchar(100) DEFAULT NULL COMMENT '登陆印证token',
  `receive_msg` tinyint(1) DEFAULT NULL COMMENT '是否接受社区消息  1：是，0：否',
  `c_at` datetime DEFAULT NULL COMMENT '创建时间',
  `m_at` datetime DEFAULT NULL COMMENT '更新时间',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话号',
  `channel` varchar(100) DEFAULT NULL COMMENT '第三方渠道',
  `status` varchar(1) DEFAULT NULL COMMENT '状体0-默认，1-禁用',
  `forbiddenReason` varchar(1000) DEFAULT NULL COMMENT '禁用原因',
  `third_access_token` varchar(100) DEFAULT NULL COMMENT '第三方登录获取的access_token',
  `logged` datetime DEFAULT NULL COMMENT '最后登录时间',
  `activated` datetime DEFAULT NULL COMMENT '激活时间',
  `email_status` tinyint(1) DEFAULT NULL COMMENT 'email是否认证 1：是，0：否',
  `phone_status` tinyint(1) DEFAULT NULL COMMENT 'phone认证1：是，0：否',
  `content_count` int(11) DEFAULT '0' COMMENT '发布数',
  `comment_count` int(11) DEFAULT '0' COMMENT '评论数',
  `idcardtype` tinyint(2) DEFAULT NULL COMMENT '身份证件类型',
  `password` varchar(128) DEFAULT NULL COMMENT '登陆密码',
  `salt` varchar(32) DEFAULT NULL COMMENT '盐',
  `loginname` varchar(50) DEFAULT NULL COMMENT '登陆名',
  `isAdmin` varchar(1) DEFAULT NULL COMMENT '是否是管理员',
  `unionid` varchar(50) DEFAULT NULL COMMENT '微信多平台统一登录id',
  `d_at` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `status` (`status`),
  KEY `content_count` (`content_count`),
  KEY `comment_count` (`comment_count`),
  KEY `c_at` (`c_at`),
  KEY `nickname` (`nickname`),
  KEY `email` (`email`),
  KEY `phone` (`phone`),
  KEY `loginname` (`loginname`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of s_user
-- ----------------------------
BEGIN;
INSERT INTO `s_user` VALUES (8, NULL, '管理员', 0, 'http://images.cichlid.cc/image/avatar/dft-avatar.jpg', NULL, '写点啥吧～', NULL, NULL, NULL, '2018-01-27 22:06:12', '2018-05-07 10:43:21', '13998377271', NULL, '0', NULL, NULL, '2018-09-13 17:08:07', NULL, 0, 0, 0, 0, NULL, '$2a$10$HhLZkzVCsbp5KHwGRY2KuOPmKq4y5AiWcULWQwdQVr0UuBAqNV44G', '$2a$10$HhLZkzVCsbp5KHwGRY2KuO', 'mb-admin', '0', NULL, NULL);
INSERT INTO `s_user` VALUES (9, NULL, '操作员', 0, 'http://images.cichlid.cc/image/avatar/dft-avatar.jpg', NULL, '写点啥吧～', NULL, NULL, NULL, '2018-01-27 22:06:48', '2018-01-27 22:06:48', '18899998888', NULL, '0', NULL, NULL, '2018-09-11 19:48:04', NULL, 0, 0, 0, 0, NULL, '$2a$10$N.2yLGRkdXQLUTBiFre3S.Ehhh8Rv.G8fccvPu8gJn5rscnfpvduu', '$2a$10$N.2yLGRkdXQLUTBiFre3S.', 'oper', '1', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for s_user_role
-- ----------------------------
DROP TABLE IF EXISTS `s_user_role`;
CREATE TABLE `s_user_role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `rId` int(11) DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='用户角色多对多关系表';

-- ----------------------------
-- Records of s_user_role
-- ----------------------------
BEGIN;
INSERT INTO `s_user_role` VALUES (25, 9, 2);
INSERT INTO `s_user_role` VALUES (45, 8, 1);
COMMIT;

-- ----------------------------
-- Table structure for trade_log
-- ----------------------------
DROP TABLE IF EXISTS `trade_log`;
CREATE TABLE `trade_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tradeNo` varchar(255) DEFAULT NULL COMMENT '交易流水号',
  `tradeMerID` int(11) DEFAULT NULL COMMENT '交易商户ID',
  `tradeMerNo` varchar(255) DEFAULT NULL COMMENT '交易商户编号',
  `tradeMerName` varchar(255) DEFAULT NULL COMMENT '交易商户名称',
  `tradeTime` datetime DEFAULT NULL COMMENT '交易时间',
  `tradeAmount` decimal(20,2) DEFAULT NULL COMMENT '交易金额',
  `tradeRealAmount` decimal(20,2) DEFAULT NULL COMMENT '交易实付金额',
  `tradeWxAcct` varchar(255) DEFAULT NULL COMMENT '交易二维码微信账号',
  `tradeQrcodeID` int(11) DEFAULT NULL COMMENT '交易二维码唯一标识（ID）',
  `tradeQrcodeImg` varchar(255) DEFAULT NULL COMMENT '交易二维码文件名',
  `tradeStatus` varchar(1) DEFAULT NULL COMMENT '交易状态 0：交易成功 1：交易已发起 2：交易失败',
  `callBackUrl` varchar(255) DEFAULT NULL COMMENT '交易结果回调完整地址',
  `reqMsg` text COMMENT '交易原始报文',
  `resMsg` text COMMENT '交易响应报文',
  `tradeImgName` varchar(255) DEFAULT NULL COMMENT '交易凭证文件名',
  `cat` datetime DEFAULT NULL COMMENT '创建时间',
  `mat` datetime DEFAULT NULL COMMENT '修改时间',
  `dat` datetime DEFAULT NULL COMMENT '删除时间',
  `operID` varchar(255) DEFAULT NULL COMMENT '最后操作员ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='二维码信息表';

SET FOREIGN_KEY_CHECKS = 1;
