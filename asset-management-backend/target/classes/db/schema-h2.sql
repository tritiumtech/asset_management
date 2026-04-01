-- H2 Database Schema for Asset Management

CREATE TABLE IF NOT EXISTS "user" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    employee_no VARCHAR(50) UNIQUE,
    email VARCHAR(100),
    phone VARCHAR(20),
    department VARCHAR(100),
    employee_type VARCHAR(20),
    store_name VARCHAR(100),
    role VARCHAR(50),
    -- 角色体系字段
    user_type VARCHAR(20),
    role_code VARCHAR(50),
    sub_role VARCHAR(50),
    data_scope VARCHAR(20),
    supplier_id BIGINT,
    managed_depts TEXT,
    managed_stores TEXT,
    status VARCHAR(20),
    join_date DATE,
    leave_date DATE,
    last_login_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS asset (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_code VARCHAR(50) NOT NULL UNIQUE,
    asset_name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL,
    sub_category VARCHAR(50),
    brand VARCHAR(50),
    model VARCHAR(100),
    serial_number VARCHAR(100),
    purchase_date DATE,
    purchase_amount DECIMAL(15,2),
    warranty_date DATE,
    supplier VARCHAR(100),
    location_type VARCHAR(20),
    location_detail VARCHAR(200),
    status VARCHAR(20) NOT NULL,
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

CREATE TABLE IF NOT EXISTS asset_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT NOT NULL,
    operation_type VARCHAR(20) NOT NULL,
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

CREATE TABLE IF NOT EXISTS inventory_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(20) NOT NULL UNIQUE,
    task_name VARCHAR(100) NOT NULL,
    scope VARCHAR(50),
    scope_detail VARCHAR(200),
    planned_start_date DATE,
    planned_end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    status VARCHAR(20),
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

CREATE TABLE IF NOT EXISTS inventory_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    asset_code VARCHAR(50),
    planned_date DATE,
    actual_date DATE,
    checker_id BIGINT,
    checker_name VARCHAR(50),
    result VARCHAR(20),
    checked_location VARCHAR(200),
    checked_user_id BIGINT,
    checked_user_name VARCHAR(50),
    first_verify_status VARCHAR(20),
    first_verify_note TEXT,
    first_verify_by VARCHAR(50),
    first_verify_time TIMESTAMP,
    second_verify_status VARCHAR(20),
    second_verify_note TEXT,
    second_verify_by VARCHAR(50),
    second_verify_time TIMESTAMP,
    final_result VARCHAR(20),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(50) NOT NULL UNIQUE,
    supplier_name VARCHAR(100) NOT NULL,
    supplier_type VARCHAR(20),
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    address TEXT,
    business_license VARCHAR(100),
    bank_account VARCHAR(100),
    status VARCHAR(20),
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 权限审计表
CREATE TABLE IF NOT EXISTS permission_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator_id BIGINT NOT NULL,
    operator_name VARCHAR(50),
    target_user_id BIGINT NOT NULL,
    target_user_name VARCHAR(50),
    change_type VARCHAR(20),
    old_roles TEXT,
    new_roles TEXT,
    change_reason VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入默认管理员用户
INSERT INTO "user" (username, password, real_name, role, user_type, role_code, status, department) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '管理员', 'ADMIN', 'MANAGER', 'MGR_SYSTEM', '在职', 'IT部');

-- 插入测试资产数据
INSERT INTO asset (asset_code, asset_name, category, brand, model, serial_number, purchase_date, purchase_amount, location_type, location_detail, status) 
VALUES 
('NB-26-0001', 'MacBook Pro 16寸', 'NB', 'Apple', 'MacBook Pro 16" M3 Max', 'C02ZW1YJMD6T', '2026-01-15', 29999.00, '总部', '总部-1F-仓库', 'IN_STOCK'),
('DS-26-0001', 'Dell显示器 U2723QE', 'DS', 'Dell', 'U2723QE', 'SN123456789', '2026-01-20', 4500.00, '总部', '总部-1F-仓库', 'IN_STOCK'),
('IP-26-0001', 'iPad Pro 12.9寸', 'IP', 'Apple', 'iPad Pro 12.9" M2', 'SN987654321', '2026-02-01', 8999.00, '门店', '深圳店-收银台', 'IN_USE');