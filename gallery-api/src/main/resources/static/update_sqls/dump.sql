-- MySQL dump 10.16  Distrib 10.1.35-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 10.220.110.166    Database: gallery
-- ------------------------------------------------------
-- Server version	5.7.23-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tb_admin`
--

DROP TABLE IF EXISTS `tb_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL COMMENT '帐号',
  `hashed_pwd` varchar(32) NOT NULL COMMENT '加密密码',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remark` varchar(256) NOT NULL DEFAULT '',
  `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '角色id，默认为0',
  `brand_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '品牌id，默认为0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_admin_id_uindex` (`id`),
  UNIQUE KEY `tb_admin_username_uindex` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_admin`
--

/*!40000 ALTER TABLE `tb_admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_admin` ENABLE KEYS */;

--
-- Table structure for table `tb_admin_menu`
--

DROP TABLE IF EXISTS `tb_admin_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_admin_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `p_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父id',
  `menu_name` varchar(45) NOT NULL COMMENT '菜单名',
  `menu_icon` varchar(45) NOT NULL DEFAULT '' COMMENT '角标',
  `menu_address` varchar(100) NOT NULL COMMENT '页面跳转路径',
  `weight` int(11) NOT NULL DEFAULT '100' COMMENT '排序权重，从大到小',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_admin_menu_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_admin_menu`
--

/*!40000 ALTER TABLE `tb_admin_menu` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_admin_menu` ENABLE KEYS */;

--
-- Table structure for table `tb_admin_role`
--

DROP TABLE IF EXISTS `tb_admin_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_admin_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL COMMENT '角色名称',
  `desc` varchar(256) NOT NULL DEFAULT '' COMMENT '简介',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_admin_role_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_admin_role`
--

/*!40000 ALTER TABLE `tb_admin_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_admin_role` ENABLE KEYS */;

--
-- Table structure for table `tb_admin_role_menu`
--

DROP TABLE IF EXISTS `tb_admin_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_admin_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `menu_id` bigint(20) NOT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_admin_role_menu_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_admin_role_menu`
--

/*!40000 ALTER TABLE `tb_admin_role_menu` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_admin_role_menu` ENABLE KEYS */;

--
-- Table structure for table `tb_brand`
--

DROP TABLE IF EXISTS `tb_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '品牌名称',
  `contact` varchar(32) NOT NULL DEFAULT '' COMMENT '品牌联系人',
  `contact_phone` varchar(11) NOT NULL DEFAULT '' COMMENT '品牌联系人手机号',
  `introduction` varchar(512) NOT NULL DEFAULT '' COMMENT '简介',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expired_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '有效期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_brand_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_brand`
--

/*!40000 ALTER TABLE `tb_brand` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_brand` ENABLE KEYS */;

--
-- Table structure for table `tb_brand_active_code`
--

DROP TABLE IF EXISTS `tb_brand_active_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_brand_active_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(8) NOT NULL DEFAULT '' COMMENT '激活码',
  `status` enum('un_active','is_active') NOT NULL DEFAULT 'un_active' COMMENT '激活状态（un_active：待激活，is_active：已激活）',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '激活用户id',
  `active_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '激活时间',
  `expired_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_brand_active_code_id_uindex` (`id`),
  UNIQUE KEY `tb_brand_active_code_user_id_uindex` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌激活码';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_brand_active_code`
--

/*!40000 ALTER TABLE `tb_brand_active_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_brand_active_code` ENABLE KEYS */;

--
-- Table structure for table `tb_drawcash_log`
--

DROP TABLE IF EXISTS `tb_drawcash_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_drawcash_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `total_history_draw` decimal(11,2) NOT NULL DEFAULT '0.00' COMMENT '历史提现总额',
  `total_history_draw_count` int(11) NOT NULL DEFAULT '0' COMMENT '历史提现次数',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提现申请时间',
  `apply_amount` decimal(11,2) NOT NULL DEFAULT '0.00' COMMENT '申请时间',
  `status` enum('to_be_audit','passed','refused') NOT NULL DEFAULT 'to_be_audit' COMMENT '提现状态（to_be_audit：待审核；passed：已通过；refused：已拒绝）',
  `reason` varchar(256) NOT NULL DEFAULT '' COMMENT '理由，拒绝时必填',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_put_forward_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户提现数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_drawcash_log`
--

/*!40000 ALTER TABLE `tb_drawcash_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_drawcash_log` ENABLE KEYS */;

--
-- Table structure for table `tb_label`
--

DROP TABLE IF EXISTS `tb_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_name` varchar(24) NOT NULL DEFAULT '' COMMENT '标签名称',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_mould_label_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板标签数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_label`
--

/*!40000 ALTER TABLE `tb_label` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_label` ENABLE KEYS */;

--
-- Table structure for table `tb_sowing_map`
--

DROP TABLE IF EXISTS `tb_sowing_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_sowing_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '位置（首页：0；分享获益：1；会员权益：2；3：品牌展示）',
  `img_url` varchar(512) NOT NULL DEFAULT '' COMMENT '图片地址',
  `link_url` varchar(512) NOT NULL DEFAULT '' COMMENT '跳转地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_sowing_map_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sowing_map`
--

/*!40000 ALTER TABLE `tb_sowing_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sowing_map` ENABLE KEYS */;

--
-- Table structure for table `tb_template`
--

DROP TABLE IF EXISTS `tb_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '模板名称',
  `code` varchar(18) NOT NULL DEFAULT '' COMMENT '模板编号，12位数字+6位大写字母组合',
  `type_id` bigint(20) NOT NULL COMMENT '模板分类id',
  `mould_label_names` varchar(512) DEFAULT '' COMMENT '模板标签名称集合，逗号隔开',
  `ratio` varchar(32) NOT NULL DEFAULT '' COMMENT '模板比例',
  `is_enable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启用',
  `preview_pic_url` varchar(512) NOT NULL DEFAULT '' COMMENT '模板预览图地址',
  `template_desc` longtext NOT NULL COMMENT '模板描述，保存图层/文字关系',
  `brand_id` bigint(20) NOT NULL COMMENT '品牌id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_mould_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_template`
--

/*!40000 ALTER TABLE `tb_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_template` ENABLE KEYS */;

--
-- Table structure for table `tb_template_label`
--

DROP TABLE IF EXISTS `tb_template_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_template_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '模板id',
  `label_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '标签id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_template_label_id_uindex` (`id`),
  UNIQUE KEY `tb_template_label_template_id_uindex` (`template_id`),
  UNIQUE KEY `tb_template_label_label_id_uindex` (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板标签关系数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_template_label`
--

/*!40000 ALTER TABLE `tb_template_label` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_template_label` ENABLE KEYS */;

--
-- Table structure for table `tb_template_statistics`
--

DROP TABLE IF EXISTS `tb_template_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_template_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '模板id',
  `template_type_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '模板分类id',
  `brand_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '品牌id',
  `see_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '查看次数',
  `share_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '分享次数',
  `use_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '使用次数',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_mould_statistics_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板统计数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_template_statistics`
--

/*!40000 ALTER TABLE `tb_template_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_template_statistics` ENABLE KEYS */;

--
-- Table structure for table `tb_template_type`
--

DROP TABLE IF EXISTS `tb_template_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_template_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) NOT NULL DEFAULT '' COMMENT '分类名称',
  `weight` int(2) NOT NULL DEFAULT '100' COMMENT '权重',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否热门',
  `introduction` varchar(256) NOT NULL DEFAULT '' COMMENT '简述',
  `background_pic` varchar(256) NOT NULL DEFAULT '' COMMENT '背景图',
  `mould_filters` longtext NOT NULL COMMENT '滤镜数，数组形式。如：[{"num":"1", "url": "xxx"}, {}...]',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_mould_type_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板分类数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_template_type`
--

/*!40000 ALTER TABLE `tb_template_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_template_type` ENABLE KEYS */;

--
-- Table structure for table `tb_user`
--

DROP TABLE IF EXISTS `tb_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_nickname` varchar(32) NOT NULL DEFAULT '' COMMENT '用户昵称',
  `user_headimg` varchar(512) NOT NULL DEFAULT '' COMMENT '用户头像',
  `user_sex` varchar(4) NOT NULL DEFAULT '未知' COMMENT '用户性别',
  `is_member` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否会员',
  `user_member_type` int(1) NOT NULL DEFAULT '0' COMMENT '会员类型（1：半年会员；2：一年会员；3：品牌会员）',
  `member_expired_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会员过期时间',
  `is_agent` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否代理',
  `wx_unionid` varchar(32) NOT NULL DEFAULT '' COMMENT '微信开放id',
  `wxmp_openid` varchar(32) NOT NULL DEFAULT '' COMMENT '微信公众号id',
  `wxmini_openid` varchar(32) NOT NULL DEFAULT '' COMMENT '微信小程序id',
  `user_share_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '分享者id',
  `user_share_name` varchar(32) DEFAULT '' COMMENT '分享者名称',
  `invite_num` int(11) DEFAULT '0' COMMENT '邀请用户数量',
  `total_sharing_amount` decimal(11,2) NOT NULL DEFAULT '0.00' COMMENT '分享获益总额',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_user_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user`
--

/*!40000 ALTER TABLE `tb_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_user` ENABLE KEYS */;

--
-- Table structure for table `tb_user_brand`
--

DROP TABLE IF EXISTS `tb_user_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_user_brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `brand_id` bigint(20) NOT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_user_brand_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_brand`
--

/*!40000 ALTER TABLE `tb_user_brand` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_user_brand` ENABLE KEYS */;

--
-- Table structure for table `tb_user_payment`
--

DROP TABLE IF EXISTS `tb_user_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_user_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT '0' COMMENT '用户id',
  `order_num` varchar(32) NOT NULL DEFAULT '' COMMENT '订单编号，系统生成',
  `order_total_amount` decimal(11,2) NOT NULL DEFAULT '0.00' COMMENT '订单支付金额',
  `order_status` enum('paid','unpay') NOT NULL DEFAULT 'unpay' COMMENT '订单状态（paid：已支付；unpay：未支付）',
  `transaction_id` varchar(36) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `gmt_payment` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `spare_cl0` varchar(32) NOT NULL DEFAULT '' COMMENT '备用字段0',
  `spare_cl1` varchar(32) NOT NULL DEFAULT '' COMMENT '备用字段1',
  `spare_cl2` varchar(32) NOT NULL DEFAULT '' COMMENT '备用字段2',
  `user_name` varchar(32) DEFAULT '' COMMENT '用户名称',
  `user_headimg` varchar(512) NOT NULL DEFAULT '' COMMENT '用户头像',
  `recharge_type` int(11) NOT NULL DEFAULT '0' COMMENT '充值类型（1：半年会员；2：年度会员；）',
  `direct_share_user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '直接分享人id',
  `direct_share_user_name` varchar(32) NOT NULL DEFAULT '' COMMENT '直接分享人名称',
  `direct_share_profit` decimal(11,2) NOT NULL DEFAULT '0.00' COMMENT '直接分享人获益金额',
  `indirect_share_user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '间接分享人id',
  `indirect_share_user_name` varchar(32) NOT NULL DEFAULT '' COMMENT '间接分享人名称',
  `indirect_share_profit` decimal(11,2) NOT NULL DEFAULT '0.00' COMMENT '间接分享人收益',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tb_user_payment_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_payment`
--

/*!40000 ALTER TABLE `tb_user_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_user_payment` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-10-16 13:49:57
