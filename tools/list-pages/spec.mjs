// 通用只读列表页规格（唯一数据源）。运行 `node tools/list-pages/gen.mjs` 生成：
//   backend/src/main/resources/db/schema-list.sql、data-list.sql、list-resources.json
//   frontend/src/config/listPages.generated.js
// 全部字段、编码、接口路径均为本项目占位；数据全部虚构。不要写入任何真实业务数据。
//
// 列定义：[name(camel), label, type, opts]
//   type: str | int | num(两位小数) | money | rate | time
//   opts: { kw: 参与关键字搜索, filter: 下拉筛选, enum: {值: [文案, tag类型]}, width, tabs: 作为状态页签 }
//   enum 列自动可筛选；每个资源最多一个 tabs 列。
// 批次 2 起的资源按模块分在 spec-orders / spec-finance / spec-settings 中。

import { pad, pick, day, time, WH, CUS, ON_OFF } from './helpers.mjs'
import { orders } from './spec-orders.mjs'
import { finance } from './spec-finance.mjs'
import { settings } from './spec-settings.mjs'
import { extra } from './spec-extra.mjs'

const batch1 = [
  {
    key: 'customer', route: 'customer/list', title: '客户列表', idBase: 3100000000000000,
    columns: [
      ['customerCode', '客户代码', 'str', { kw: true, width: 100 }],
      ['customerName', '客户名称', 'str', { kw: true }],
      ['contactName', '联系人', 'str', { width: 110 }],
      ['settleCurrency', '结算币种', 'str', { filter: true, width: 90 }],
      ['balance', '账户余额', 'money', { width: 110 }],
      ['creditLimit', '信用额度', 'money', { width: 110 }],
      ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    count: 12,
    row: i => ({ customerCode: 'C' + pad(i + 1), customerName: '示例客户' + (i + 1), contactName: '示例联系人' + (i + 1),
      settleCurrency: pick(['USD', 'CNY', 'EUR'], i), balance: (i * 1234.5 + 300).toFixed(2), creditLimit: ((i % 4) * 5000).toFixed(2),
      status: i % 6 === 5 ? 0 : 1, createTime: time(i) })
  },
  {
    key: 'customerRecharge', route: 'customer/recharge', title: '充值审核', idBase: 3200000000000000,
    columns: [
      ['rechargeNo', '充值单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { kw: true, width: 100 }],
      ['customerName', '客户名称', 'str', {}],
      ['amount', '金额', 'money', { width: 110 }],
      ['currency', '币种', 'str', { filter: true, width: 80 }],
      ['payMethod', '支付方式', 'str', { filter: true, width: 110 }],
      ['status', '状态', 'int', { enum: { 10: ['待审核', 'warning'], 20: ['已通过', 'success'], 30: ['已驳回', 'danger'] }, tabs: true, width: 90 }],
      ['applyTime', '申请时间', 'time', { width: 160 }],
      ['auditTime', '审核时间', 'time', { width: 160 }]
    ],
    count: 16,
    row: i => {
      const s = pick([10, 20, 20, 30], i)
      return { rechargeNo: 'RC2026110' + pad(i + 1, 4), ...CUS(i), amount: ((i + 1) * 500).toFixed(2), currency: pick(['USD', 'CNY'], i),
        payMethod: pick(['银行转账', '线上支付'], i), status: s, applyTime: time(i), auditTime: s === 10 ? '' : time(i, 2) }
    }
  },
  {
    key: 'seedWall', route: 'basicData/seedWall', title: '播种墙', idBase: 3300000000000000,
    columns: [
      ['wallCode', '播种墙编码', 'str', { kw: true, width: 120 }],
      ['wallName', '名称', 'str', { kw: true }],
      ['whCode', '仓库', 'str', { filter: true, width: 90 }],
      ['rowCount', '行数', 'int', { width: 70 }],
      ['colCount', '列数', 'int', { width: 70 }],
      ['gridCount', '格口数', 'int', { width: 80 }],
      ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    count: 8,
    row: i => {
      const r = 3 + (i % 3)
      const c = 4 + (i % 4)
      return { wallCode: 'SW' + pad(i + 1), wallName: '示例播种墙' + (i + 1), whCode: pick(WH, i), rowCount: r, colCount: c,
        gridCount: r * c, status: i === 7 ? 0 : 1, createTime: time(i) }
    }
  },
  {
    key: 'pickCart', route: 'basicData/pickCart', title: '拣货车', idBase: 3400000000000000,
    columns: [
      ['cartCode', '拣货车编码', 'str', { kw: true, width: 120 }],
      ['cartName', '名称', 'str', {}],
      ['whCode', '仓库', 'str', { filter: true, width: 90 }],
      ['gridCount', '格口数', 'int', { width: 80 }],
      ['bindWaveNo', '绑定波次', 'str', { kw: true, width: 150 }],
      ['status', '状态', 'int', { enum: { 0: ['空闲', 'success'], 1: ['使用中', 'warning'], 2: ['停用', 'info'] }, tabs: true, width: 90 }],
      ['updateTime', '更新时间', 'time', { width: 160 }]
    ],
    count: 10,
    row: i => {
      const s = pick([0, 1, 0, 1, 2], i)
      return { cartCode: 'CART' + pad(i + 1), cartName: '示例拣货车' + (i + 1), whCode: pick(WH, i), gridCount: pick([12, 16, 24], i),
        bindWaveNo: s === 1 ? 'WV2026110' + pad(i + 1, 4) : '', status: s, updateTime: time(i) }
    }
  },
  {
    key: 'account', route: 'auth/account', title: '账号管理', idBase: 3500000000000000,
    columns: [
      ['username', '登录账号', 'str', { kw: true, width: 120 }],
      ['realName', '姓名', 'str', { kw: true, width: 120 }],
      ['roleName', '角色', 'str', { filter: true, width: 120 }],
      ['whCodes', '可用仓库', 'str', { width: 120 }],
      ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }],
      ['lastLoginTime', '最近登录', 'time', { width: 160 }]
    ],
    count: 12,
    row: i => ({ username: 'user' + pad(i + 1, 2), realName: '示例员工' + (i + 1), roleName: pick(['仓库管理员', '收货员', '拣货员', '复核员'], i),
      whCodes: pick(['WH01', 'WH02', 'WH01,WH02'], i), status: i % 5 === 4 ? 0 : 1, lastLoginTime: time(i, 3) })
  },
  {
    key: 'role', route: 'auth/role', title: '角色管理', idBase: 3600000000000000,
    columns: [
      ['roleCode', '角色编码', 'str', { kw: true, width: 140 }],
      ['roleName', '角色名称', 'str', { kw: true, width: 140 }],
      ['userCount', '账号数', 'int', { width: 80 }],
      ['remark', '备注', 'str', {}],
      ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    count: 6,
    row: i => ({ roleCode: pick(['ROLE_ADMIN', 'ROLE_RECEIVE', 'ROLE_PICK', 'ROLE_REVIEW', 'ROLE_FINANCE', 'ROLE_GUEST'], i),
      roleName: pick(['仓库管理员', '收货员', '拣货员', '复核员', '财务', '访客'], i), userCount: [2, 3, 4, 3, 1, 0][i],
      remark: '示例角色 ' + (i + 1), status: i === 5 ? 0 : 1, createTime: time(i) })
  },
  {
    key: 'channel', route: 'logisticsManage/channel', title: '物流渠道', idBase: 3700000000000000,
    columns: [
      ['channelCode', '渠道代码', 'str', { kw: true, width: 130 }],
      ['channelName', '渠道名称', 'str', { kw: true }],
      ['carrier', '承运商', 'str', { filter: true, width: 120 }],
      ['serviceType', '服务类型', 'str', { filter: true, width: 90 }],
      ['whCode', '仓库', 'str', { filter: true, width: 90 }],
      ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }],
      ['updateTime', '更新时间', 'time', { width: 160 }]
    ],
    count: 14,
    row: i => ({ channelCode: 'CH-' + pad(i + 1), channelName: '示例渠道' + (i + 1), carrier: pick(['示例承运商A', '示例承运商B', '示例承运商C'], i),
      serviceType: pick(['快递', '卡派', '自提'], i), whCode: pick(WH, i), status: i % 7 === 6 ? 0 : 1, updateTime: time(i) })
  },
  {
    key: 'channelGroup', route: 'logisticsManage/channelGroup', title: '渠道分组', idBase: 3800000000000000,
    columns: [
      ['groupCode', '分组代码', 'str', { kw: true, width: 130 }],
      ['groupName', '分组名称', 'str', { kw: true }],
      ['channelCount', '渠道数', 'int', { width: 80 }],
      ['remark', '备注', 'str', {}],
      ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }],
      ['updateTime', '更新时间', 'time', { width: 160 }]
    ],
    count: 5,
    row: i => ({ groupCode: 'GRP-' + pad(i + 1), groupName: '示例分组' + (i + 1), channelCount: 2 + i, remark: '示例渠道分组',
      status: i === 4 ? 0 : 1, updateTime: time(i) })
  },
  {
    key: 'exchangeRate', route: 'setting/exchangeRate', title: '汇率', idBase: 3900000000000000,
    columns: [
      ['fromCurrency', '原币种', 'str', { filter: true, kw: true, width: 90 }],
      ['toCurrency', '目标币种', 'str', { filter: true, width: 90 }],
      ['rate', '汇率', 'rate', { width: 110 }],
      ['effectiveDate', '生效日期', 'str', { width: 110 }],
      ['updateUser', '维护人', 'str', { width: 110 }]
    ],
    count: 8,
    row: i => ({ fromCurrency: pick(['USD', 'EUR', 'GBP', 'JPY'], i), toCurrency: i < 4 ? 'CNY' : 'USD',
      rate: (pick([7.1, 7.7, 9.0, 0.048, 1, 1.08, 1.26, 0.0067], i) + (i % 3) * 0.001).toFixed(6), effectiveDate: day(i), updateUser: '示例员工1' })
  },
  {
    key: 'notice', route: 'setting/notice', title: '公告', idBase: 4000000000000000,
    columns: [
      ['title', '标题', 'str', { kw: true }],
      ['noticeType', '类型', 'str', { filter: true, width: 100 }],
      ['publisher', '发布人', 'str', { width: 110 }],
      ['status', '状态', 'int', { enum: { 0: ['草稿', 'info'], 1: ['已发布', 'success'], 2: ['已撤回', 'warning'] }, tabs: true, width: 90 }],
      ['publishTime', '发布时间', 'time', { width: 160 }]
    ],
    count: 9,
    row: i => {
      const s = pick([1, 1, 0, 2], i)
      return { title: '示例公告' + (i + 1), noticeType: pick(['系统公告', '仓库通知'], i), publisher: '示例员工' + ((i % 3) + 1),
        status: s, publishTime: s === 0 ? '' : time(i) }
    }
  },
  {
    key: 'requestLog', route: 'toolKit/requestLog', title: '请求日志', idBase: 4100000000000000,
    columns: [
      ['requestNo', '请求编号', 'str', { kw: true, width: 160 }],
      ['apiName', '接口', 'str', { kw: true, filter: true }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['costMs', '耗时(ms)', 'int', { width: 90 }],
      ['result', '结果', 'int', { enum: { 1: ['成功', 'success'], 0: ['失败', 'danger'] }, tabs: true, width: 80 }],
      ['requestTime', '请求时间', 'time', { width: 160 }]
    ],
    count: 20,
    row: i => ({ requestNo: 'REQ2026110' + pad(i + 1, 5), apiName: pick(['示例-创建出库单', '示例-查询库存', '示例-创建入库单'], i),
      customerCode: CUS(i).customerCode, costMs: 40 + ((i * 37) % 500), result: i % 6 === 3 ? 0 : 1, requestTime: time(i) })
  },
  {
    key: 'taskCenter', route: 'toolKit/taskCenter', title: '任务中心', idBase: 4200000000000000,
    columns: [
      ['taskNo', '任务编号', 'str', { kw: true, width: 150 }],
      ['taskName', '任务名称', 'str', { kw: true }],
      ['taskType', '类型', 'str', { filter: true, width: 80 }],
      ['progress', '进度(%)', 'int', { width: 80 }],
      ['status', '状态', 'int', { enum: { 10: ['排队中', 'info'], 20: ['执行中', 'warning'], 30: ['已完成', 'success'], 40: ['失败', 'danger'] }, tabs: true, width: 90 }],
      ['createUser', '创建人', 'str', { width: 110 }],
      ['createTime', '创建时间', 'time', { width: 160 }],
      ['finishTime', '完成时间', 'time', { width: 160 }]
    ],
    count: 12,
    row: i => {
      const s = pick([30, 30, 20, 10, 40], i)
      return { taskNo: 'TASK2026110' + pad(i + 1, 4), taskName: pick(['导出入库单', '导出库存', '导入产品资料', '导出出库单'], i),
        taskType: pick(['导出', '导出', '导入', '导出'], i), progress: { 10: 0, 20: 45, 30: 100, 40: 60 }[s], status: s,
        createUser: '示例员工' + ((i % 3) + 1), createTime: time(i), finishTime: s === 30 || s === 40 ? time(i, 1, 10) : '' }
    }
  },
  {
    key: 'returnOrder', route: 'returnGoods/warehousing', title: '退货入库', idBase: 4300000000000000,
    columns: [
      ['returnNo', '退货单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['customerName', '客户名称', 'str', { width: 110 }],
      ['sourceOrderNo', '原出库单号', 'str', { kw: true, width: 150 }],
      ['trackingNo', '退货跟踪号', 'str', { kw: true, width: 150 }],
      ['returnType', '退货类型', 'str', { filter: true, width: 100 }],
      ['skuQty', '数量', 'int', { width: 70 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['status', '状态', 'int', { enum: { 10: ['待收货', 'info'], 20: ['已收货', 'warning'], 30: ['已上架', 'success'], 90: ['已取消', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    count: 18,
    row: i => ({ returnNo: 'RT2026110' + pad(i + 1, 4), ...CUS(i), sourceOrderNo: 'OB2026100' + pad(i + 11, 4), trackingNo: 'TRK' + pad(900000 + i * 13, 8),
      returnType: pick(['买家退货', '拒收', '异常件'], i), skuQty: 1 + (i % 4), whCode: pick(WH, i), status: pick([10, 20, 30, 30, 90], i), createTime: time(i) })
  },
  {
    key: 'outboundAbnormal', route: 'outbound/abnormal', title: '异常订单', idBase: 4400000000000000,
    columns: [
      ['orderNo', '出库单号', 'str', { kw: true, width: 150 }],
      ['waveNo', '波次号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['abnormalType', '异常类型', 'str', { filter: true, width: 100 }],
      ['remark', '说明', 'str', {}],
      ['status', '状态', 'int', { enum: { 0: ['待处理', 'warning'], 1: ['已处理', 'success'] }, tabs: true, width: 90 }],
      ['reportUser', '上报人', 'str', { width: 110 }],
      ['reportTime', '上报时间', 'time', { width: 160 }]
    ],
    count: 11,
    row: i => ({ orderNo: 'OB2026110' + pad(i + 21, 4), waveNo: 'WV2026110' + pad((i % 4) + 1, 4), customerCode: CUS(i).customerCode,
      abnormalType: pick(['缺货', '库位异常', '面单异常'], i), remark: '示例异常说明', status: i % 3 === 0 ? 0 : 1,
      reportUser: '示例员工' + ((i % 4) + 1), reportTime: time(i) })
  },
  {
    key: 'outboundIntercept', route: 'outbound/intercept', title: '拦截订单', idBase: 4500000000000000,
    columns: [
      ['orderNo', '出库单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['interceptReason', '拦截原因', 'str', { filter: true }],
      ['orderStatusName', '拦截时订单状态', 'str', { width: 130 }],
      ['status', '状态', 'int', { enum: { 10: ['拦截中', 'warning'], 20: ['拦截成功', 'success'], 30: ['拦截失败', 'danger'] }, tabs: true, width: 90 }],
      ['applyTime', '申请时间', 'time', { width: 160 }]
    ],
    count: 10,
    row: i => ({ orderNo: 'OB2026110' + pad(i + 41, 4), customerCode: CUS(i).customerCode, interceptReason: pick(['客户取消', '地址错误', '重复下单'], i),
      orderStatusName: pick(['待拣货', '拣货中', '待复核'], i), status: pick([10, 20, 20, 30], i), applyTime: time(i) })
  }
]

export const resources = [...batch1, ...orders, ...finance, ...settings, ...extra]

// 批次 8：允许在本地库新建/编辑/删除的资源（基础资料、报价、设置类）。日志、单据、流水类页面保持只读。
// 只写本地开发库；字段、校验规则都是本项目假设。
export const editable = new Set([
  'customer', 'seedWall', 'pickCart', 'account', 'role', 'channel', 'channelGroup', 'exchangeRate', 'notice', 'waveRule',
  'fuelPlan', 'zoneTemp', 'surcharge', 'freightTemp', 'logisticsPriceSetting', 'operateBill', 'warehouseRent', 'quotationScheme',
  'billTemplate', 'printTemplate', 'brand', 'agency', 'receiveAccount'
])
