CREATE DATABASE IF NOT EXISTS asset_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE asset_management;

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `employee_no` VARCHAR(50) COMMENT '员工编号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `department` VARCHAR(100) COMMENT '部门',
    `employee_type` VARCHAR(20) COMMENT '员工类型：总部员工/门店员工',
    `store_name` VARCHAR(100) COMMENT '门店名称',
    `role` VARCHAR(50) COMMENT '角色',
    `status` VARCHAR(20) COMMENT '状态：在职/离职',
    `join_date` DATE COMMENT '入职日期',
    `leave_date` DATE COMMENT '离职日期',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_employee_no` (`employee_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 资产表
CREATE TABLE `asset` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `asset_code` VARCHAR(50) NOT NULL COMMENT '资产编号',
    `asset_name` VARCHAR(100) NOT NULL COMMENT '资产名称',
    `category` VARCHAR(20) NOT NULL COMMENT '资产类别',
    `sub_category` VARCHAR(50) COMMENT '二级分类',
    `brand` VARCHAR(50) COMMENT '品牌',
    `model` VARCHAR(100) COMMENT '型号',
    `serial_number` VARCHAR(100) COMMENT 'SN码',
    `purchase_date` DATE COMMENT '购置日期',
    `purchase_amount` DECIMAL(15,2) COMMENT '购置金额',
    `warranty_date` DATE COMMENT '保修期至',
    `supplier` VARCHAR(100) COMMENT '供应商',
    `location_type` VARCHAR(20) COMMENT '存放位置类型：总部/门店/仓库',
    `location_detail` VARCHAR(200) COMMENT '存放位置详情',
    `status` VARCHAR(20) NOT NULL COMMENT '使用状态',
    `current_user_id` BIGINT COMMENT '当前责任人ID',
    `current_user_name` VARCHAR(50) COMMENT '当前责任人姓名',
    `department` VARCHAR(100) COMMENT '责任部门',
    `employee_type` VARCHAR(20) COMMENT '员工类型',
    `assign_date` DATE COMMENT '领用日期',
    `expected_return_date` DATE COMMENT '预计归还日期',
    `asset_photo_url` VARCHAR(500) COMMENT '资产照片URL',
    `purchase_doc_url` VARCHAR(500) COMMENT '购置凭证URL',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_asset_code` (`asset_code`),
    KEY `idx_status` (`status`),
    KEY `idx_category` (`category`),
    KEY `idx_current_user` (`current_user_id`),
    KEY `idx_department` (`department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产表';

-- 资产流转记录表
CREATE TABLE `asset_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `asset_id` BIGINT NOT NULL COMMENT '关联资产ID',
    `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型',
    `operation_date` DATE NOT NULL COMMENT '操作日期',
    `operator_id` BIGINT COMMENT '操作人ID',
    `operator_name` VARCHAR(50) COMMENT '操作人姓名',
    `from_user_id` BIGINT COMMENT '原责任人ID',
    `from_user_name` VARCHAR(50) COMMENT '原责任人姓名',
    `to_user_id` BIGINT COMMENT '目标责任人ID',
    `to_user_name` VARCHAR(50) COMMENT '目标责任人姓名',
    `from_location` VARCHAR(200) COMMENT '原位置',
    `to_location` VARCHAR(200) COMMENT '目标位置',
    `approval_no` VARCHAR(50) COMMENT '审批单号',
    `description` TEXT COMMENT '操作说明',
    `attachment_url` VARCHAR(500) COMMENT '附件URL',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_operation_type` (`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产流转记录表';

-- 盘点任务表
CREATE TABLE `inventory_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `batch_no` VARCHAR(20) NOT NULL COMMENT '盘点批次号',
    `task_name` VARCHAR(100) NOT NULL COMMENT '盘点名称',
    `scope` VARCHAR(50) COMMENT '盘点范围',
    `scope_detail` VARCHAR(200) COMMENT '盘点范围详情',
    `planned_start_date` DATE COMMENT '计划开始日期',
    `planned_end_date` DATE COMMENT '计划结束日期',
    `actual_start_date` DATE COMMENT '实际开始日期',
    `actual_end_date` DATE COMMENT '实际结束日期',
    `status` VARCHAR(20) COMMENT '盘点状态',
    `total_count` INT DEFAULT 0 COMMENT '应盘数量',
    `checked_count` INT DEFAULT 0 COMMENT '实盘数量',
    `normal_count` INT DEFAULT 0 COMMENT '正常数量',
    `missing_count` INT DEFAULT 0 COMMENT '盘亏数量',
    `surplus_count` INT DEFAULT 0 COMMENT '盘盈数量',
    `location_change_count` INT DEFAULT 0 COMMENT '位置变更数量',
    `user_change_count` INT DEFAULT 0 COMMENT '责任人变更数量',
    `manager_id` BIGINT COMMENT '负责人ID',
    `manager_name` VARCHAR(50) COMMENT '负责人姓名',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_batch_no` (`batch_no`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点任务表';

-- 盘点明细表
CREATE TABLE `inventory_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_id` BIGINT NOT NULL COMMENT '盘点任务ID',
    `asset_id` BIGINT NOT NULL COMMENT '关联资产ID',
    `asset_code` VARCHAR(50) COMMENT '资产编号',
    `planned_date` DATE COMMENT '计划盘点日期',
    `actual_date` DATE COMMENT '实际盘点日期',
    `checker_id` BIGINT COMMENT '盘点人ID',
    `checker_name` VARCHAR(50) COMMENT '盘点人姓名',
    `result` VARCHAR(20) COMMENT '盘点结果',
    `checked_location` VARCHAR(200) COMMENT '盘点时位置',
    `checked_user_id` BIGINT COMMENT '盘点时责任人ID',
    `checked_user_name` VARCHAR(50) COMMENT '盘点时责任人姓名',
    `first_verify_status` VARCHAR(20) COMMENT '第一轮核实状态',
    `first_verify_note` TEXT COMMENT '第一轮核实说明',
    `first_verify_by` VARCHAR(50) COMMENT '第一轮核实人',
    `first_verify_time` DATETIME COMMENT '第一轮核实时间',
    `second_verify_status` VARCHAR(20) COMMENT '第二轮确认状态',
    `second_verify_note` TEXT COMMENT '第二轮确认说明',
    `second_verify_by` VARCHAR(50) COMMENT '第二轮确认人',
    `second_verify_time` DATETIME COMMENT '第二轮确认时间',
    `final_result` VARCHAR(20) COMMENT '最终处理结果',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_asset_id` (`asset_id`),
    KEY `idx_result` (`result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点明细表';

-- 插入默认管理员用户
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`, `department`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '管理员', 'ADMIN', '在职', 'IT部');