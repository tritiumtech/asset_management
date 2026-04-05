-- 测试数据初始化脚本
-- 为每个测试账户分配5件设备 + 创建10件无主设备

-- ============================================
-- 1. 先删除旧数据（确保干净状态）
-- ============================================
DELETE FROM asset_transaction WHERE asset_id IN (SELECT id FROM asset WHERE asset_code LIKE 'NB-2026%' OR asset_code LIKE 'DS-2026%' OR asset_code LIKE 'IP-2026%' OR asset_code LIKE 'MB-2026%' OR asset_code LIKE 'PC-2026%');
DELETE FROM asset WHERE asset_code LIKE 'NB-2026%' OR asset_code LIKE 'DS-2026%' OR asset_code LIKE 'IP-2026%' OR asset_code LIKE 'MB-2026%' OR asset_code LIKE 'PC-2026%';
DELETE FROM "user" WHERE username IN ('asset_admin', 'zhangsan', 'lisi', 'manager1', 'store_mgr');

-- ============================================
-- 2. 更新admin密码为正确格式 (admin123)
-- ============================================
UPDATE "user" SET password = '$2b$12$4ismFIxBAJ6SoZPSRK1mDOyyCkJx8HicBcMQ0xUhmVi/sMoBJ/syS' WHERE username = 'admin';
UPDATE "user" SET real_name = '系统管理员', role_code = 'MGR_SYSTEM', user_type = 'MANAGER', department = 'IT部', status = '在职' WHERE username = 'admin';

-- ============================================
-- 3. 插入测试用户
-- ============================================

-- 资产管理员
INSERT INTO "user" (username, password, real_name, employee_no, role, user_type, role_code, status, department, employee_type, email, phone) 
VALUES ('asset_admin', '$2b$12$4ismFIxBAJ6SoZPSRK1mDOyyCkJx8HicBcMQ0xUhmVi/sMoBJ/syS', '资产管理员', 'EMP002', 'ASSET_ADMIN', 'MANAGER', 'MGR_ASSET_ADMIN', '在职', '行政部', '总部员工', 'asset@company.com', '13800138002');

-- 普通员工1
INSERT INTO "user" (username, password, real_name, employee_no, role, user_type, role_code, status, department, employee_type, email, phone) 
VALUES ('zhangsan', '$2b$12$4ismFIxBAJ6SoZPSRK1mDOyyCkJx8HicBcMQ0xUhmVi/sMoBJ/syS', '张三', 'EMP003', 'USER', 'EMPLOYEE', 'USER_EMPLOYEE', '在职', '销售部', '总部员工', 'zhangsan@company.com', '13800138003');

-- 普通员工2
INSERT INTO "user" (username, password, real_name, employee_no, role, user_type, role_code, status, department, employee_type, email, phone) 
VALUES ('lisi', '$2b$12$4ismFIxBAJ6SoZPSRK1mDOyyCkJx8HicBcMQ0xUhmVi/sMoBJ/syS', '李四', 'EMP004', 'USER', 'EMPLOYEE', 'USER_EMPLOYEE', '在职', '市场部', '总部员工', 'lisi@company.com', '13800138004');

-- 部门经理
INSERT INTO "user" (username, password, real_name, employee_no, role, user_type, role_code, status, department, employee_type, email, phone) 
VALUES ('manager1', '$2b$12$4ismFIxBAJ6SoZPSRK1mDOyyCkJx8HicBcMQ0xUhmVi/sMoBJ/syS', '王经理', 'EMP005', 'MANAGER', 'EMPLOYEE', 'USER_DEPT_MANAGER', '在职', '技术部', '总部员工', 'manager@company.com', '13800138005');

-- 店长
INSERT INTO "user" (username, password, real_name, employee_no, role, user_type, role_code, status, department, employee_type, store_name, email, phone) 
VALUES ('store_mgr', '$2b$12$4ismFIxBAJ6SoZPSRK1mDOyyCkJx8HicBcMQ0xUhmVi/sMoBJ/syS', '刘店长', 'EMP006', 'STORE_MANAGER', 'EMPLOYEE', 'USER_STORE_MANAGER', '在职', '门店部', '门店员工', '深圳旗舰店', 'store@company.com', '13800138006');

-- ============================================
-- 4. 创建测试资产（30件设备供分配）
-- ============================================

-- 笔记本类 (NB)
INSERT INTO asset (asset_code, asset_name, category, sub_category, brand, model, serial_number, purchase_date, purchase_amount, warranty_date, supplier, location_type, location_detail, status, remark) VALUES
('NB-2026-0001', 'MacBook Pro 14寸 M3', 'NB', '笔记本电脑', 'Apple', 'MacBook Pro 14" M3 Pro', 'MPGJ3CH/A-001', '2026-01-15', 16999.00, '2029-01-15', 'Apple官方', '总部', '总部-IT部', 'IN_USE', '高性能开发本'),
('NB-2026-0002', 'MacBook Pro 14寸 M3', 'NB', '笔记本电脑', 'Apple', 'MacBook Pro 14" M3 Pro', 'MPGJ3CH/A-002', '2026-01-15', 16999.00, '2029-01-15', 'Apple官方', '总部', '总部-IT部', 'IN_USE', '高性能开发本'),
('NB-2026-0003', 'ThinkPad X1 Carbon', 'NB', '笔记本电脑', 'Lenovo', 'X1 Carbon Gen 11', '21HM0038CD-001', '2026-02-01', 12999.00, '2029-02-01', '联想官方', '总部', '总部-行政部', 'IN_USE', '商务办公本'),
('NB-2026-0004', 'ThinkPad X1 Carbon', 'NB', '笔记本电脑', 'Lenovo', 'X1 Carbon Gen 11', '21HM0038CD-002', '2026-02-01', 12999.00, '2029-02-01', '联想官方', '总部', '总部-行政部', 'IN_USE', '商务办公本'),
('NB-2026-0005', 'Dell XPS 13 Plus', 'NB', '笔记本电脑', 'Dell', 'XPS 13 Plus 9320', 'XPS9320-001', '2026-02-15', 11999.00, '2029-02-15', 'Dell官方', '总部', '总部-市场部', 'IN_USE', '轻薄本'),
('NB-2026-0006', 'Dell XPS 15', 'NB', '笔记本电脑', 'Dell', 'XPS 15 9530', 'XPS9530-001', '2026-03-01', 15999.00, '2029-03-01', 'Dell官方', '门店', '深圳旗舰店', 'IN_USE', '设计用本'),
('NB-2026-0007', 'MacBook Air 15寸', 'NB', '笔记本电脑', 'Apple', 'MacBook Air 15" M3', 'MQKW3CH/A-001', '2026-03-10', 11999.00, '2029-03-10', 'Apple官方', '门店', '深圳旗舰店', 'IN_USE', '便携办公'),
('NB-2026-0008', '华为MateBook X Pro', 'NB', '笔记本电脑', '华为', 'MateBook X Pro 2024', 'VGHH-32-001', '2026-03-15', 10999.00, '2029-03-15', '华为官方', '总部', '总部-销售部', 'IN_USE', '国产高端本');

-- 显示器类 (DS)
INSERT INTO asset (asset_code, asset_name, category, sub_category, brand, model, serial_number, purchase_date, purchase_amount, warranty_date, supplier, location_type, location_detail, status, remark) VALUES
('DS-2026-0001', 'Dell U2723QE 4K显示器', 'DS', '显示器', 'Dell', 'UltraSharp U2723QE', 'U2723QE-SN001', '2026-01-20', 4599.00, '2029-01-20', 'Dell官方', '总部', '总部-IT部', 'IN_USE', '27寸4K Type-C'),
('DS-2026-0002', 'Dell U2723QE 4K显示器', 'DS', '显示器', 'Dell', 'UltraSharp U2723QE', 'U2723QE-SN002', '2026-01-20', 4599.00, '2029-01-20', 'Dell官方', '总部', '总部-IT部', 'IN_USE', '27寸4K Type-C'),
('DS-2026-0003', 'LG 27UP850N 4K显示器', 'DS', '显示器', 'LG', '27UP850N-W', '27UP850N-001', '2026-02-10', 3299.00, '2029-02-10', 'LG官方', '总部', '总部-技术部', 'IN_USE', '27寸4K IPS'),
('DS-2026-0004', 'LG 27UP850N 4K显示器', 'DS', '显示器', 'LG', '27UP850N-W', '27UP850N-002', '2026-02-10', 3299.00, '2029-02-10', 'LG官方', '总部', '总部-技术部', 'IN_USE', '27寸4K IPS'),
('DS-2026-0005', '小米27寸4K显示器', 'DS', '显示器', '小米', 'XMMNT27NU', 'XM27NU-001', '2026-02-20', 1999.00, '2029-02-20', '小米官方', '总部', '总部-市场部', 'IN_USE', '性价比4K'),
('DS-2026-0006', '小米27寸4K显示器', 'DS', '显示器', '小米', 'XMMNT27NU', 'XM27NU-002', '2026-02-20', 1999.00, '2029-02-20', '小米官方', '门店', '深圳旗舰店', 'IN_USE', '性价比4K');

-- 平板类 (IP)
INSERT INTO asset (asset_code, asset_name, category, sub_category, brand, model, serial_number, purchase_date, purchase_amount, warranty_date, supplier, location_type, location_detail, status, remark) VALUES
('IP-2026-0001', 'iPad Pro 12.9寸 M2', 'IP', '平板电脑', 'Apple', 'iPad Pro 12.9" M2 WiFi', 'MNXQ3CH/A-001', '2026-01-25', 8999.00, '2029-01-25', 'Apple官方', '总部', '总部-销售部', 'IN_USE', '演示用平板'),
('IP-2026-0002', 'iPad Pro 12.9寸 M2', 'IP', '平板电脑', 'Apple', 'iPad Pro 12.9" M2 WiFi', 'MNXQ3CH/A-002', '2026-01-25', 8999.00, '2029-01-25', 'Apple官方', '门店', '深圳旗舰店', 'IN_USE', '门店展示'),
('IP-2026-0003', 'iPad Pro 11寸 M2', 'IP', '平板电脑', 'Apple', 'iPad Pro 11" M2 WiFi', 'MNXD3CH/A-001', '2026-02-05', 6799.00, '2029-02-05', 'Apple官方', '门店', '深圳旗舰店', 'IN_USE', '门店展示'),
('IP-2026-0004', '华为MatePad Pro 13.2', 'IP', '平板电脑', '华为', 'MatePad Pro 13.2"', 'PCE-W30-001', '2026-02-15', 5699.00, '2029-02-15', '华为官方', '总部', '总部-行政部', 'IN_USE', '会议用平板');

-- 手机类 (MB)
INSERT INTO asset (asset_code, asset_name, category, sub_category, brand, model, serial_number, purchase_date, purchase_amount, warranty_date, supplier, location_type, location_detail, status, remark) VALUES
('MB-2026-0001', 'iPhone 15 Pro Max', 'MB', '手机', 'Apple', 'iPhone 15 Pro Max 256G', 'MU2Q3CH/A-001', '2026-01-10', 9999.00, '2029-01-10', 'Apple官方', '总部', '总部-销售部', 'IN_USE', '业务用机'),
('MB-2026-0002', 'iPhone 15 Pro', 'MB', '手机', 'Apple', 'iPhone 15 Pro 256G', 'MTQ83CH/A-001', '2026-01-10', 8999.00, '2029-01-10', 'Apple官方', '总部', '总部-市场部', 'IN_USE', '业务用机'),
('MB-2026-0003', '华为Mate 60 Pro', 'MB', '手机', '华为', 'Mate 60 Pro 512G', 'ALN-AL00-001', '2026-02-01', 6999.00, '2029-02-01', '华为官方', '门店', '深圳旗舰店', 'IN_USE', '门店业务机'),
('MB-2026-0004', '小米14 Pro', 'MB', '手机', '小米', '小米14 Pro 256G', '23116PN5BC-001', '2026-02-20', 4999.00, '2029-02-20', '小米官方', '总部', '总部-技术部', 'IN_USE', '测试用机');

-- 配件类 (PC)
INSERT INTO asset (asset_code, asset_name, category, sub_category, brand, model, serial_number, purchase_date, purchase_amount, warranty_date, supplier, location_type, location_detail, status, remark) VALUES
('PC-2026-0001', 'AirPods Pro 2', 'PC', '耳机', 'Apple', 'AirPods Pro 2nd Gen', 'MTJV3CH/A-001', '2026-01-15', 1899.00, '2028-01-15', 'Apple官方', '总部', '总部-IT部', 'IN_USE', '会议耳机'),
('PC-2026-0002', 'AirPods Pro 2', 'PC', '耳机', 'Apple', 'AirPods Pro 2nd Gen', 'MTJV3CH/A-002', '2026-01-15', 1899.00, '2028-01-15', 'Apple官方', '总部', '总部-技术部', 'IN_USE', '会议耳机'),
('PC-2026-0003', '罗技MX Master 3S', 'PC', '鼠标', 'Logitech', 'MX Master 3S', 'MR0071-001', '2026-02-01', 699.00, '2028-02-01', '罗技官方', '总部', '总部-IT部', 'IN_USE', '办公鼠标'),
('PC-2026-0004', '罗技MX Keys S', 'PC', '键盘', 'Logitech', 'MX Keys S', '920-011559-001', '2026-02-01', 899.00, '2028-02-01', '罗技官方', '总部', '总部-IT部', 'IN_USE', '办公键盘'),
('PC-2026-0005', '绿联Type-C扩展坞', 'PC', '扩展坞', '绿联', 'CM179 九合一', 'CM179-001', '2026-02-10', 299.00, '2028-02-10', '绿联官方', '总部', '总部-行政部', 'IN_USE', '接口扩展');

-- ============================================
-- 5. 分配资产给测试用户（每人5件）
-- ============================================

-- 管理员(admin) - 5件：笔记本+显示器+平板+手机+配件
UPDATE asset SET 
    current_user_id = (SELECT id FROM "user" WHERE username = 'admin'),
    current_user_name = (SELECT real_name FROM "user" WHERE username = 'admin'),
    department = (SELECT department FROM "user" WHERE username = 'admin'),
    employee_type = (SELECT employee_type FROM "user" WHERE username = 'admin'),
    assign_date = '2026-01-20',
    status = 'IN_USE'
WHERE asset_code IN ('NB-2026-0001', 'DS-2026-0001', 'IP-2026-0001', 'MB-2026-0001', 'PC-2026-0001');

-- 资产管理员(asset_admin) - 5件
UPDATE asset SET 
    current_user_id = (SELECT id FROM "user" WHERE username = 'asset_admin'),
    current_user_name = (SELECT real_name FROM "user" WHERE username = 'asset_admin'),
    department = (SELECT department FROM "user" WHERE username = 'asset_admin'),
    employee_type = (SELECT employee_type FROM "user" WHERE username = 'asset_admin'),
    assign_date = '2026-02-01',
    status = 'IN_USE'
WHERE asset_code IN ('NB-2026-0003', 'DS-2026-0003', 'IP-2026-0004', 'PC-2026-0003', 'PC-2026-0005');

-- 张三(zhangsan) - 5件
UPDATE asset SET 
    current_user_id = (SELECT id FROM "user" WHERE username = 'zhangsan'),
    current_user_name = (SELECT real_name FROM "user" WHERE username = 'zhangsan'),
    department = (SELECT department FROM "user" WHERE username = 'zhangsan'),
    employee_type = (SELECT employee_type FROM "user" WHERE username = 'zhangsan'),
    assign_date = '2026-02-15',
    status = 'IN_USE'
WHERE asset_code IN ('NB-2026-0005', 'DS-2026-0005', 'MB-2026-0002', 'PC-2026-0002', 'PC-2026-0004');

-- 李四(lisi) - 5件
UPDATE asset SET 
    current_user_id = (SELECT id FROM "user" WHERE username = 'lisi'),
    current_user_name = (SELECT real_name FROM "user" WHERE username = 'lisi'),
    department = (SELECT department FROM "user" WHERE username = 'lisi'),
    employee_type = (SELECT employee_type FROM "user" WHERE username = 'lisi'),
    assign_date = '2026-02-20',
    status = 'IN_USE'
WHERE asset_code IN ('NB-2026-0002', 'DS-2026-0002', 'IP-2026-0002', 'MB-2026-0004', 'PC-2026-0001');

-- 王经理(manager1) - 5件
UPDATE asset SET 
    current_user_id = (SELECT id FROM "user" WHERE username = 'manager1'),
    current_user_name = (SELECT real_name FROM "user" WHERE username = 'manager1'),
    department = (SELECT department FROM "user" WHERE username = 'manager1'),
    employee_type = (SELECT employee_type FROM "user" WHERE username = 'manager1'),
    assign_date = '2026-03-01',
    status = 'IN_USE'
WHERE asset_code IN ('NB-2026-0004', 'DS-2026-0004', 'IP-2026-0003', 'MB-2026-0003', 'PC-2026-0003');

-- 刘店长(store_mgr) - 5件
UPDATE asset SET 
    current_user_id = (SELECT id FROM "user" WHERE username = 'store_mgr'),
    current_user_name = (SELECT real_name FROM "user" WHERE username = 'store_mgr'),
    department = (SELECT department FROM "user" WHERE username = 'store_mgr'),
    employee_type = (SELECT employee_type FROM "user" WHERE username = 'store_mgr'),
    assign_date = '2026-03-10',
    status = 'IN_USE'
WHERE asset_code IN ('NB-2026-0006', 'NB-2026-0007', 'DS-2026-0006', 'IP-2026-0002', 'MB-2026-0003');

-- ============================================
-- 6. 创建10件无主设备（IN_STOCK状态）
-- ============================================

INSERT INTO asset (asset_code, asset_name, category, sub_category, brand, model, serial_number, purchase_date, purchase_amount, warranty_date, supplier, location_type, location_detail, status, remark) VALUES
('NB-2026-0101', 'MacBook Pro 16寸 M3 Max', 'NB', '笔记本电脑', 'Apple', 'MacBook Pro 16" M3 Max', 'MUW63CH/A-101', '2026-03-20', 29999.00, '2029-03-20', 'Apple官方', '总部', '总部-仓库-A区', 'IN_STOCK', '库存备用-高性能'),
('NB-2026-0102', 'ThinkPad T14s', 'NB', '笔记本电脑', 'Lenovo', 'ThinkPad T14s Gen 4', '21F6003TCD-101', '2026-03-20', 8999.00, '2029-03-20', '联想官方', '总部', '总部-仓库-A区', 'IN_STOCK', '库存备用-商务'),
('DS-2026-0101', 'Dell U3223QE 32寸4K', 'DS', '显示器', 'Dell', 'UltraSharp U3223QE', 'U3223QE-SN101', '2026-03-22', 5999.00, '2029-03-22', 'Dell官方', '总部', '总部-仓库-B区', 'IN_STOCK', '库存备用-32寸旗舰'),
('DS-2026-0102', '明基PD2706U 27寸4K', 'DS', '显示器', '明基', 'PD2706U', 'PD2706U-101', '2026-03-22', 3699.00, '2029-03-22', '明基官方', '总部', '总部-仓库-B区', 'IN_STOCK', '库存备用-设计专用'),
('IP-2026-0101', 'iPad Air 11寸 M2', 'IP', '平板电脑', 'Apple', 'iPad Air 11" M2', 'MUWG3CH/A-101', '2026-03-25', 4799.00, '2029-03-25', 'Apple官方', '总部', '总部-仓库-C区', 'IN_STOCK', '库存备用-中端平板'),
('IP-2026-0102', '华为MatePad 11.5S', 'IP', '平板电脑', '华为', 'MatePad 11.5S', 'TGR-W00-101', '2026-03-25', 2299.00, '2029-03-25', '华为官方', '总部', '总部-仓库-C区', 'IN_STOCK', '库存备用-性价比平板'),
('MB-2026-0101', 'iPhone 15 Plus', 'MB', '手机', 'Apple', 'iPhone 15 Plus 256G', 'MU093CH/A-101', '2026-03-28', 7999.00, '2029-03-28', 'Apple官方', '总部', '总部-仓库-D区', 'IN_STOCK', '库存备用-大屏机'),
('MB-2026-0102', 'vivo X100 Pro', 'MB', '手机', 'vivo', 'X100 Pro 256G', 'V2309-101', '2026-03-28', 4999.00, '2029-03-28', 'vivo官方', '总部', '总部-仓库-D区', 'IN_STOCK', '库存备用-影像旗舰'),
('PC-2026-0101', '索尼WH-1000XM5', 'PC', '耳机', '索尼', 'WH-1000XM5', 'WH1000XM5-101', '2026-03-30', 2499.00, '2028-03-30', '索尼官方', '总部', '总部-仓库-E区', 'IN_STOCK', '库存备用-降噪耳机'),
('PC-2026-0102', 'Keychron K3 Pro', 'PC', '键盘', 'Keychron', 'K3 Pro 矮轴', 'K3P-H1-101', '2026-03-30', 599.00, '2028-03-30', 'Keychron官方', '总部', '总部-仓库-E区', 'IN_STOCK', '库存备用-机械键盘');

-- ============================================
-- 7. 创建资产流转记录（领用记录）
-- ============================================

INSERT INTO asset_transaction (asset_id, operation_type, operation_date, operator_id, operator_name, to_user_id, to_user_name, to_location, description) 
SELECT 
    a.id,
    'ASSIGN',
    a.assign_date,
    1,
    '系统初始化',
    a.current_user_id,
    a.current_user_name,
    a.location_detail,
    '测试数据初始化-资产分配'
FROM asset a 
WHERE a.current_user_id IS NOT NULL AND a.assign_date IS NOT NULL;

-- 验证数据
SELECT '用户数量' as item, COUNT(*) as count FROM "user" WHERE deleted = 0
UNION ALL
SELECT '资产总数', COUNT(*) FROM asset WHERE deleted = 0
UNION ALL
SELECT '已分配资产', COUNT(*) FROM asset WHERE deleted = 0 AND current_user_id IS NOT NULL
UNION ALL
SELECT '库存资产', COUNT(*) FROM asset WHERE deleted = 0 AND status = 'IN_STOCK'
UNION ALL
SELECT '流转记录', COUNT(*) FROM asset_transaction;