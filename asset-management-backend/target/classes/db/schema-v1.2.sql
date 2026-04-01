-- 资产管理系统数据库表结构 v1.2
-- 更新内容：
-- 1. 添加核实记录表（支持多轮沟通）
-- 2. 添加流程实例表（支持OA集成）
-- 3. 优化索引支持5万级数据量

-- =============================================
-- 1. 用户表
-- =============================================
CREATE TABLE IF NOT EXISTS "user" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    employee_no VARCHAR(50) UNIQUE,
    email VARCHAR(100),
    phone VARCHAR(20),
    department VARCHAR(100),
    employee_type VARCHAR(20) COMMENT '总部员工/门店员工',
    store_name VARCHAR(100),
    role VARCHAR(50) COMMENT 'ADMIN/ASSET_MANAGER/REGION_MANAGER/DEPT_MANAGER/STORE_MANAGER/USER',
    status VARCHAR(20) COMMENT '在职/离职',
    join_date DATE,
    leave_date DATE,
    last_login_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 用户表索引
CREATE INDEX idx_user_dept ON "user"(department);
CREATE INDEX idx_user_role ON "user"(role);
CREATE INDEX idx_user_status ON "user"(status);

-- =============================================
-- 2. 资产表
-- =============================================
CREATE TABLE IF NOT EXISTS asset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_code VARCHAR(50) NOT NULL UNIQUE,
    asset_name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL COMMENT 'PC/NB/IP/DS/PE/NT/PO/MAT',
    sub_category VARCHAR(50),
    brand VARCHAR(50),
    model VARCHAR(100),
    serial_number VARCHAR(100),
    purchase_date DATE,
    purchase_amount DECIMAL(15,2),
    warranty_date DATE,
    supplier VARCHAR(100),
    location_type VARCHAR(20) COMMENT '总部/门店/仓库',
    location_detail VARCHAR(200),
    status VARCHAR(20) NOT NULL COMMENT '库存/在用/调拨中/维修中/报废/盘亏',
    current_user_id BIGINT,
    current_user_name VARCHAR(50),
    department VARCHAR(100),
    employee_type VARCHAR(20),
    assign_date DATE,
    expected_return_date DATE,
    asset_photo_url VARCHAR(500),
    purchase_doc_url VARCHAR(500),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 资产表索引（优化5万级数据查询）
CREATE INDEX idx_asset_code ON asset(asset_code);
CREATE INDEX idx_asset_status ON asset(status);
CREATE INDEX idx_asset_category ON asset(category);
CREATE INDEX idx_asset_location ON asset(location_detail);
CREATE INDEX idx_asset_dept ON asset(department);
CREATE INDEX idx_asset_user ON asset(current_user_id);
CREATE INDEX idx_asset_supplier ON asset(supplier);
CREATE INDEX idx_asset_warranty ON asset(warranty_date);

-- =============================================
-- 3. 资产流转记录表
-- =============================================
CREATE TABLE IF NOT EXISTS asset_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    operation_type VARCHAR(20) NOT NULL COMMENT '入库/领用/归还/调拨/维修/报废/盘点调整',
    operation_date DATE NOT NULL,
    operator_id BIGINT,
    operator_name VARCHAR(50),
    from_user_id BIGINT,
    from_user_name VARCHAR(50),
    to_user_id BIGINT,
    to_user_name VARCHAR(50),
    from_location VARCHAR(200),
    to_location VARCHAR(200),
    approval_no VARCHAR(50),
    description TEXT,
    attachment_url VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 流转记录索引
CREATE INDEX idx_trans_asset ON asset_transaction(asset_id);
CREATE INDEX idx_trans_type ON asset_transaction(operation_type);
CREATE INDEX idx_trans_date ON asset_transaction(operation_date);

-- =============================================
-- 4. 盘点任务表
-- =============================================
CREATE TABLE IF NOT EXISTS inventory_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(20) NOT NULL UNIQUE,
    task_name VARCHAR(100) NOT NULL,
    scope VARCHAR(50) COMMENT '全部/指定部门/指定门店',
    scope_detail VARCHAR(200),
    planned_start_date DATE,
    planned_end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    status VARCHAR(20) COMMENT '计划中/进行中/核实中/已完成',
    total_count INT DEFAULT 0,
    checked_count INT DEFAULT 0,
    normal_count INT DEFAULT 0,
    missing_count INT DEFAULT 0,
    surplus_count INT DEFAULT 0,
    location_change_count INT DEFAULT 0,
    user_change_count INT DEFAULT 0,
    manager_id BIGINT,
    manager_name VARCHAR(50),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 盘点任务索引
CREATE INDEX idx_inv_task_batch ON inventory_task(batch_no);
CREATE INDEX idx_inv_task_status ON inventory_task(status);
CREATE INDEX idx_inv_task_date ON inventory_task(planned_start_date);

-- =============================================
-- 5. 盘点明细表
-- =============================================
CREATE TABLE IF NOT EXISTS inventory_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    asset_code VARCHAR(50),
    planned_date DATE,
    actual_date DATE,
    checker_id BIGINT,
    checker_name VARCHAR(50),
    result VARCHAR(20) COMMENT '正常/位置变更/责任人变更/盘亏/盘盈',
    checked_location VARCHAR(200),
    checked_user_id BIGINT,
    checked_user_name VARCHAR(50),
    -- 简化：只保留当前状态，详细记录在 verification 表
    current_status VARCHAR(20) COMMENT 'PENDING/SUBMITTED/UNDER_REVIEW/RESOLVED/CLOSED',
    current_round INT DEFAULT 0,
    is_closed BOOLEAN DEFAULT FALSE,
    final_result VARCHAR(20),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 盘点明细索引
CREATE INDEX idx_inv_item_task ON inventory_item(task_id);
CREATE INDEX idx_inv_item_asset ON inventory_item(asset_id);
CREATE INDEX idx_inv_item_result ON inventory_item(result);
CREATE INDEX idx_inv_item_status ON inventory_item(current_status);

-- =============================================
-- 6. 核实记录表（新增 - 支持多轮沟通）
-- =============================================
CREATE TABLE IF NOT EXISTS inventory_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL COMMENT '关联盘点明细',
    task_id BIGINT NOT NULL COMMENT '关联盘点任务',
    round INT DEFAULT 1 COMMENT '第几轮沟通',
    verifier_id BIGINT COMMENT '核实人ID',
    verifier_name VARCHAR(50) COMMENT '核实人姓名',
    verifier_type VARCHAR(20) COMMENT 'MANAGER/USER/SYSTEM',
    action VARCHAR(20) COMMENT 'SUBMIT/REVIEW/RETURN/CLOSE',
    result VARCHAR(20) COMMENT 'PASS/REJECT/NEED_INFO/MODIFIED',
    comment TEXT COMMENT '核实意见',
    attachments TEXT COMMENT '附件列表（JSON格式）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 核实记录索引
CREATE INDEX idx_ver_item ON inventory_verification(inventory_item_id);
CREATE INDEX idx_ver_task ON inventory_verification(task_id);
CREATE INDEX idx_ver_round ON inventory_verification(inventory_item_id, round);
CREATE INDEX idx_ver_create ON inventory_verification(create_time);

-- =============================================
-- 7. 流程实例表（新增 - 支持OA集成）
-- =============================================
CREATE TABLE IF NOT EXISTS workflow_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    biz_type VARCHAR(50) NOT NULL COMMENT '业务类型：INVENTORY/ASSIGN/TRANSFER/REPAIR/SCRAP',
    biz_id BIGINT NOT NULL COMMENT '业务ID',
    biz_code VARCHAR(100) COMMENT '业务编号（展示用）',
    status VARCHAR(20) NOT NULL COMMENT '流程状态',
    source VARCHAR(20) DEFAULT 'INTERNAL' COMMENT '来源：OA/INTERNAL/SUPPLIER',
    callback_url VARCHAR(500) COMMENT 'OA回调地址',
    callback_token VARCHAR(255) COMMENT '回调签名密钥',
    current_round INT DEFAULT 1 COMMENT '当前轮次',
    max_rounds INT DEFAULT 10 COMMENT '最大轮次限制',
    is_closed BOOLEAN DEFAULT FALSE COMMENT '是否已结案',
    closed_time TIMESTAMP COMMENT '结案时间',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 流程实例索引
CREATE INDEX idx_wf_type_id ON workflow_instance(biz_type, biz_id);
CREATE INDEX idx_wf_status ON workflow_instance(status);
CREATE INDEX idx_wf_source ON workflow_instance(source);
CREATE INDEX idx_wf_closed ON workflow_instance(is_closed);

-- =============================================
-- 8. 流程审批记录表（新增）
-- =============================================
CREATE TABLE IF NOT EXISTS workflow_approval (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_id BIGINT NOT NULL COMMENT '关联流程实例',
    round INT DEFAULT 1 COMMENT '第几轮审批',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(50),
    approver_type VARCHAR(20) COMMENT 'MANAGER/DEPT_HEAD/HR/FINANCE',
    action VARCHAR(20) COMMENT 'SUBMIT/APPROVE/REJECT/RETURN/CLOSE',
    result VARCHAR(20) COMMENT 'APPROVED/REJECTED/NEED_INFO',
    comment TEXT,
    attachments TEXT COMMENT '附件（JSON）',
    next_approver_id BIGINT COMMENT '下一审批人（用于多级审批）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 审批记录索引
CREATE INDEX idx_wf_approval_workflow ON workflow_approval(workflow_id);
CREATE INDEX idx_wf_approval_round ON workflow_approval(workflow_id, round);

-- =============================================
-- 9. 初始化数据
-- =============================================

-- 插入默认管理员用户（密码: admin123）
INSERT INTO "user" (username, password, real_name, role, status, department) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', 'ADMIN', '在职', 'IT部');

-- 插入测试资产数据
INSERT INTO asset (asset_code, asset_name, category, brand, model, serial_number, purchase_date, purchase_amount, location_type, location_detail, status) 
VALUES 
('NB-26-0001', 'MacBook Pro 16寸', 'NB', 'Apple', 'MacBook Pro 16" M3 Max', 'C02ZW1YJMD6T', '2026-01-15', 29999.00, '总部', '总部-1F-仓库', 'IN_STOCK'),
('DS-26-0001', 'Dell显示器 U2723QE', 'DS', 'Dell', 'U2723QE', 'SN123456789', '2026-01-20', 4500.00, '总部', '总部-1F-仓库', 'IN_STOCK'),
('IP-26-0001', 'iPad Pro 12.9寸', 'IP', 'Apple', 'iPad Pro 12.9" M2', 'SN987654321', '2026-02-01', 8999.00, '门店', '深圳店-收银台', 'IN_USE');