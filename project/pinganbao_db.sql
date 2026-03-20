/*
 Navicat Premium Dump SQL

 Source Server         : ceshi
 Source Server Type    : MySQL
 Source Server Version : 90100 (9.1.0)
 Source Host           : localhost:3306
 Source Schema         : pinganbao_db

 Target Server Type    : MySQL
 Target Server Version : 90100 (9.1.0)
 File Encoding         : 65001

 Date: 31/12/2025 15:52:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for contract
-- ----------------------------
DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract`  (
  `contract_id` bigint NOT NULL AUTO_INCREMENT COMMENT '合同ID',
  `contract_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '合同编号',
  `unit_id` bigint NOT NULL COMMENT '单位ID',
  `contract_type` tinyint NOT NULL DEFAULT 1 COMMENT '类型：1-初始合同 2-续约合同',
  `parent_contract_id` bigint NULL DEFAULT NULL COMMENT '父合同ID（续约链）',
  `renewal_count` int NOT NULL DEFAULT 0 COMMENT '续约次数',
  `start_date` date NOT NULL COMMENT '生效日期',
  `end_date` date NOT NULL COMMENT '到期日期',
  `renewal_start_date` date NULL DEFAULT NULL COMMENT '续约开始时间',
  `original_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '原价合计',
  `discount_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠合计',
  `final_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '应付合计',
  `contract_status` tinyint NOT NULL DEFAULT 1 COMMENT '合同状态：\r\n    1-草稿/待确认\r\n    2-已确认(待缴费)\r\n    3-缴费审核中\r\n    4-待签章\r\n    5-已生效\r\n    6-即将到期\r\n    7-已到期\r\n    8-已作废\r\n    -1-已拒绝',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`contract_id`) USING BTREE,
  UNIQUE INDEX `contract_no`(`contract_no` ASC) USING BTREE,
  INDEX `idx_contract_unit`(`unit_id` ASC) USING BTREE,
  INDEX `idx_contract_status`(`contract_status` ASC) USING BTREE,
  INDEX `idx_contract_end_date`(`end_date` ASC) USING BTREE,
  INDEX `idx_contract_parent`(`parent_contract_id` ASC) USING BTREE,
  INDEX `idx_contract_unit_status_end`(`unit_id` ASC, `contract_status` ASC, `end_date` DESC) USING BTREE,
  CONSTRAINT `fk_contract_parent` FOREIGN KEY (`parent_contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_contract_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`unit_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract
-- ----------------------------

-- ----------------------------
-- Table structure for contract_cylinder_line
-- ----------------------------
DROP TABLE IF EXISTS `contract_cylinder_line`;
CREATE TABLE `contract_cylinder_line`  (
  `line_id` bigint NOT NULL AUTO_INCREMENT COMMENT '计价行ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `cylinder_type_id` bigint NOT NULL COMMENT '气瓶类型ID',
  `cylinder_qty` int NOT NULL COMMENT '气瓶数量',
  `unit_price` decimal(12, 2) NOT NULL COMMENT '单价(元/瓶/年)',
  `years` decimal(5, 2) NOT NULL COMMENT '年限',
  `original_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '原价',
  `discount_rate` decimal(6, 4) NOT NULL DEFAULT 1.0000 COMMENT '折扣率',
  `discount_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `final_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '折后金额',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`line_id`) USING BTREE,
  INDEX `idx_ccl_contract`(`contract_id` ASC) USING BTREE,
  INDEX `idx_ccl_type`(`cylinder_type_id` ASC) USING BTREE,
  CONSTRAINT `fk_ccl_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_ccl_type` FOREIGN KEY (`cylinder_type_id`) REFERENCES `cylinder_type_dict` (`cylinder_type_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同气瓶类型计价明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract_cylinder_line
-- ----------------------------

-- ----------------------------
-- Table structure for contract_file
-- ----------------------------
DROP TABLE IF EXISTS `contract_file`;
CREATE TABLE `contract_file`  (
  `file_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `file_type` tinyint NOT NULL COMMENT '类型：1-扫描件 2-草稿PDF 3-签章PDF 4-证据链',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件URL',
  `file_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件hash',
  `uploaded_by` bigint NULL DEFAULT NULL COMMENT '上传人',
  `uploaded_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_cf_contract`(`contract_id` ASC) USING BTREE,
  INDEX `idx_cf_type`(`file_type` ASC) USING BTREE,
  CONSTRAINT `fk_contract_file_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract_file
-- ----------------------------

-- ----------------------------
-- Table structure for contract_item
-- ----------------------------
DROP TABLE IF EXISTS `contract_item`;
CREATE TABLE `contract_item`  (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '产品明细ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `product_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品编码',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `unit_price` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `idx_ci_contract`(`contract_id` ASC) USING BTREE,
  INDEX `idx_ci_product`(`product_code` ASC) USING BTREE,
  CONSTRAINT `fk_contract_item_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同产品明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract_item
-- ----------------------------

-- ----------------------------
-- Table structure for contract_merge_ref
-- ----------------------------
DROP TABLE IF EXISTS `contract_merge_ref`;
CREATE TABLE `contract_merge_ref`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merged_contract_id` bigint NOT NULL COMMENT '汇总合同ID',
  `original_contract_id` bigint NOT NULL COMMENT '原合同ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cmr_pair`(`merged_contract_id` ASC, `original_contract_id` ASC) USING BTREE,
  INDEX `idx_cmr_merged`(`merged_contract_id` ASC) USING BTREE,
  INDEX `idx_cmr_original`(`original_contract_id` ASC) USING BTREE,
  CONSTRAINT `fk_cmr_merged` FOREIGN KEY (`merged_contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_cmr_original` FOREIGN KEY (`original_contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '汇总合同-原合同关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract_merge_ref
-- ----------------------------

-- ----------------------------
-- Table structure for contract_no_sequence
-- ----------------------------
DROP TABLE IF EXISTS `contract_no_sequence`;
CREATE TABLE `contract_no_sequence`  (
  `seq_id` bigint NOT NULL AUTO_INCREMENT,
  `region_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地区编码',
  `unit_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单位编码',
  `year` int NOT NULL COMMENT '年份',
  `last_seq` int NOT NULL DEFAULT 0 COMMENT '当前最大序号',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`seq_id`) USING BTREE,
  UNIQUE INDEX `uk_cns`(`region_code` ASC, `unit_code` ASC, `year` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同编号序列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract_no_sequence
-- ----------------------------

-- ----------------------------
-- Table structure for contract_party_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `contract_party_snapshot`;
CREATE TABLE `contract_party_snapshot`  (
  `snapshot_id` bigint NOT NULL AUTO_INCREMENT COMMENT '快照ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `party_a_unit_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '甲方单位名称',
  `party_a_social_credit_code` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方社会信用代码',
  `party_a_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方地址',
  `party_a_principal_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方负责人',
  `party_a_principal_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方负责人电话',
  `party_a_principal_id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方负责人身份证',
  `party_a_bank_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方开户银行',
  `party_a_bank_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方银行账号',
  `party_a_bank_branch` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方开户支行',
  `party_b_company_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '乙方公司名称',
  `party_b_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '乙方联系电话',
  `party_b_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '乙方地址',
  `party_b_bank_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '乙方开户行',
  `party_b_bank_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '乙方银行账号',
  `party_b_bank_branch` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '乙方开户支行',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`snapshot_id`) USING BTREE,
  UNIQUE INDEX `contract_id`(`contract_id` ASC) USING BTREE,
  CONSTRAINT `fk_cps_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同甲乙双方信息快照' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract_party_snapshot
-- ----------------------------

-- ----------------------------
-- Table structure for cylinder_type_dict
-- ----------------------------
DROP TABLE IF EXISTS `cylinder_type_dict`;
CREATE TABLE `cylinder_type_dict`  (
  `cylinder_type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '气瓶类型ID',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型编码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cylinder_type_id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '气瓶类型字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cylinder_type_dict
-- ----------------------------
INSERT INTO `cylinder_type_dict` VALUES (1, 'LPG', '液化石油气瓶', 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `cylinder_type_dict` VALUES (2, 'CNG', '压缩天然气瓶', 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `cylinder_type_dict` VALUES (3, 'O2', '工业氧气瓶', 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `cylinder_type_dict` VALUES (4, 'N2', '液氮气瓶', 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `cylinder_type_dict` VALUES (5, 'CO2', '二氧化碳气瓶', 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `cylinder_type_dict` VALUES (6, 'AR', '氩气瓶', 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `cylinder_type_dict` VALUES (7, 'H2', '氢气瓶', 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `cylinder_type_dict` VALUES (8, 'ACETYLENE', '乙炔气瓶', 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');

-- ----------------------------
-- Table structure for esign_envelope
-- ----------------------------
DROP TABLE IF EXISTS `esign_envelope`;
CREATE TABLE `esign_envelope`  (
  `envelope_id` bigint NOT NULL AUTO_INCREMENT COMMENT '签章任务ID',
  `case_id` bigint NOT NULL COMMENT '续约案件ID',
  `provider` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '签章服务商',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-待发起 2-签章中 3-成功 -1-失败',
  `request_payload` json NULL COMMENT '请求参数',
  `callback_payload` json NULL COMMENT '回调原文',
  `started_at` datetime NULL DEFAULT NULL,
  `finished_at` datetime NULL DEFAULT NULL,
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`envelope_id`) USING BTREE,
  UNIQUE INDEX `case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_ee_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_ee_case` FOREIGN KEY (`case_id`) REFERENCES `renewal_case` (`case_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '电子签章任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of esign_envelope
-- ----------------------------

-- ----------------------------
-- Table structure for esign_file
-- ----------------------------
DROP TABLE IF EXISTS `esign_file`;
CREATE TABLE `esign_file`  (
  `file_id` bigint NOT NULL AUTO_INCREMENT COMMENT '签章文件ID',
  `envelope_id` bigint NOT NULL COMMENT '签章任务ID',
  `file_type` tinyint NOT NULL COMMENT '类型：1-签前PDF 2-签后PDF 3-证据链',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_ef_envelope`(`envelope_id` ASC) USING BTREE,
  INDEX `idx_ef_type`(`file_type` ASC) USING BTREE,
  CONSTRAINT `fk_ef_envelope` FOREIGN KEY (`envelope_id`) REFERENCES `esign_envelope` (`envelope_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '签章文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of esign_file
-- ----------------------------

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `notice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `notice_type` tinyint NOT NULL COMMENT '类型：1-到期提醒 2-续约待审核 3-审核结果 4-缴费提醒 5-签章完成',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `target_unit_id` bigint NULL DEFAULT NULL COMMENT '目标单位',
  `target_user_id` bigint NULL DEFAULT NULL COMMENT '目标用户',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notice_id`) USING BTREE,
  INDEX `idx_notice_unit`(`target_unit_id` ASC) USING BTREE,
  INDEX `idx_notice_user`(`target_user_id` ASC) USING BTREE,
  INDEX `idx_notice_type`(`notice_type` ASC) USING BTREE,
  INDEX `idx_notice_unit_type_created`(`target_unit_id` ASC, `notice_type` ASC, `created_at` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '站内通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------

-- ----------------------------
-- Table structure for notification_delivery
-- ----------------------------
DROP TABLE IF EXISTS `notification_delivery`;
CREATE TABLE `notification_delivery`  (
  `delivery_id` bigint NOT NULL AUTO_INCREMENT COMMENT '投递ID',
  `notice_id` bigint NOT NULL COMMENT '通知ID',
  `channel` tinyint NOT NULL COMMENT '渠道：1-站内 2-短信 3-邮件',
  `receiver` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收方',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待发送 1-已发送 -1-失败',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `last_error` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sent_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`delivery_id`) USING BTREE,
  INDEX `idx_nd_notice`(`notice_id` ASC) USING BTREE,
  INDEX `idx_nd_status`(`status` ASC) USING BTREE,
  INDEX `idx_nd_channel`(`channel` ASC) USING BTREE,
  CONSTRAINT `fk_nd_notice` FOREIGN KEY (`notice_id`) REFERENCES `notification` (`notice_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知投递记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification_delivery
-- ----------------------------

-- ----------------------------
-- Table structure for op_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `op_audit_log`;
CREATE TABLE `op_audit_log`  (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '审计日志ID',
  `actor_type` tinyint NOT NULL COMMENT '操作人类型：1-单位人员 2-管理员 3-系统',
  `actor_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '动作',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对象类型',
  `target_id` bigint NULL DEFAULT NULL COMMENT '对象ID',
  `before_data` json NULL COMMENT '变更前数据',
  `after_data` json NULL COMMENT '变更后数据',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_oal_actor`(`actor_type` ASC, `actor_id` ASC) USING BTREE,
  INDEX `idx_oal_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_oal_created`(`created_at` ASC) USING BTREE,
  INDEX `idx_oal_target_created`(`target_type` ASC, `target_id` ASC, `created_at` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作审计日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of op_audit_log
-- ----------------------------

-- ----------------------------
-- Table structure for payment_order
-- ----------------------------
DROP TABLE IF EXISTS `payment_order`;
CREATE TABLE `payment_order`  (
  `pay_order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付订单ID',
  `pay_order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付订单号',
  `case_id` bigint NOT NULL COMMENT '续约案件ID',
  `unit_id` bigint NOT NULL COMMENT '单位ID',
  `pay_method` tinyint NOT NULL DEFAULT 3 COMMENT '支付方式：1-微信 2-支付宝 3-对公转账',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-待支付 2-已提交凭证 3-审核通过 4-审核拒绝 -1-已取消 -2-异常',
  `amount_expected` decimal(18, 2) NOT NULL COMMENT '应付金额',
  `amount_paid` decimal(18, 2) NULL DEFAULT NULL COMMENT '实付金额',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'CNY',
  `pay_attempts` int NOT NULL DEFAULT 0 COMMENT '支付尝试次数',
  `expire_at` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pay_order_id`) USING BTREE,
  UNIQUE INDEX `pay_order_no`(`pay_order_no` ASC) USING BTREE,
  UNIQUE INDEX `case_id`(`case_id` ASC) USING BTREE,
  INDEX `idx_po_unit`(`unit_id` ASC) USING BTREE,
  INDEX `idx_po_status`(`status` ASC) USING BTREE,
  INDEX `idx_po_expire`(`expire_at` ASC) USING BTREE,
  INDEX `idx_po_unit_status`(`unit_id` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `fk_po_case` FOREIGN KEY (`case_id`) REFERENCES `renewal_case` (`case_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_po_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`unit_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_order
-- ----------------------------

-- ----------------------------
-- Table structure for payment_voucher
-- ----------------------------
DROP TABLE IF EXISTS `payment_voucher`;
CREATE TABLE `payment_voucher`  (
  `voucher_id` bigint NOT NULL AUTO_INCREMENT COMMENT '凭证ID',
  `pay_order_id` bigint NOT NULL COMMENT '支付订单ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `uploaded_by` bigint NULL DEFAULT NULL COMMENT '上传人',
  `uploaded_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `audit_status` tinyint NOT NULL DEFAULT 1 COMMENT '审核状态：1-待审核 2-通过 -1-拒绝',
  `audited_by` bigint NULL DEFAULT NULL COMMENT '审核人',
  `audited_at` datetime NULL DEFAULT NULL,
  `audit_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  PRIMARY KEY (`voucher_id`) USING BTREE,
  INDEX `idx_pv_order`(`pay_order_id` ASC) USING BTREE,
  INDEX `idx_pv_audit`(`audit_status` ASC) USING BTREE,
  CONSTRAINT `fk_pv_order` FOREIGN KEY (`pay_order_id`) REFERENCES `payment_order` (`pay_order_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付凭证表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_voucher
-- ----------------------------

-- ----------------------------
-- Table structure for product_dict
-- ----------------------------
DROP TABLE IF EXISTS `product_dict`;
CREATE TABLE `product_dict`  (
  `product_id` bigint NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `product_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品编码',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称',
  `product_type` tinyint NOT NULL DEFAULT 1 COMMENT '类型：1-服务 2-硬件 3-增值模块',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`) USING BTREE,
  UNIQUE INDEX `product_code`(`product_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_dict
-- ----------------------------
INSERT INTO `product_dict` VALUES (1, 'STD', '气瓶安全监管服务（标准版）', 1, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `product_dict` VALUES (2, 'TRACK', '气瓶追溯模块', 3, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `product_dict` VALUES (3, 'HANDHELD', '手持机设备服务', 2, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `product_dict` VALUES (4, 'AI_INSPECT', 'AI智能巡检模块', 3, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `product_dict` VALUES (5, 'DATA_REPORT', '数据分析报表服务', 3, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');

-- ----------------------------
-- Table structure for region_dict
-- ----------------------------
DROP TABLE IF EXISTS `region_dict`;
CREATE TABLE `region_dict`  (
  `region_id` bigint NOT NULL AUTO_INCREMENT COMMENT '地区ID',
  `region_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地区/分公司编码',
  `region_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地区/分公司名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`region_id`) USING BTREE,
  UNIQUE INDEX `region_code`(`region_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地区/分公司字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of region_dict
-- ----------------------------
INSERT INTO `region_dict` VALUES (1, 'NJ', '南京分公司', 1, '2025-12-29 17:26:19', '2025-12-29 17:26:19');
INSERT INTO `region_dict` VALUES (2, 'BJ', '北京分公司', 1, '2025-12-29 17:26:19', '2025-12-29 17:26:19');
INSERT INTO `region_dict` VALUES (3, 'SH', '上海分公司', 1, '2025-12-29 17:26:19', '2025-12-29 17:26:19');
INSERT INTO `region_dict` VALUES (4, 'GZ', '广州分公司', 1, '2025-12-29 17:26:19', '2025-12-29 17:26:19');
INSERT INTO `region_dict` VALUES (5, 'SZ', '深圳分公司', 1, '2025-12-29 17:26:19', '2025-12-29 17:26:19');
INSERT INTO `region_dict` VALUES (6, 'CD', '成都分公司', 1, '2025-12-29 17:26:19', '2025-12-29 17:26:19');
INSERT INTO `region_dict` VALUES (7, 'HZ', '杭州分公司', 1, '2025-12-29 17:26:19', '2025-12-29 17:26:19');
INSERT INTO `region_dict` VALUES (8, 'WH', '武汉分公司', 1, '2025-12-29 17:26:19', '2025-12-29 17:26:19');

-- ----------------------------
-- Table structure for renewal_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `renewal_audit_log`;
CREATE TABLE `renewal_audit_log`  (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `case_id` bigint NOT NULL COMMENT '续约案件ID',
  `action` tinyint NOT NULL COMMENT '动作：1-创建 2-单位确认 3-管理员确认 4-管理员拒绝 5-上传凭证 6-财务通过 7-财务拒绝 8-发起签章 9-签章完成 10-取消',
  `actor_type` tinyint NOT NULL COMMENT '操作人类型：1-单位人员 2-管理员 3-系统',
  `actor_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `note` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_ral_case`(`case_id` ASC) USING BTREE,
  INDEX `idx_ral_action`(`action` ASC) USING BTREE,
  INDEX `idx_ral_created`(`created_at` ASC) USING BTREE,
  CONSTRAINT `fk_ral_case` FOREIGN KEY (`case_id`) REFERENCES `renewal_case` (`case_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '续约流程日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of renewal_audit_log
-- ----------------------------

-- ----------------------------
-- Table structure for renewal_case
-- ----------------------------
DROP TABLE IF EXISTS `renewal_case`;
CREATE TABLE `renewal_case`  (
  `case_id` bigint NOT NULL AUTO_INCREMENT COMMENT '续约案件ID',
  `case_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '续约单号',
  `unit_id` bigint NOT NULL COMMENT '单位ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：\r\n    1-填写中\r\n    2-待单位确认\r\n    3-待管理员确认\r\n    4-待缴费\r\n    5-待财务审核\r\n    6-待签章\r\n    7-已完成\r\n    -1-已取消\r\n    -2-已拒绝',
  `expire_at` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `strategy_id` bigint NULL DEFAULT NULL COMMENT '续约策略ID',
  `original_amount` decimal(18, 2) NOT NULL DEFAULT 0.00,
  `discount_amount` decimal(18, 2) NOT NULL DEFAULT 0.00,
  `final_amount` decimal(18, 2) NOT NULL DEFAULT 0.00,
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  PRIMARY KEY (`case_id`) USING BTREE,
  UNIQUE INDEX `case_no`(`case_no` ASC) USING BTREE,
  INDEX `fk_renewal_case_strategy`(`strategy_id` ASC) USING BTREE,
  INDEX `idx_rc_unit`(`unit_id` ASC) USING BTREE,
  INDEX `idx_rc_status`(`status` ASC) USING BTREE,
  INDEX `idx_rc_expire`(`expire_at` ASC) USING BTREE,
  INDEX `idx_rc_unit_status_created`(`unit_id` ASC, `status` ASC, `created_at` DESC) USING BTREE,
  CONSTRAINT `fk_renewal_case_strategy` FOREIGN KEY (`strategy_id`) REFERENCES `renewal_strategy` (`strategy_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_renewal_case_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`unit_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '续约案件主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of renewal_case
-- ----------------------------

-- ----------------------------
-- Table structure for renewal_case_contract
-- ----------------------------
DROP TABLE IF EXISTS `renewal_case_contract`;
CREATE TABLE `renewal_case_contract`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `case_id` bigint NOT NULL COMMENT '续约案件ID',
  `original_contract_id` bigint NOT NULL COMMENT '原合同ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_case_contract`(`case_id` ASC, `original_contract_id` ASC) USING BTREE,
  INDEX `fk_rcc_contract`(`original_contract_id` ASC) USING BTREE,
  INDEX `idx_rcc_case`(`case_id` ASC) USING BTREE,
  CONSTRAINT `fk_rcc_case` FOREIGN KEY (`case_id`) REFERENCES `renewal_case` (`case_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_rcc_contract` FOREIGN KEY (`original_contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '续约案件-原合同关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of renewal_case_contract
-- ----------------------------

-- ----------------------------
-- Table structure for renewal_contract_draft
-- ----------------------------
DROP TABLE IF EXISTS `renewal_contract_draft`;
CREATE TABLE `renewal_contract_draft`  (
  `draft_id` bigint NOT NULL AUTO_INCREMENT COMMENT '草稿ID',
  `case_id` bigint NOT NULL COMMENT '续约案件ID',
  `renew_start_date` date NOT NULL COMMENT '续约开始日期',
  `renew_end_date` date NOT NULL COMMENT '续约结束日期',
  `renewal_years` decimal(5, 2) NOT NULL COMMENT '续约年限',
  `original_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '原价合计',
  `discount_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠合计',
  `final_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '应付合计',
  `unit_principal_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位负责人姓名（续约时可能变更）',
  `unit_principal_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位负责人电话（续约时可能变更）',
  `unit_principal_id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位负责人身份证（续约时可能变更）',
  `unit_bank_account_id` bigint NULL DEFAULT NULL COMMENT '选择的银行账户ID',
  `operator_personnel_id` bigint NOT NULL COMMENT '经办人ID',
  `unit_confirmed` tinyint NOT NULL DEFAULT 0 COMMENT '单位是否确认：1-是 0-否',
  `unit_confirmed_at` datetime NULL DEFAULT NULL COMMENT '单位确认时间',
  `unit_confirmed_by` bigint NULL DEFAULT NULL COMMENT '单位确认人ID',
  `admin_confirmed` tinyint NOT NULL DEFAULT 0 COMMENT '管理员是否确认：1-是 0-否',
  `admin_confirmed_at` datetime NULL DEFAULT NULL COMMENT '管理员确认时间',
  `admin_confirmed_by` bigint NULL DEFAULT NULL COMMENT '管理员确认人ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`draft_id`) USING BTREE,
  UNIQUE INDEX `case_id`(`case_id` ASC) USING BTREE,
  INDEX `fk_rcd_bank`(`unit_bank_account_id` ASC) USING BTREE,
  INDEX `fk_rcd_operator`(`operator_personnel_id` ASC) USING BTREE,
  INDEX `idx_rcd_dates`(`renew_start_date` ASC, `renew_end_date` ASC) USING BTREE,
  INDEX `idx_rcd_confirmed`(`unit_confirmed` ASC, `admin_confirmed` ASC) USING BTREE,
  CONSTRAINT `fk_rcd_bank` FOREIGN KEY (`unit_bank_account_id`) REFERENCES `unit_bank_account` (`account_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_rcd_case` FOREIGN KEY (`case_id`) REFERENCES `renewal_case` (`case_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_rcd_operator` FOREIGN KEY (`operator_personnel_id`) REFERENCES `unit_personnel` (`personnel_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '续约草稿表（优化版：17个字段）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of renewal_contract_draft
-- ----------------------------

-- ----------------------------
-- Table structure for renewal_contract_draft_cylinder_line
-- ----------------------------
DROP TABLE IF EXISTS `renewal_contract_draft_cylinder_line`;
CREATE TABLE `renewal_contract_draft_cylinder_line`  (
  `line_id` bigint NOT NULL AUTO_INCREMENT COMMENT '草稿计价行ID',
  `draft_id` bigint NOT NULL COMMENT '草稿ID',
  `cylinder_type_id` bigint NOT NULL COMMENT '气瓶类型ID',
  `cylinder_qty` int NOT NULL COMMENT '气瓶数量',
  `unit_price` decimal(12, 2) NOT NULL COMMENT '单价(元/瓶/年)',
  `years` decimal(5, 2) NOT NULL COMMENT '年限',
  `original_amount` decimal(18, 2) NOT NULL DEFAULT 0.00,
  `discount_rate` decimal(6, 4) NOT NULL DEFAULT 1.0000,
  `discount_amount` decimal(18, 2) NOT NULL DEFAULT 0.00,
  `final_amount` decimal(18, 2) NOT NULL DEFAULT 0.00,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`line_id`) USING BTREE,
  INDEX `idx_rcdcl_draft`(`draft_id` ASC) USING BTREE,
  INDEX `idx_rcdcl_type`(`cylinder_type_id` ASC) USING BTREE,
  CONSTRAINT `fk_rcdcl_draft` FOREIGN KEY (`draft_id`) REFERENCES `renewal_contract_draft` (`draft_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_rcdcl_type` FOREIGN KEY (`cylinder_type_id`) REFERENCES `cylinder_type_dict` (`cylinder_type_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '续约草稿-气瓶计价明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of renewal_contract_draft_cylinder_line
-- ----------------------------

-- ----------------------------
-- Table structure for renewal_contract_draft_item
-- ----------------------------
DROP TABLE IF EXISTS `renewal_contract_draft_item`;
CREATE TABLE `renewal_contract_draft_item`  (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '草稿产品明细ID',
  `draft_id` bigint NOT NULL COMMENT '草稿ID',
  `product_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品编码',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `unit_price` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `idx_rcdi_draft`(`draft_id` ASC) USING BTREE,
  CONSTRAINT `fk_rcdi_draft` FOREIGN KEY (`draft_id`) REFERENCES `renewal_contract_draft` (`draft_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '续约草稿-产品明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of renewal_contract_draft_item
-- ----------------------------

-- ----------------------------
-- Table structure for renewal_strategy
-- ----------------------------
DROP TABLE IF EXISTS `renewal_strategy`;
CREATE TABLE `renewal_strategy`  (
  `strategy_id` bigint NOT NULL AUTO_INCREMENT COMMENT '策略ID',
  `strategy_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '策略名称',
  `renewal_years` decimal(5, 2) NOT NULL COMMENT '续约年限',
  `is_recommended` tinyint NOT NULL DEFAULT 0 COMMENT '是否推荐：1-是 0-否',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序权重',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`strategy_id`) USING BTREE,
  INDEX `idx_rs_status`(`status` ASC) USING BTREE,
  INDEX `idx_rs_years`(`renewal_years` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '续约策略表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of renewal_strategy
-- ----------------------------
INSERT INTO `renewal_strategy` VALUES (1, '1年基础包', 1.00, 0, 1, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy` VALUES (2, '3年超值包', 3.00, 1, 1, 2, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy` VALUES (3, '5年长期包', 5.00, 0, 1, 3, '2025-12-29 17:26:20', '2025-12-29 17:26:20');

-- ----------------------------
-- Table structure for renewal_strategy_price
-- ----------------------------
DROP TABLE IF EXISTS `renewal_strategy_price`;
CREATE TABLE `renewal_strategy_price`  (
  `price_id` bigint NOT NULL AUTO_INCREMENT COMMENT '价格行ID',
  `strategy_id` bigint NOT NULL COMMENT '策略ID',
  `cylinder_type_id` bigint NOT NULL COMMENT '气瓶类型ID',
  `min_qty` int NOT NULL DEFAULT 0 COMMENT '最小数量（含）',
  `max_qty` int NOT NULL DEFAULT 2147483647 COMMENT '最大数量（含）',
  `credit_level` tinyint NULL DEFAULT NULL COMMENT '适用信用等级（NULL=不限）',
  `unit_price` decimal(12, 2) NOT NULL COMMENT '单价：元/瓶/年',
  `discount_rate` decimal(6, 4) NOT NULL DEFAULT 1.0000 COMMENT '折扣率',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`price_id`) USING BTREE,
  INDEX `idx_rsp_strategy`(`strategy_id` ASC) USING BTREE,
  INDEX `idx_rsp_type`(`cylinder_type_id` ASC) USING BTREE,
  INDEX `idx_rsp_qty`(`min_qty` ASC, `max_qty` ASC) USING BTREE,
  INDEX `idx_rsp_credit`(`credit_level` ASC) USING BTREE,
  INDEX `idx_rsp_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_rsp_cylinder_type` FOREIGN KEY (`cylinder_type_id`) REFERENCES `cylinder_type_dict` (`cylinder_type_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_rsp_strategy` FOREIGN KEY (`strategy_id`) REFERENCES `renewal_strategy` (`strategy_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '续约策略定价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of renewal_strategy_price
-- ----------------------------
INSERT INTO `renewal_strategy_price` VALUES (1, 1, 1, 0, 999999, 1, 10.00, 0.9000, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy_price` VALUES (2, 1, 1, 0, 999999, 2, 10.00, 0.9500, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy_price` VALUES (3, 1, 1, 0, 999999, 3, 10.00, 1.0000, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy_price` VALUES (4, 2, 1, 0, 999999, 1, 9.00, 0.8000, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy_price` VALUES (5, 2, 1, 0, 999999, 2, 9.00, 0.8500, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy_price` VALUES (6, 2, 1, 0, 999999, 3, 9.00, 0.9000, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy_price` VALUES (7, 3, 1, 0, 999999, 1, 8.00, 0.7000, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy_price` VALUES (8, 3, 1, 0, 999999, 2, 8.00, 0.7500, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');
INSERT INTO `renewal_strategy_price` VALUES (9, 3, 1, 0, 999999, 3, 8.00, 0.8000, 1, '2025-12-29 17:26:20', '2025-12-29 17:26:20');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置值',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '说明',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`config_id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'contract_expire_remind_days', '30,7,3,2,1', '合同到期提醒天数', '2025-12-29 17:26:20');
INSERT INTO `sys_config` VALUES (2, 'renewal_case_expire_hours', '24', '续约案件超时小时数', '2025-12-29 17:26:20');
INSERT INTO `sys_config` VALUES (3, 'payment_max_retry', '3', '支付最大重试次数', '2025-12-29 17:26:20');
INSERT INTO `sys_config` VALUES (4, 'provider_company_name', '瓶安保软件服务有限公司', '安保公司名称', '2025-12-29 17:26:20');
INSERT INTO `sys_config` VALUES (5, 'provider_address', '南京市江宁区科技园XX路XX号', '安保公司地址', '2025-12-29 17:26:20');
INSERT INTO `sys_config` VALUES (6, 'provider_phone', '025-88888888', '安保公司联系电话', '2025-12-29 17:26:20');
INSERT INTO `sys_config` VALUES (7, 'provider_bank_name', '工商银行南京科技支行', '安保公司开户行', '2025-12-29 17:26:20');
INSERT INTO `sys_config` VALUES (8, 'provider_bank_branch', '南京科技支行', '安保公司开户支行', '2025-12-29 17:26:20');
INSERT INTO `sys_config` VALUES (9, 'provider_bank_account', '8888666699990000', '安保公司银行账号', '2025-12-29 17:26:20');

-- ----------------------------
-- Table structure for unit
-- ----------------------------
DROP TABLE IF EXISTS `unit`;
CREATE TABLE `unit`  (
  `unit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '单位ID',
  `unit_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单位编码',
  `unit_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单位名称',
  `social_credit_code` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '统一社会信用代码',
  `region_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NJ' COMMENT '地区/分公司编码',
  `unit_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '单位详细地址',
  `unit_principal_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '单位负责人姓名',
  `unit_principal_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '单位负责人电话',
  `unit_principal_id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '单位负责人身份证号',
  `credit_level` tinyint NOT NULL DEFAULT 3 COMMENT '信用等级：1-优秀 2-良好 3-一般 4-差',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-停用 -1-注销',
  `service_status` tinyint NOT NULL DEFAULT 1 COMMENT '服务状态：1-正常 2-受限(到期) 3-冻结',
  `service_valid_to` date NULL DEFAULT NULL COMMENT '服务有效期至',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  PRIMARY KEY (`unit_id`) USING BTREE,
  UNIQUE INDEX `unit_code`(`unit_code` ASC) USING BTREE,
  UNIQUE INDEX `social_credit_code`(`social_credit_code` ASC) USING BTREE,
  INDEX `idx_unit_name`(`unit_name` ASC) USING BTREE,
  INDEX `idx_unit_status`(`status` ASC) USING BTREE,
  INDEX `idx_unit_credit_level`(`credit_level` ASC) USING BTREE,
  INDEX `idx_unit_region`(`region_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '单位主数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of unit
-- ----------------------------

-- ----------------------------
-- Table structure for unit_bank_account
-- ----------------------------
DROP TABLE IF EXISTS `unit_bank_account`;
CREATE TABLE `unit_bank_account`  (
  `account_id` bigint NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `unit_id` bigint NOT NULL COMMENT '单位ID',
  `account_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '开户名',
  `bank_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '开户银行',
  `bank_branch` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开户支行',
  `bank_account_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '银行账号',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认：1-是 0-否',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`) USING BTREE,
  INDEX `idx_uba_unit`(`unit_id` ASC) USING BTREE,
  INDEX `idx_uba_status`(`status` ASC) USING BTREE,
  INDEX `idx_uba_default`(`unit_id` ASC, `is_default` ASC) USING BTREE,
  CONSTRAINT `fk_unit_bank_account_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`unit_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '单位银行账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of unit_bank_account
-- ----------------------------

-- ----------------------------
-- Table structure for unit_cylinder_type
-- ----------------------------
DROP TABLE IF EXISTS `unit_cylinder_type`;
CREATE TABLE `unit_cylinder_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `unit_id` bigint NOT NULL COMMENT '单位ID',
  `cylinder_type_id` bigint NOT NULL COMMENT '气瓶类型ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_unit_cylinder`(`unit_id` ASC, `cylinder_type_id` ASC) USING BTREE,
  INDEX `fk_uct_cylinder_type`(`cylinder_type_id` ASC) USING BTREE,
  INDEX `idx_uct_unit`(`unit_id` ASC) USING BTREE,
  CONSTRAINT `fk_uct_cylinder_type` FOREIGN KEY (`cylinder_type_id`) REFERENCES `cylinder_type_dict` (`cylinder_type_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_uct_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`unit_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '单位气瓶类型关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of unit_cylinder_type
-- ----------------------------

-- ----------------------------
-- Table structure for unit_personnel
-- ----------------------------
DROP TABLE IF EXISTS `unit_personnel`;
CREATE TABLE `unit_personnel`  (
  `personnel_id` bigint NOT NULL AUTO_INCREMENT COMMENT '人员ID',
  `unit_id` bigint NOT NULL COMMENT '所属单位ID',
  `person_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号（登录账号）',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门',
  `role_type` tinyint NOT NULL DEFAULT 2 COMMENT '角色：1-单位管理员 2-操作员 3-只读查看',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-停用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`personnel_id`) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `id_card`(`id_card` ASC) USING BTREE,
  INDEX `idx_up_unit`(`unit_id` ASC) USING BTREE,
  INDEX `idx_up_role`(`role_type` ASC) USING BTREE,
  INDEX `idx_up_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_unit_personnel_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`unit_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT = '单位人员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of unit_personnel
-- ----------------------------

-- ============================================================
-- Auth / User / Role (新增：支持个体用户注册，unit_id 可为空)
-- ============================================================

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称(英文)：ADMIN/UNIT_ADMIN/USER等',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色说明',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role`(`id`,`name`,`description`) VALUES (1,'ADMIN','平台管理员');
INSERT INTO `role`(`id`,`name`,`description`) VALUES (2,'UNIT_ADMIN','单位管理员');
INSERT INTO `role`(`id`,`name`,`description`) VALUES (3,'USER','普通用户/个体用户');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名/账号(可用于密码登录)',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(建议BCrypt加密)',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号(可用于验证码登录/注册)',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `unit_id` bigint NULL DEFAULT NULL COMMENT '所属单位ID(可为空，表示个体用户)',
  `role_id` bigint NOT NULL DEFAULT 3 COMMENT '角色ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-停用 -1-注销',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_id_card`(`id_card` ASC) USING BTREE,
  INDEX `idx_user_unit`(`unit_id` ASC) USING BTREE,
  INDEX `idx_user_role`(`role_id` ASC) USING BTREE,
  INDEX `idx_user_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_user_unit` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`unit_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表(支持个体用户，后续可分配单位)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
CREATE TABLE `cylinder_inspect_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `register_id` bigint NOT NULL COMMENT '气瓶登记ID',
  `cylinder_no` varchar(64) NOT NULL COMMENT '气瓶编号',
  `inspect_date` date NOT NULL COMMENT '检验日期',
  `inspect_result` tinyint NOT NULL COMMENT '检验结果：1-合格 2-不合格 3-报废',
  `inspector` varchar(100) DEFAULT NULL COMMENT '检验员',
  `inspect_org` varchar(200) DEFAULT NULL COMMENT '检验机构',
  `next_inspect_date` date DEFAULT NULL COMMENT '下次检验日期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`) USING BTREE,
  KEY `idx_cil_register` (`register_id`) USING BTREE,
  KEY `idx_cil_cylinder_no` (`cylinder_no`) USING BTREE,
  KEY `idx_cil_date` (`inspect_date`) USING BTREE,
  CONSTRAINT `fk_cil_register` FOREIGN KEY (`register_id`) REFERENCES `contract_cylinder_register` (`register_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='气瓶检验记录表';

CREATE TABLE `contract_cylinder_register` (
  `register_id` bigint NOT NULL AUTO_INCREMENT COMMENT '登记ID',
  `contract_id` bigint NOT NULL COMMENT '所属合同ID',
  `cylinder_type_id` bigint NOT NULL COMMENT '气瓶类型ID',
  `cylinder_no` varchar(64) NOT NULL COMMENT '气瓶编号（唯一）',
  `rfid_tag` varchar(64) DEFAULT NULL COMMENT 'RFID标签编号',
  `manufacture_date` date DEFAULT NULL COMMENT '制造日期',
  `manufacturer` varchar(200) DEFAULT NULL COMMENT '制造厂商',
  `design_pressure` decimal(10,2) DEFAULT NULL COMMENT '设计压力(MPa)',
  `volume` decimal(10,2) DEFAULT NULL COMMENT '容积(L)',
  `weight_empty` decimal(10,2) DEFAULT NULL COMMENT '空瓶重量(kg)',
  `last_inspect_date` date DEFAULT NULL COMMENT '最近检验日期',
  `next_inspect_date` date DEFAULT NULL COMMENT '下次检验到期日',
  `cylinder_status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-正常使用 2-待检验 3-已过期 4-已报废 5-封存',
  `location` varchar(255) DEFAULT NULL COMMENT '当前位置',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `registered_by` bigint DEFAULT NULL COMMENT '登记人ID',
  `registered_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`register_id`) USING BTREE,
  UNIQUE KEY `uk_ccr_cylinder_no` (`cylinder_no`) USING BTREE,
  KEY `idx_ccr_contract` (`contract_id`) USING BTREE,
  KEY `idx_ccr_type` (`cylinder_type_id`) USING BTREE,
  KEY `idx_ccr_status` (`cylinder_status`) USING BTREE,
  KEY `idx_ccr_rfid` (`rfid_tag`) USING BTREE,
  KEY `idx_ccr_next_inspect` (`next_inspect_date`) USING BTREE,
  CONSTRAINT `fk_ccr_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`contract_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ccr_type` FOREIGN KEY (`cylinder_type_id`) REFERENCES `cylinder_type_dict` (`cylinder_type_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='合同气瓶登记追踪表';
SET FOREIGN_KEY_CHECKS = 1;
