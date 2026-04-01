# 资产管理系统 - 产品需求文档 (PRD)

**文档版本**: v1.0  
**创建日期**: 2026-04-01  
**文档状态**: 评审中  
**适用范围**: 总部 + 门店全场景资产管理

---

## 一、项目概述

### 1.1 背景
公司资产分布在总部及各门店，涉及IT设备（主机、显示器、笔记本、iPad等）及消耗性物料。当前管理存在以下痛点：
- 资产信息分散，盘点困难
- 入离职资产调拨不及时，造成资产流失
- 盘点周期长，核实流程不规范
- 门店与总部资产流转缺乏追踪

### 1.2 目标
- **全面数字化**: 所有资产条码/二维码管理，扫码可查
- **流程规范化**: 入离职自动触发资产调拨，盘点流程标准化
- **实时可视化**: 随时掌握资产分布、状态、责任人
- **高效盘点**: 支持移动端扫码盘点，两轮核实机制

### 1.3 适用范围
| 场景 | 说明 |
|------|------|
| **总部员工** | 办公IT设备、公共设备 |
| **门店员工** | 门店专用设备、收银设备 |
| **入离职** | 自动触发资产回收/分配流程 |
| **定期盘点** | 每6个月全量盘点 |

---

## 二、资产分类体系

### 2.1 IT设备类

| 一级分类 | 二级分类 | 管理属性 | 示例 |
|----------|----------|----------|------|
| **主机类** | 台式主机 | SN码、配置、位置 | 办公主机、服务器 |
| | 笔记本 | SN码、配置、责任人 | MacBook、ThinkPad |
| | iPad/平板 | SN码、配件、责任人 | iPad Pro、华为平板 |
| **显示类** | 显示器 | 尺寸、接口、位置 | 24寸显示器、4K屏 |
| | 投影仪 | 灯泡时长、位置 | 会议室投影仪 |
| **外设类** | 键盘鼠标 | 有线/无线、库存量 | 机械键盘、鼠标 |
| | 耳机/耳麦 | 类型、库存量 | 头戴式、入耳式 |
| | 转接线/配件 | 接口类型、库存量 | HDMI线、Type-C扩展坞 |
| **网络类** | 路由器 | IP、位置、配置 | 门店路由器 |
| | 交换机 | 端口数、位置 | 办公交换机 |
| | AP设备 | 覆盖区域、位置 | 无线AP |
| **收银类** | POS机 | SN码、门店绑定 | 收银终端 |
| | 扫码枪 | 型号、门店绑定 | 条码扫描器 |
| | 钱箱 | 规格、门店绑定 | 收银钱箱 |

### 2.2 消耗性物料

| 分类 | 管理属性 | 预警机制 |
|------|----------|----------|
| 打印耗材 | 型号、适用设备、库存量 | 低于安全库存预警 |
| 线材耗材 | 类型、长度、库存量 | 低于安全库存预警 |
| 清洁用品 | 类型、库存量 | 低于安全库存预警 |
| 办公文具 | 类型、库存量 | 按需申领 |

### 2.3 资产状态定义

| 状态 | 说明 | 流转条件 |
|------|------|----------|
| **库存** | 在库可用 | 新购入库、归还入库 |
| **在用** | 已分配给使用人 | 领用审批通过 |
| **调拨中** | 正在流转过程中 | 调拨申请已提交 |
| **维修中** | 送修或待维修 | 报修申请通过 |
| **报废** | 已报废待处理 | 报废审批通过 |
| **盘亏** | 盘点发现缺失 | 盘点核实确认 |

---

## 三、资产编码规范

### 3.1 编码规则

**格式**: `类别代码-年份-流水号`

| 代码段 | 说明 | 示例 |
|--------|------|------|
| 类别代码 | 2位字母 | PC=主机/NB=笔记本/IP=iPad/DS=显示器/PE=外设/NT=网络/PO=收银/MAT=物料 |
| 年份 | 2位数字 | 26=2026年 |
| 流水号 | 4位数字 | 0001-9999 |

**示例**: `NB-26-0001` = 2026年入库的第1台笔记本

### 3.2 条码/二维码规范

**条码**: Code128 标准，支持扫码枪识别
**二维码**: 包含资产编号+资产名称+当前责任人信息

**标签规格**:
- IT设备: 60mm×40mm 不干胶标签
- 消耗物料: 40mm×30mm 标签
- 材质: 防水防刮，耐高温

---

## 四、核心业务流程

### 4.1 资产入库流程

```
采购到货 → 资产管理员验收 → 贴标赋码 → 录入系统 → 入库完成
```

**关键字段录入**:
- 资产编号（自动生成/扫码录入）
- 资产类别、品牌、型号
- SN码（设备唯一标识）
- 购置日期、金额、供应商
- 保修期限
- 存放位置（总部/具体门店）
- 初始照片（设备正面+SN码）

### 4.2 资产领用流程

**总部员工领用**:
```
员工申请 → 部门负责人审批 → 资产管理员确认 → 领取设备 → 更新台账
```

**门店员工领用**:
```
店长申请 → 区域经理审批 → 资产管理员确认 → 配送/领取 → 更新台账
```

**审批节点**:
| 节点 | 处理人 | 时效要求 |
|------|--------|----------|
| 提交申请 | 使用人/店长 | - |
| 部门审批 | 部门负责人/区域经理 | 2工作日 |
| 资产确认 | 资产管理员 | 1工作日 |
| 领取确认 | 双方扫码确认 | 即时 |

### 4.3 入离职资产调拨流程

**入职资产分配**:
```
HR发起入职流程 → 资产管理员查看岗位配置 → 分配设备 → 员工签收
```

**离职资产回收**:
```
HR发起离职流程 → 资产管理员查看名下资产 → 发起回收 → 员工归还 → 验收入库
```

**关键机制**:
- HR系统与资产系统联动，入离职自动触发
- 离职流程中资产未结清，系统阻塞离职审批
- 资产损坏需定损，明确赔偿金额

### 4.4 资产调拨流程（跨门店/部门）

```
调出方申请 → 调入方确认 → 双方负责人审批 → 资产管理员审核 → 实物调拨 → 扫码确认
```

**调拨类型**:
- 门店间调拨
- 门店调回总部
- 总部调拨门店
- 部门间调拨

### 4.5 资产维修流程

```
使用人报修 → 资产管理员评估 → 送修/现场维修 → 维修完成 → 验收归还
```

**维修记录**:
- 故障描述
- 维修时间、费用
- 维修商信息
- 更换配件记录
- 维修后照片

### 4.6 资产报废流程

```
资产管理员发起 → 部门负责人审批 → 财务确认 → 实物处置 → 系统销账
```

**报废条件**:
- 使用年限超过规定期限
- 维修成本超过残值
- 技术淘汰无法使用
- 损坏无法修复

---

## 五、资产盘点流程（核心）

### 5.1 盘点周期与范围

- **周期**: 每6个月一次
- **时间**: 每年6月、12月
- **范围**: 全部在册资产（库存+在用）
- **负责人**: 资产管理员牵头，各部门/门店配合

### 5.2 盘点流程

```
发起盘点 → 生成盘点任务 → 扫码盘点 → 第一轮核实 → 第二轮确认 → 生成盘点报告
```

### 5.3 盘点执行细节

**Step 1: 发起盘点**
- 资产管理员创建盘点计划
- 系统自动生成盘点清单（按部门/门店分组）
- 通知相关责任人准备

**Step 2: 扫码盘点**
- 盘点人员使用手机/扫码枪扫描资产条码
- 系统自动核对资产信息
- 实时显示盘点进度

**盘点结果标记**:
| 结果 | 说明 | 操作 |
|------|------|------|
| **正常** | 资产在册，信息一致 | 确认无误 |
| **位置变更** | 资产在，位置变动 | 更新位置信息 |
| **责任人变更** | 资产在，使用人变动 | 更新责任人 |
| **盘亏** | 资产找不到 | 标记盘亏，进入核实流程 |
| **盘盈** | 发现未入账资产 | 标记盘盈，补录入账 |

**Step 3: 第一轮核实（3个工作日内）**
- 对盘亏资产，责任人需提供说明
- 可能情况：借用未登记、存放位置变更、已报废未销账
- 核实后更新盘点状态

**Step 4: 第二轮确认（5个工作日内）**
- 对仍无法核实的盘亏资产，升级处理
- 部门负责人介入调查
- 明确资产去向或确认损失

**Step 5: 盘点报告**
- 自动生成盘点统计报表
- 盘亏资产清单及处理建议
- 责任人确认签字
- 财务销账处理

### 5.4 盘点报表

| 报表类型 | 内容 |
|----------|------|
| **盘点总览** | 应盘数量、实盘数量、盘亏数量、盘盈数量 |
| **部门盘点表** | 按部门/门店统计盘点结果 |
| **盘亏资产明细** | 资产编号、名称、原责任人、账面价值、核实状态 |
| **盘点差异分析** | 差异原因分类统计 |

---

## 六、系统功能设计

### 6.1 多维表格结构设计

**主表：资产台账**

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| 资产编号 | 文本 | ✅ | 唯一标识，条码/二维码内容 |
| 资产名称 | 文本 | ✅ | 显示名称 |
| 资产类别 | 单选 | ✅ | 主机/笔记本/iPad/显示器/外设/网络/收银/物料 |
| 二级分类 | 单选 | | 根据类别动态显示 |
| 品牌 | 文本 | | |
| 型号 | 文本 | | |
| SN码 | 文本 | ✅ | 设备唯一序列号 |
| 购置日期 | 日期 | ✅ | |
| 购置金额 | 数字 | ✅ | 元 |
| 保修期至 | 日期 | | |
| 供应商 | 文本 | | |
| 存放位置类型 | 单选 | ✅ | 总部/门店/仓库 |
| 存放位置详情 | 文本 | ✅ | 具体门店名/楼层/区域 |
| 使用状态 | 单选 | ✅ | 库存/在用/调拨中/维修中/报废/盘亏 |
| 当前责任人 | 人员 | | 飞书用户，库存状态为空 |
| 责任部门 | 文本 | | 部门/门店名称 |
| 员工类型 | 单选 | | 总部员工/门店员工 |
| 领用日期 | 日期 | | |
| 预计归还日期 | 日期 | | 临时借用场景 |
| 资产照片 | 附件 | | 设备照片 |
| 购置凭证 | 附件 | | 发票/收据 |
| 备注 | 文本 | | |
| 创建时间 | 创建时间 | ✅ | 自动 |
| 更新时间 | 修改时间 | ✅ | 自动 |

**从表：流转记录**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| 关联资产 | 关联 | 关联资产台账 |
| 操作类型 | 单选 | 入库/领用/归还/调拨/维修/报废/盘点调整 |
| 操作日期 | 日期 | |
| 操作人 | 人员 | |
| 原责任人 | 人员 | |
| 目标责任人 | 人员 | |
| 原位置 | 文本 | |
| 目标位置 | 文本 | |
| 审批单号 | 文本 | 关联审批流程 |
| 操作说明 | 文本 | |
| 附件 | 附件 | 交接单照片等 |

**从表：盘点记录**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| 盘点批次 | 文本 | 如"2026H1" |
| 关联资产 | 关联 | 关联资产台账 |
| 计划盘点日期 | 日期 | |
| 实际盘点日期 | 日期 | |
| 盘点人 | 人员 | |
| 盘点结果 | 单选 | 正常/位置变更/责任人变更/盘亏/盘盈 |
| 第一轮核实状态 | 单选 | 待核实/已核实/待确认 |
| 第一轮核实说明 | 文本 | |
| 第二轮确认状态 | 单选 | 待确认/已确认 |
| 第二轮确认说明 | 文本 | |
| 最终处理结果 | 单选 | 无差异/已调整/报损/补录 |

**从表：维修记录**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| 关联资产 | 关联 | 关联资产台账 |
| 报修日期 | 日期 | |
| 故障描述 | 文本 | |
| 送修日期 | 日期 | |
| 维修商 | 文本 | |
| 维修费用 | 数字 | |
| 更换配件 | 文本 | |
| 完成日期 | 日期 | |
| 维修结果 | 单选 | 已修复/无法修复/报废 |
| 验收人 | 人员 | |

### 6.2 飞书审批流程模板

**模板1：资产领用申请**

| 字段 | 类型 | 说明 |
|------|------|------|
| 申请人 | 人员 | 自动 |
| 申请日期 | 日期 | 自动 |
| 申请类型 | 单选 | 新员工/设备更新/临时借用 |
| 领用资产 | 关联 | 从库存资产中选择 |
| 资产编号 | 文本 | 自动带出 |
| 领用事由 | 文本 | |
| 期望领用日期 | 日期 | |
| 审批人 | 人员 | 部门负责人 |
| 资产管理员确认 | 人员 | |

**模板2：资产调拨申请**

| 字段 | 类型 | 说明 |
|------|------|------|
| 调出方 | 文本 | |
| 调入方 | 文本 | |
| 调拨资产 | 关联 | |
| 调拨原因 | 文本 | |
| 调出方负责人 | 人员 | |
| 调入方负责人 | 人员 | |
| 资产管理员审核 | 人员 | |

**模板3：资产报修申请**

| 字段 | 类型 | 说明 |
|------|------|------|
| 报修资产 | 关联 | |
| 故障现象 | 文本 | |
| 故障照片 | 附件 | |
| 期望处理方式 | 单选 | 现场维修/送修/更换 |

**模板4：资产报废申请**

| 字段 | 类型 | 说明 |
|------|------|------|
| 报废资产 | 关联 | |
| 报废原因 | 单选 | 到期/损坏/技术淘汰 |
| 资产状况 | 文本 | |
| 残值评估 | 数字 | |

**模板5：离职资产回收确认**

| 字段 | 类型 | 说明 |
|------|------|------|
| 离职员工 | 人员 | HR系统自动带出 |
| 名下资产清单 | 关联表格 | 自动列出 |
| 资产归还状态 | 单选 | 全部归还/部分缺失/待赔偿 |
| 缺失资产说明 | 文本 | |
| 赔偿金额 | 数字 | |
| 资产管理员验收 | 人员 | |
| HR确认 | 人员 | |

---

## 七、权限与角色设计

### 7.1 角色定义

| 角色 | 权限范围 | 数据范围 |
|------|----------|----------|
| **系统管理员** | 全部权限 | 全部资产 |
| **资产管理员** | 台账维护、审批处理、盘点管理 | 全部资产 |
| **财务专员** | 查看金额字段、审批报废 | 全部资产 |
| **区域经理** | 审批门店资产申请、查看本区域资产 | 所辖门店 |
| **部门负责人** | 审批部门资产申请、查看本部门资产 | 本部门 |
| **店长** | 管理门店资产、提交申请 | 本门店 |
| **普通员工** | 查看个人资产、提交申请 | 个人相关 |

### 7.2 字段级权限

| 字段 | 普通员工 | 店长/部门负责人 | 资产管理员 | 财务 |
|------|----------|----------------|------------|------|
| 资产基础信息 | 查看 | 查看 | 编辑 | 查看 |
| 购置金额 | 隐藏 | 隐藏 | 查看 | 编辑 |
| SN码 | 查看 | 查看 | 编辑 | 查看 |
| 责任人 | 查看 | 查看范围内编辑 | 编辑 | 查看 |
| 位置信息 | 查看 | 查看范围内编辑 | 编辑 | 查看 |
| 审批记录 | 查看个人 | 查看范围内 | 全部 | 查看 |

---

## 八、报表与看板

### 8.1 管理驾驶舱

**资产总览卡片**:
- 资产总数
- 资产总价值
- 在用资产数
- 库存资产数
- 维修中资产数
- 本月新增资产数

**分布图表**:
- 资产类别分布（饼图）
- 部门/门店资产分布（柱状图）
- 资产状态分布（饼图）
- 资产购置趋势（折线图）

**预警提醒**:
- 本月过保资产
- 待审批申请
- 进行中的盘点任务
- 低库存物料预警

### 8.2 盘点专题看板

- 当前盘点进度
- 盘点完成率（按部门/门店）
- 盘亏资产统计
- 盘点差异分析

### 8.3 个人资产看板

- 我名下的资产
- 待审批的申请
- 待归还的借用资产
- 资产到期提醒

---

## 九、系统集成需求

### 9.1 HR系统对接

**对接场景**:
- 新员工入职 → 自动创建资产分配任务
- 员工离职 → 自动触发资产回收流程
- 员工转岗 → 自动触发资产调拨评估

**数据同步**:
- 员工信息（姓名、部门、岗位、员工类型）
- 入职/离职/转岗事件

### 9.2 财务系统对接

**对接场景**:
- 资产报废 → 同步财务销账
- 月度折旧 → 同步折旧数据
- 资产采购 → 同步采购订单

### 9.3 飞书生态集成

- **多维表格**: 核心数据存储
- **审批**: 流程审批引擎
- **机器人**: 消息通知、查询服务
- **日历**: 盘点任务提醒
- **文档**: 盘点报告、操作手册

---

## 十、实施路线图

### Phase 1: 基础搭建（第1-2周）

**Week 1: 系统配置**
- [ ] 创建多维表格结构
- [ ] 配置资产编码规则
- [ ] 设计条码/二维码模板
- [ ] 配置审批流程模板
- [ ] 设置权限体系

**Week 2: 数据准备**
- [ ] 整理现有资产清单
- [ ] 打印资产标签
- [ ] 粘贴标签并扫码录入
- [ ] 导入历史资产数据
- [ ] 初始化库存数据

### Phase 2: 流程上线（第3-4周）

**Week 3: 流程测试**
- [ ] 领用流程端到端测试
- [ ] 调拨流程测试
- [ ] 维修流程测试
- [ ] 报废流程测试
- [ ] 入离职联动测试

**Week 4: 盘点试点**
- [ ] 选择试点部门/门店
- [ ] 执行首轮盘点
- [ ] 验证两轮核实流程
- [ ] 优化盘点流程
- [ ] 培训资产管理员

### Phase 3: 全面推广（第5-6周）

**Week 5: 全员培训**
- [ ] 总部员工培训
- [ ] 门店店长培训
- [ ] 制作操作手册视频
- [ ] 建立答疑群

**Week 6: 正式上线**
- [ ] 全面启用新流程
- [ ] 旧Excel并行一周
- [ ] 收集反馈并优化
- [ ] 正式上线运营

### Phase 4: 持续优化（第7-8周）

- [ ] 开发智能查询机器人
- [ ] 配置自动化预警
- [ ] 优化盘点效率
- [ ] 数据分析报表
- [ ] 编写最佳实践文档

---

## 十一、待确认事项

1. **资产编码规则**: 是否确认采用 `类别-年份-流水号` 格式？
2. **盘点时间**: 6月和12月的具体盘点启动日期？
3. **HR对接**: 是否有现有HR系统需要对接？还是纯飞书生态？
4. **历史数据**: 现有资产数据量大概多少？是否有Excel可导入？
5. **标签打印**: 是否需要采购标签打印机？现有设备是否支持？
6. **门店范围**: 全国门店数量？是否需要分批次上线？
7. **财务对接**: 是否需要与财务系统对接折旧和销账？

---

## 十二、附录

### 12.1 资产类别代码表

| 代码 | 类别 | 说明 |
|------|------|------|
| PC | 台式主机 | 办公主机、服务器 |
| NB | 笔记本 | MacBook、ThinkPad等 |
| IP | iPad/平板 | iPad、华为平板等 |
| DS | 显示器 | 各类显示器 |
| PJ | 投影仪 | 会议室投影仪 |
| PE | 外设 | 键盘鼠标耳机等 |
| NT | 网络设备 | 路由器交换机AP |
| PO | 收银设备 | POS机扫码枪等 |
| MAT | 物料 | 耗材类 |

### 12.2 盘点计划模板

```
盘点批次: 2026H1
盘点期间: 2026年6月1日 - 6月30日
盘点范围: 全部在册资产
盘点负责人: [资产管理员]

时间安排:
- 6/1-6/5: 盘点准备，生成盘点清单
- 6/6-6/20: 现场盘点，扫码核查
- 6/21-6/25: 第一轮核实
- 6/26-6/30: 第二轮确认，生成报告

各部门/门店盘点时间表:
[具体安排...]
```

---

## 附录B: 技术规格（更新）

### B.1 技术栈选型

| 层级 | 技术选型 | 版本 | 说明 |
|------|----------|------|------|
| **后端框架** | Spring Boot | 3.2.0 | Java 17 + Spring Boot 3.x |
| **持久层** | MyBatis-Plus | 3.5.5 | 简化CRUD操作 |
| **数据库** | MySQL | 8.0+ | 主数据库 |
| **缓存** | Redis | 6.x | 缓存和会话存储 |
| **安全** | Spring Security + JWT | - | 认证授权 |
| **前端框架** | Vue 3 | 3.4.0 | Composition API |
| **前端构建** | Vite | 5.0 | 快速构建工具 |
| **移动端UI** | Vant 4 | 4.8.0 | 移动端组件库 |
| **状态管理** | Pinia | 2.1.0 | Vue 3状态管理 |
| **路由** | Vue Router | 4.2.0 | 前端路由 |
| **HTTP客户端** | Axios | 1.6.0 | API请求 |
| **条码/二维码** | ZXing + JsBarcode | - | 码制生成和识别 |

### B.2 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Vue 3     │  │   Vant 4    │  │  移动端优化UI        │  │
│  │  Composition│  │  组件库      │  │  (盘点扫描界面)      │  │
│  │    API      │  │             │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST API
┌────────────────────────▼────────────────────────────────────┐
│                      后端层                                   │
│  ┌─────────────────────────────────────────────────────┐     │
│  │              Spring Boot 3.2                        │     │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐           │     │
│  │  │Controller│ │ Service  │ │  Mapper  │           │     │
│  │  │   层     │ │   层     │ │   层     │           │     │
│  │  └──────────┘ └──────────┘ └──────────┘           │     │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐           │     │
│  │  │Spring    │ │  JWT     │ │ MyBatis- │           │     │
│  │  │Security  │ │ 认证     │ │  Plus    │           │     │
│  │  └──────────┘ └──────────┘ └──────────┘           │     │
│  └─────────────────────────────────────────────────────┘     │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│    MySQL     │ │    Redis     │ │    文件存储   │
│   (主数据)    │ │  (缓存/会话)  │ │  (图片/附件)  │
└──────────────┘ └──────────────┘ └──────────────┘
```

### B.3 后端项目结构

```
asset-management-backend/
├── pom.xml                         # Maven配置
├── src/
│   ├── main/
│   │   ├── java/com/drv/assetmanagement/
│   │   │   ├── AssetManagementApplication.java
│   │   │   ├── config/             # 配置类
│   │   │   ├── controller/         # 控制器层
│   │   │   ├── dto/                # 数据传输对象
│   │   │   ├── entity/             # 实体类
│   │   │   ├── enums/              # 枚举类
│   │   │   ├── mapper/             # MyBatis Mapper
│   │   │   ├── repository/         # 数据访问层
│   │   │   ├── service/            # 服务层
│   │   │   │   └── impl/           # 服务实现
│   │   │   ├── security/           # 安全配置
│   │   │   └── utils/              # 工具类
│   │   └── resources/
│   │       ├── application.yml     # 应用配置
│   │       └── db/
│   │           └── schema.sql      # 数据库脚本
│   └── test/                       # 测试代码
```

### B.4 前端项目结构

```
asset-management-frontend/
├── index.html                      # 入口HTML
├── package.json                    # npm配置
├── vite.config.js                  # Vite配置
└── src/
    ├── main.js                     # 入口JS
    ├── App.vue                     # 根组件
    ├── router/
    │   └── index.js                # 路由配置
    ├── api/
    │   └── index.js                # API接口封装
    ├── stores/                     # Pinia状态管理
    ├── utils/                      # 工具函数
    └── views/                      # 页面视图
        ├── Home.vue                # 首页
        ├── Assets.vue              # 资产列表
        ├── AssetDetail.vue         # 资产详情
        ├── Inventory.vue           # 盘点任务
        ├── InventoryDetail.vue     # 盘点详情
        ├── InventoryScan.vue       # 扫码盘点（核心）
        ├── Mine.vue                # 个人中心
        └── MyAssets.vue            # 我的资产
```

### B.5 移动端盘点界面设计

**核心特性**:
- **大按钮设计**: 扫码按钮占据屏幕主要区域，便于点击
- **振动反馈**: 扫码成功时触发设备振动
- **离线支持**: 网络不稳定时可缓存盘点数据
- **快速输入**: 支持手动输入资产编号作为备用方案
- **滑动操作**: 已盘点记录支持左滑编辑
- **实时进度**: 顶部进度条实时显示盘点完成度

### B.6 数据库脚本位置

- **建表脚本**: `asset-management-backend/src/main/resources/db/schema.sql`
- **完整ER图**: 见设计文档

---

## 十四、用户分类与角色管理体系（新增）

### 14.1 用户分类架构

系统用户分为**三大类**，每类包含不同的角色：

```
┌─────────────────────────────────────────────────────────┐
│                    资产管理系统用户体系                   │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐ │
│  │  供应商     │  │ 设备使用者  │  │   设备管理者    │ │
│  │  (外部)     │  │  (内部)     │  │    (内部)       │ │
│  └──────┬──────┘  └──────┬──────┘  └────────┬────────┘ │
│         │                │                   │          │
│    ┌────┼────┐          │            ┌──────┼──────┐   │
│    ↓    ↓    ↓          ↓            ↓      ↓      ↓   │
│  出售方 出租方 维修方   员工      资产管理员 调拨者 盘点者 │
└─────────────────────────────────────────────────────────┘
```

### 14.2 用户分类定义

#### 第一类：供应商（外部用户）

| 角色 | 角色编码 | 定义 | 主要权限 |
|------|----------|------|----------|
| **设备出售方** | SUPPLIER_SELLER | 向公司提供资产采购服务的供应商 | 查看订单、确认发货、上传发票 |
| **出租方** | SUPPLIER_RENTER | 提供资产租赁服务的供应商 | 查看租赁合同、确认租期、发起续租/退租 |
| **维修方** | SUPPLIER_MAINTAINER | 提供资产维修服务的供应商 | 接收报修单、反馈维修进度、提交维修报告 |

**供应商通用权限**:
- ✅ 查看与自己相关的资产/订单信息
- ✅ 上传相关文档（发票、合同、维修报告）
- ✅ 接收系统通知（订单、报修）
- ❌ 查看其他供应商信息
- ❌ 查看公司内部资产明细
- ❌ 操作资产状态变更

#### 第二类：设备使用者（内部用户）

| 角色 | 角色编码 | 定义 | 主要权限 |
|------|----------|------|----------|
| **普通员工** | USER_EMPLOYEE | 使用公司资产的普通员工 | 查看个人资产、申请领用/归还/报修 |
| **店长** | USER_STORE_MANAGER | 门店负责人 | 查看门店资产、审批门店员工申请 |
| **部门负责人** | USER_DEPT_MANAGER | 总部部门负责人 | 查看部门资产、审批部门员工申请 |

**设备使用者权限矩阵**:

| 功能 | 普通员工 | 店长 | 部门负责人 |
|------|----------|------|------------|
| 查看个人名下资产 | ✅ | ✅ | ✅ |
| 申请领用资产 | ✅ | ✅ | ✅ |
| 申请归还资产 | ✅ | ✅ | ✅ |
| 申请报修资产 | ✅ | ✅ | ✅ |
| 查看下属资产 | ❌ | ✅ | ✅ |
| 审批下属申请 | ❌ | ✅ | ✅ |
| 发起调拨申请 | ❌ | ✅ | ✅ |

#### 第三类：设备管理者（内部用户）

| 角色 | 角色编码 | 定义 | 主要权限 |
|------|----------|------|----------|
| **资产管理员** | MGR_ASSET_ADMIN | 资产管理核心人员，负责资产全生命周期 | 资产CRUD、审批所有流程、管理供应商 |
| **调拨者** | MGR_TRANSFER | 负责资产调拨的执行人员 | 执行调拨操作、确认物流、更新位置 |
| **盘点者** | MGR_INVENTORY | 负责资产盘点的执行人员 | 创建盘点任务、扫码盘点、核实差异 |
| **财务专员** | MGR_FINANCE | 负责资产财务相关事务 | 查看金额、审批报废、核对折旧 |
| **系统管理员** | MGR_SYSTEM | 系统维护人员 | 用户管理、权限配置、系统设置 |

**设备管理者权限矩阵**:

| 功能 | 资产管理员 | 调拨者 | 盘点者 | 财务专员 | 系统管理员 |
|------|-----------|--------|--------|----------|-----------|
| 资产台账管理 | ✅ | ❌ | ❌ | 查看 | ❌ |
| 执行资产调拨 | ✅ | ✅ | ❌ | ❌ | ❌ |
| 创建/执行盘点 | ✅ | ❌ | ✅ | ❌ | ❌ |
| 审批领用/调拨 | ✅ | ❌ | ❌ | ❌ | ❌ |
| 审批报废 | ✅ | ❌ | ❌ | ✅ | ❌ |
| 查看购置金额 | ✅ | ❌ | ❌ | ✅ | ❌ |
| 管理供应商 | ✅ | ❌ | ❌ | ❌ | ❌ |
| 用户权限管理 | ❌ | ❌ | ❌ | ❌ | ✅ |
| 系统配置 | ❌ | ❌ | ❌ | ❌ | ✅ |

### 14.3 角色层级与数据范围

#### 数据范围控制

| 角色 | 数据范围 | 说明 |
|------|----------|------|
| 系统管理员 | 全部 | 可查看所有数据 |
| 资产管理员 | 全部 | 可查看所有资产数据 |
| 财务专员 | 全部 | 可查看所有资产的金额信息 |
| 调拨者/盘点者 | assigned | 只能查看分配给自己的任务 |
| 部门负责人 | 本部门 | 只能查看本部门资产和人员 |
| 店长 | 本门店 | 只能查看本门店资产和人员 |
| 普通员工 | 个人 | 只能查看自己名下的资产 |
| 供应商 | 关联订单 | 只能查看与自己相关的订单信息 |

#### 角色继承关系

```
系统管理员
    └── 资产管理员
            ├── 调拨者
            ├── 盘点者
            └── 财务专员
    
部门负责人
    └── 普通员工

店长
    └── 普通员工
```

### 14.4 权限编码规范

采用 **RBAC（基于角色的访问控制）** 模型，权限编码格式：

```
[资源]:[操作]:[范围]

示例：
- asset:read:self      # 查看个人资产
- asset:read:dept      # 查看部门资产
- asset:read:all       # 查看所有资产
- asset:write:all      # 编辑所有资产
- process:approve:dept # 审批部门流程
- inventory:execute:assigned # 执行分配的盘点任务
```

### 14.5 特殊权限场景

#### 场景1：跨部门调拨
- **发起方**：部门负责人/资产管理员
- **接收方**：目标部门负责人审批
- **执行方**：调拨者执行

#### 场景2：门店报修
- **发起方**：店长/普通员工
- **审批方**：资产管理员
- **执行方**：维修方（外部供应商）

#### 场景3：盘点差异核实
- **盘点方**：盘点者发现差异
- **核实方**：资产管理员 + 资产责任人（多轮沟通）
- **结案方**：资产管理员

### 14.6 供应商角色详细设计

#### 设备出售方 (SUPPLIER_SELLER)

| 功能模块 | 权限 |
|----------|------|
| 采购订单 | 查看分配的订单、确认发货、上传发票 |
| 资产信息 | 查看订单关联的资产信息（仅型号数量） |
| 消息通知 | 接收订单通知、提醒 |
| 文档管理 | 上传合同、发票、保修卡 |

**工作流接入**：
- 可作为采购审批流的末端节点
- 审批通过后，供应商收到发货通知
- 供应商确认发货，系统自动更新资产状态

#### 出租方 (SUPPLIER_RENTER)

| 功能模块 | 权限 |
|----------|------|
| 租赁合同 | 查看租赁合同、确认起租/退租 |
| 资产信息 | 查看租赁资产状态 |
| 续租管理 | 发起续租申请、确认退租 |
| 结算管理 | 查看租金结算单 |

#### 维修方 (SUPPLIER_MAINTAINER)

| 功能模块 | 权限 |
|----------|------|
| 报修工单 | 接收分配的工单、反馈维修进度 |
| 维修记录 | 提交维修报告、更换配件记录 |
| 费用结算 | 提交维修费用、查看结算状态 |

### 14.7 用户组与快捷授权

为方便管理，设置以下**用户组**：

| 用户组 | 包含角色 | 适用场景 |
|--------|----------|----------|
| **资产全权限组** | 资产管理员 + 调拨者 + 盘点者 | IT部门资产管理团队 |
| **门店管理组** | 店长 + 盘点者 | 门店日常管理 |
| **财务审计组** | 财务专员 + 资产管理员（只读） | 财务审计 |
| **外部供应商组** | 所有供应商角色 | 外部合作伙伴 |

### 14.8 权限变更审计

所有权限变更需要记录审计日志：

| 字段 | 说明 |
|------|------|
| 操作人 | 谁变更了权限 |
| 被操作人 | 谁的权限被变更 |
| 变更类型 | 新增/删除/修改 |
| 变更前角色 | 原角色列表 |
| 变更后角色 | 新角色列表 |
| 变更原因 | 变更说明 |
| 变更时间 | 操作时间 |

---

## 附录C: 数据库设计更新

### C.1 用户表扩展

```sql
-- 用户表增加用户类型和角色
ALTER TABLE "user" ADD COLUMN user_type VARCHAR(20) COMMENT '用户类型：SUPPLIER/EMPLOYEE/MANAGER';
ALTER TABLE "user" ADD COLUMN role_code VARCHAR(50) COMMENT '角色编码';
ALTER TABLE "user" ADD COLUMN sub_role VARCHAR(50) COMMENT '子角色：SELLER/RENTER/MAINTAINER';
ALTER TABLE "user" ADD COLUMN data_scope VARCHAR(20) COMMENT '数据范围：ALL/DEPT/STORE/SELF/ASSIGNED';
ALTER TABLE "user" ADD COLUMN supplier_id BIGINT COMMENT '关联供应商ID（外部用户）';
ALTER TABLE "user" ADD COLUMN managed_depts TEXT COMMENT '管理的部门列表（JSON）';
ALTER TABLE "user" ADD COLUMN managed_stores TEXT COMMENT '管理的门店列表（JSON）';
```

### C.2 供应商表

```sql
CREATE TABLE supplier (
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
```

### C.3 权限审计表

```sql
CREATE TABLE permission_audit (
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
```

---

## 十五、系统集成与测试账户（新增）

### 15.1 系统集成架构

```
┌─────────────────────────────────────────────────────────────┐
│                      资产管理系统                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │  SRM集成    │  │  北森HR集成  │  │     测试账户        │ │
│  │  (供应商)   │  │  (组织架构)  │  │   (开发测试用)      │ │
│  └──────┬──────┘  └──────┬──────┘  └────────┬────────────┘ │
│         │                │                   │              │
│         ▼                ▼                   ▼              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │srm_supplier │  │ hr_employee │  │      user           │ │
│  │_sync        │  │ _sync       │  │   (测试标记)        │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 15.2 SRM系统集成（供应商）

#### 集成方式
- **数据同步方向**: SRM → 资产管理系统（单向）
- **同步频率**: 每日凌晨2:00
- **同步方式**: 数据库视图/API/文件导入（可配置）

#### 中间表设计

```sql
-- SRM供应商同步中间表
CREATE TABLE srm_supplier_sync (
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
    
    -- 同步控制字段
    sync_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '同步状态：PENDING/SYNCED/FAILED/IGNORED',
    sync_time TIMESTAMP COMMENT '同步时间',
    sync_error TEXT COMMENT '同步错误信息',
    local_supplier_id BIGINT COMMENT '关联本地供应商ID',
    
    -- 数据变更追踪
    srm_create_time TIMESTAMP COMMENT 'SRM创建时间',
    srm_update_time TIMESTAMP COMMENT 'SRM更新时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_srm_code ON srm_supplier_sync(srm_supplier_code);
CREATE INDEX idx_srm_sync_status ON srm_supplier_sync(sync_status);
CREATE INDEX idx_srm_type ON srm_supplier_sync(supplier_type);
```

#### 同步规则

| 场景 | 处理逻辑 |
|------|----------|
| 新增供应商 | SRM新增 → 中间表标记PENDING → 同步到本地supplier表 |
| 供应商变更 | SRM更新 → 中间表更新 → 同步更新本地supplier表 |
| 供应商停用 | SRM停用 → 本地标记为"暂停"，不删除 |
| 类型映射 | SRM分类 → 映射到SELLER/RENTER/MAINTAINER |

#### SRM供应商类型映射

| SRM分类 | 映射类型 | 说明 |
|---------|----------|------|
| 设备采购供应商 | SELLER | 设备出售方 |
| IT服务供应商 | MAINTAINER | 维修方 |
| 租赁服务商 | RENTER | 出租方 |
| 办公用品供应商 | SELLER | 物料供应商 |

### 15.3 北森HR系统集成（组织架构）

#### 集成方式
- **数据同步方向**: 北森HR → 资产管理系统（单向）
- **同步频率**: 每日凌晨1:00（早于供应商同步）
- **同步内容**: 员工信息、部门架构、入离职状态

#### 中间表设计

```sql
-- 北森HR员工同步中间表
CREATE TABLE hr_employee_sync (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hr_employee_id VARCHAR(50) NOT NULL COMMENT '北森员工ID',
    hr_employee_no VARCHAR(50) COMMENT '员工工号',
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    department_code VARCHAR(50) COMMENT '部门编码',
    department_name VARCHAR(100) COMMENT '部门名称',
    store_code VARCHAR(50) COMMENT '门店编码（门店员工）',
    store_name VARCHAR(100) COMMENT '门店名称',
    position VARCHAR(100) COMMENT '职位',
    employee_type VARCHAR(20) COMMENT '总部员工/门店员工',
    status VARCHAR(20) COMMENT '在职/离职',
    join_date DATE,
    leave_date DATE,
    manager_id VARCHAR(50) COMMENT '上级ID',
    
    -- 同步控制字段
    sync_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '同步状态',
    sync_time TIMESTAMP COMMENT '同步时间',
    sync_error TEXT COMMENT '同步错误信息',
    local_user_id BIGINT COMMENT '关联本地用户ID',
    
    -- 数据变更追踪
    hr_create_time TIMESTAMP,
    hr_update_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 北森部门同步中间表
CREATE TABLE hr_department_sync (
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

-- 索引
CREATE INDEX idx_hr_emp_id ON hr_employee_sync(hr_employee_id);
CREATE INDEX idx_hr_emp_status ON hr_employee_sync(sync_status);
CREATE INDEX idx_hr_dept_code ON hr_department_sync(hr_dept_code);
```

#### 同步规则

| 场景 | 处理逻辑 |
|------|----------|
| 新员工入职 | HR新增 → 中间表PENDING → 创建本地用户（默认USER_EMPLOYEE） |
| 员工离职 | HR标记离职 → 触发资产回收流程 → 禁用本地账户 |
| 部门调动 | HR更新部门 → 更新本地用户部门 → 触发资产调拨检查 |
| 职位变更 | HR更新职位 → 更新本地用户角色（如店长任命） |

#### 角色自动映射

| 北森职位/属性 | 映射角色 | 数据范围 |
|---------------|----------|----------|
| 店长/门店经理 | USER_STORE_MANAGER | 本门店 |
| 部门经理 | USER_DEPT_MANAGER | 本部门 |
| 普通员工 | USER_EMPLOYEE | 个人 |
| IT资产管理员 | MGR_ASSET_ADMIN | 全部 |

### 15.4 测试账户体系

#### 测试账户设计原则
- **标记方式**: 所有测试账户`username`以`test_`前缀
- **禁用机制**: 生产环境通过配置`app.test-accounts.enabled=false`禁用
- **密码统一**: 测试账户使用统一测试密码
- **数据隔离**: 测试数据与生产数据逻辑隔离

#### 测试账户列表

| 用户名 | 密码 | 角色 | 数据范围 | 用途 |
|--------|------|------|----------|------|
| **admin** | admin123 | MGR_SYSTEM | 全部 | 系统管理员 |
| test_asset_admin | test123 | MGR_ASSET_ADMIN | 全部 | 资产管理员测试 |
| test_transfer | test123 | MGR_TRANSFER | 分配任务 | 调拨者测试 |
| test_inventory | test123 | MGR_INVENTORY | 分配任务 | 盘点者测试 |
| test_finance | test123 | MGR_FINANCE | 全部（财务） | 财务专员测试 |
| test_store_manager | test123 | USER_STORE_MANAGER | 测试门店 | 店长测试 |
| test_dept_manager | test123 | USER_DEPT_MANAGER | 测试部门 | 部门负责人测试 |
| test_employee | test123 | USER_EMPLOYEE | 个人 | 普通员工测试 |
| test_supplier_seller | test123 | SUPPLIER_SELLER | 关联订单 | 出售方测试 |
| test_supplier_renter | test123 | SUPPLIER_RENTER | 关联订单 | 出租方测试 |
| test_supplier_maintainer | test123 | SUPPLIER_MAINTAINER | 关联订单 | 维修方测试 |

#### 测试账户初始化SQL

```sql
-- 系统管理员（生产环境保留）
INSERT INTO "user" (username, password, real_name, role, user_type, role_code, data_scope, status, department) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', 'ADMIN', 'MANAGER', 'MGR_SYSTEM', 'ALL', '在职', 'IT部');

-- 测试账户（生产环境禁用）
INSERT INTO "user" (username, password, real_name, role, user_type, role_code, data_scope, status, department, is_test_account) VALUES
-- 设备管理者
('test_asset_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试资产管理员', 'ASSET_ADMIN', 'MANAGER', 'MGR_ASSET_ADMIN', 'ALL', '在职', 'IT部', true),
('test_transfer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试调拨员', 'TRANSFER', 'MANAGER', 'MGR_TRANSFER', 'ASSIGNED', '在职', 'IT部', true),
('test_inventory', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试盘点员', 'INVENTORY', 'MANAGER', 'MGR_INVENTORY', 'ASSIGNED', '在职', 'IT部', true),
('test_finance', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试财务专员', 'FINANCE', 'MANAGER', 'MGR_FINANCE', 'ALL', '在职', '财务部', true),

-- 设备使用者
('test_store_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试店长', 'STORE_MANAGER', 'EMPLOYEE', 'USER_STORE_MANAGER', 'STORE', '在职', '深圳店', true),
('test_dept_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试部门经理', 'DEPT_MANAGER', 'EMPLOYEE', 'USER_DEPT_MANAGER', 'DEPT', '在职', '技术部', true),
('test_employee', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试员工', 'USER', 'EMPLOYEE', 'USER_EMPLOYEE', 'SELF', '在职', '技术部', true),

-- 供应商
('test_supplier_seller', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试出售方', 'SUPPLIER', 'SUPPLIER', 'SUPPLIER_SELLER', 'ORDER', '合作中', null, true),
('test_supplier_renter', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试出租方', 'SUPPLIER', 'SUPPLIER', 'SUPPLIER_RENTER', 'ORDER', '合作中', null, true),
('test_supplier_maintainer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试维修方', 'SUPPLIER', 'SUPPLIER', 'SUPPLIER_MAINTAINER', 'ORDER', '合作中', null, true);

-- 测试供应商数据
INSERT INTO supplier (supplier_code, supplier_name, supplier_type, contact_name, contact_phone, status) VALUES
('TEST_SELLER_001', '测试设备出售商', 'SELLER', '张销售', '13800138001', '合作中'),
('TEST_RENTER_001', '测试设备租赁商', 'RENTER', '李租赁', '13800138002', '合作中'),
('TEST_MAINTAINER_001', '测试维修服务商', 'MAINTAINER', '王维修', '13800138003', '合作中');
```

### 15.5 生产环境禁用机制

#### 配置方式

```yaml
# application-prod.yml
app:
  test-accounts:
    enabled: false  # 生产环境禁用测试账户
    usernames:      # 明确列出的测试账户
      - test_asset_admin
      - test_transfer
      - test_inventory
      - test_finance
      - test_store_manager
      - test_dept_manager
      - test_employee
      - test_supplier_seller
      - test_supplier_renter
      - test_supplier_maintainer
```

#### 禁用实现

```java
@Component
public class TestAccountFilter extends OncePerRequestFilter {
    
    @Value("${app.test-accounts.enabled:true}")
    private boolean testAccountsEnabled;
    
    @Value("${app.test-accounts.usernames:}")
    private List<String> testUsernames;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain chain) throws ServletException, IOException {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !testAccountsEnabled) {
            String username = auth.getName();
            if (testUsernames.contains(username)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Test accounts are disabled in production environment");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
```

### 15.6 同步任务调度

```java
@Component
public class DataSyncScheduler {
    
    @Autowired
    private HrDataSyncService hrDataSyncService;
    
    @Autowired
    private SrmDataSyncService srmDataSyncService;
    
    /**
     * HR数据同步 - 每日凌晨1:00
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void syncHrData() {
        hrDataSyncService.syncEmployees();
        hrDataSyncService.syncDepartments();
    }
    
    /**
     * SRM供应商同步 - 每日凌晨2:00
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncSrmData() {
        srmDataSyncService.syncSuppliers();
    }
}
```

---

## 附录D: 系统集成数据库表

### D.1 SRM供应商同步中间表

```sql
CREATE TABLE srm_supplier_sync (
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
    sync_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '同步状态',
    sync_time TIMESTAMP COMMENT '同步时间',
    sync_error TEXT COMMENT '同步错误信息',
    local_supplier_id BIGINT COMMENT '关联本地供应商ID',
    srm_create_time TIMESTAMP COMMENT 'SRM创建时间',
    srm_update_time TIMESTAMP COMMENT 'SRM更新时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### D.2 北森HR员工同步中间表

```sql
CREATE TABLE hr_employee_sync (
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
```

### D.3 北森HR部门同步中间表

```sql
CREATE TABLE hr_department_sync (
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
```

---

**文档维护记录**

| 日期 | 版本 | 修改人 | 说明 |
|------|------|--------|------|
| 2026-04-01 | v1.0 | 书呆子 | 完善PRD，增加盘点流程、门店管理、入离职联动 |
| 2026-04-01 | v1.1 | 书呆子 | 新增用户分类与角色管理体系，支持供应商、设备使用者、设备管理者三大类角色 |
| 2026-04-01 | v1.2 | 书呆子 | 新增系统集成章节：SRM供应商集成、北森HR集成、测试账户体系、生产环境禁用机制 |

---

**飞书文档**: https://www.feishu.cn/wiki/GoELwhxsNiHsNtkz8O7c0l4Enze