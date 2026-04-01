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
    -- 角色体系字段（新增）
    user_type VARCHAR(20) COMMENT '用户类型：SUPPLIER/EMPLOYEE/MANAGER',
    role_code VARCHAR(50) COMMENT '角色编码',
    sub_role VARCHAR(50) COMMENT '子角色：SELLER/RENTER/MAINTAINER/TRANSFER/INVENTORY',
    data_scope VARCHAR(20) COMMENT '数据范围：ALL/DEPT/STORE/SELF/ASSIGNED',
    supplier_id BIGINT COMMENT '关联供应商ID（外部用户）',
    managed_depts TEXT COMMENT '管理的部门列表（JSON）',
    managed_stores TEXT COMMENT '管理的门店列表（JSON）',
    -- 测试账户标记
    is_test_account TINYINT DEFAULT 0 COMMENT '是否测试账户',
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
-- 9. 供应商表（新增）
-- =============================================
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    supplier_type VARCHAR(20) COMMENT '供应商类型：SELLER/RENTER/MAINTAINER',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    address TEXT COMMENT '地址',
    business_license VARCHAR(100) COMMENT '营业执照号',
    bank_account VARCHAR(100) COMMENT '银行账号',
    status VARCHAR(20) COMMENT '状态：合作中/暂停/终止',
    remark TEXT COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 供应商索引
CREATE INDEX idx_supplier_type ON supplier(supplier_type);
CREATE INDEX idx_supplier_status ON supplier(status);

-- =============================================
-- 10. 权限审计表（新增）
-- =============================================
CREATE TABLE IF NOT EXISTS permission_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    target_user_id BIGINT NOT NULL COMMENT '被操作用户ID',
    target_user_name VARCHAR(50) COMMENT '被操作用户姓名',
    change_type VARCHAR(20) COMMENT '变更类型：ADD/REMOVE/UPDATE',
    old_roles TEXT COMMENT '变更前角色',
    new_roles TEXT COMMENT '变更后角色',
    change_reason VARCHAR(500) COMMENT '变更原因',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 权限审计索引
CREATE INDEX idx_audit_operator ON permission_audit(operator_id);
CREATE INDEX idx_audit_target ON permission_audit(target_user_id);
CREATE INDEX idx_audit_time ON permission_audit(create_time);

-- =============================================
-- 11. 初始化数据
-- =============================================

-- 插入默认管理员用户（密码: admin123）
INSERT INTO "user" (username, password, real_name, role, user_type, role_code, status, department) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', 'ADMIN', 'MANAGER', 'MGR_SYSTEM', '在职', 'IT部');

-- 插入测试资产数据
INSERT INTO asset (asset_code, asset_name, category, brand, model, serial_number, purchase_date, purchase_amount, location_type, location_detail, status) 
VALUES 
('NB-26-0001', 'MacBook Pro 16寸', 'NB', 'Apple', 'MacBook Pro 16" M3 Max', 'C02ZW1YJMD6T', '2026-01-15', 29999.00, '总部', '总部-1F-仓库', 'IN_STOCK'),
('DS-26-0001', 'Dell显示器 U2723QE', 'DS', 'Dell', 'U2723QE', 'SN123456789', '2026-01-20', 4500.00, '总部', '总部-1F-仓库', 'IN_STOCK'),
('IP-26-0001', 'iPad Pro 12.9寸', 'IP', 'Apple', 'iPad Pro 12.9" M2', 'SN987654321', '2026-02-01', 8999.00, '门店', '深圳店-收银台', 'IN_USE');

-- 插入测试供应商
INSERT INTO supplier (supplier_code, supplier_name, supplier_type, contact_name, contact_phone, status) VALUES
('SUP001', 'Apple官方授权经销商', 'SELLER', '张经理', '13800138001', '合作中'),
('SUP002', '联想租赁服务有限公司', 'RENTER', '李经理', '13800138002', '合作中'),
('SUP003', '快修电脑维修中心', 'MAINTAINER', '王师傅', '13800138003', '合作中');

-- =============================================
-- 12. 系统集成中间表（新增）
-- =============================================

-- SRM供应商同步中间表
CREATE TABLE IF NOT EXISTS srm_supplier_sync (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    srm_supplier_code VARCHAR(50) NOT NULL COMMENT 'SRM系统供应商编码',
    srm_supplier_name VARCHAR(100) NOT NULL COMMENT 'SRM系统供应商名称',
    supplier_type VARCHAR(20) COMMENT '供应商类型：SELLER/RENTER/MAINTAINER',
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    address TEXT,
    business_license VARCHAR(100),
    bank_account VARCHAR(100),
    status VARCHAR(20) COMMENT 'SRM状态',
    sync_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '同步状态：PENDING/SYNCED/FAILED/IGNORED',
    sync_time TIMESTAMP COMMENT '同步时间',
    sync_error TEXT COMMENT '同步错误信息',
    local_supplier_id BIGINT COMMENT '关联本地供应商ID',
    srm_create_time TIMESTAMP COMMENT 'SRM创建时间',
    srm_update_time TIMESTAMP COMMENT 'SRM更新时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_srm_code ON srm_supplier_sync(srm_supplier_code);
CREATE INDEX idx_srm_sync_status ON srm_supplier_sync(sync_status);
CREATE INDEX idx_srm_type ON srm_supplier_sync(supplier_type);

-- 北森HR员工同步中间表
CREATE TABLE IF NOT EXISTS hr_employee_sync (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hr_employee_id VARCHAR(50) NOT NULL COMMENT '北森员工ID',
    hr_employee_no VARCHAR(50) COMMENT '员工工号',
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    department_code VARCHAR(50) COMMENT '部门编码',
    department_name VARCHAR(100) COMMENT '部门名称',
    store_code VARCHAR(50) COMMENT '门店编码',
    store_name VARCHAR(100) COMMENT '门店名称',
    position VARCHAR(100) COMMENT '职位',
    employee_type VARCHAR(20) COMMENT '总部员工/门店员工',
    status VARCHAR(20) COMMENT '在职/离职',
    join_date DATE,
    leave_date DATE,
    manager_id VARCHAR(50) COMMENT '上级ID',
    sync_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '同步状态',
    sync_time TIMESTAMP COMMENT '同步时间',
    sync_error TEXT COMMENT '同步错误信息',
    local_user_id BIGINT COMMENT '关联本地用户ID',
    hr_create_time TIMESTAMP,
    hr_update_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hr_emp_id ON hr_employee_sync(hr_employee_id);
CREATE INDEX idx_hr_emp_status ON hr_employee_sync(sync_status);

-- 北森部门同步中间表
CREATE TABLE IF NOT EXISTS hr_department_sync (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hr_dept_code VARCHAR(50) NOT NULL COMMENT '北森部门编码',
    hr_dept_name VARCHAR(100) NOT NULL,
    parent_code VARCHAR(50) COMMENT '上级部门编码',
    dept_level INT COMMENT '部门层级',
    dept_type VARCHAR(20) COMMENT '部门类型：总部/区域/门店',
    status VARCHAR(20),
    sync_status VARCHAR(20) DEFAULT 'PENDING',
    sync_time TIMESTAMP,
    local_dept_id BIGINT,
    hr_create_time TIMESTAMP,
    hr_update_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hr_dept_code ON hr_department_sync(hr_dept_code);

-- =============================================
-- 13. 测试账户（开发测试用，生产环境禁用）
-- =============================================

-- 测试账户
INSERT INTO "user" (username, password, real_name, role, user_type, role_code, data_scope, status, department, is_test_account) VALUES
('test_asset_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试资产管理员', 'ASSET_ADMIN', 'MANAGER', 'MGR_ASSET_ADMIN', 'ALL', '在职', 'IT部', 1),
('test_transfer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试调拨员', 'TRANSFER', 'MANAGER', 'MGR_TRANSFER', 'ASSIGNED', '在职', 'IT部', 1),
('test_inventory', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试盘点员', 'INVENTORY', 'MANAGER', 'MGR_INVENTORY', 'ASSIGNED', '在职', 'IT部', 1),
('test_finance', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试财务专员', 'FINANCE', 'MANAGER', 'MGR_FINANCE', 'ALL', '在职', '财务部', 1),
('test_store_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试店长', 'STORE_MANAGER', 'EMPLOYEE', 'USER_STORE_MANAGER', 'STORE', '在职', '深圳店', 1),
('test_dept_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试部门经理', 'DEPT_MANAGER', 'EMPLOYEE', 'USER_DEPT_MANAGER', 'DEPT', '在职', '技术部', 1),
('test_employee', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试员工', 'USER', 'EMPLOYEE', 'USER_EMPLOYEE', 'SELF', '在职', '技术部', 1),
('test_supplier_seller', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试出售方', 'SUPPLIER', 'SUPPLIER', 'SUPPLIER_SELLER', 'ORDER', '合作中', NULL, 1),
('test_supplier_renter', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试出租方', 'SUPPLIER', 'SUPPLIER', 'SUPPLIER_RENTER', 'ORDER', '合作中', NULL, 1),
('test_supplier_maintainer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试维修方', 'SUPPLIER', 'SUPPLIER', 'SUPPLIER_MAINTAINER', 'ORDER', '合作中', NULL, 1);

-- 测试供应商数据
INSERT INTO supplier (supplier_code, supplier_name, supplier_type, contact_name, contact_phone, status) VALUES
('TEST_SELLER_001', '测试设备出售商', 'SELLER', '张销售', '13800138001', '合作中'),
('TEST_RENTER_001', '测试设备租赁商', 'RENTER', '李租赁', '13800138002', '合作中'),
('TEST_MAINTAINER_001', '测试维修服务商', 'MAINTAINER', '王维修', '13800138003', '合作中');