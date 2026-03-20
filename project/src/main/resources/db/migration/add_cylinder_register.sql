-- ========================================
-- 气瓶追踪管理增强脚本
-- 执行时机：在 pinganbao_db.sql 基础上增量执行
-- ========================================

-- 1. 为 contract_cylinder_line 添加唯一约束，确保每个合同仅一种气瓶类型
ALTER TABLE `contract_cylinder_line`
ADD UNIQUE KEY `uk_ccl_contract` (`contract_id`);

-- 2. 创建气瓶登记表（用于追踪每个气瓶实例）
DROP TABLE IF EXISTS `contract_cylinder_register`;
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
  `cylinder_status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常使用 2-待检验 3-已过期 4-已报废 5-封存',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='合同气瓶登记追踪表' ROW_FORMAT=Dynamic;

-- 3. 创建气瓶检验记录表（用于记录历次检验）
DROP TABLE IF EXISTS `cylinder_inspect_log`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='气瓶检验记录表' ROW_FORMAT=Dynamic;

-- 4. 示例数据（可选）
-- INSERT INTO contract_cylinder_register
-- (contract_id, cylinder_type_id, cylinder_no, rfid_tag, manufacture_date, manufacturer,
--  design_pressure, volume, weight_empty, last_inspect_date, next_inspect_date,
--  cylinder_status, location, registered_by)
-- VALUES
-- (1, 101, 'CYL-2026-001', 'RFID-A001', '2024-01-15', '南京某制瓶厂',
--  15.00, 40.00, 48.50, '2025-01-15', '2028-01-15',
--  1, '南京市江宁区某充装站', 1);
