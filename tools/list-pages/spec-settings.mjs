// 批次 2：设置 / 工具箱中的只读列表页。账号、密钥、卡号一律是虚构的脱敏占位，不对应任何真实值。
import { pad, pick, time, WH, cus, ON_OFF, OK_FAIL, CHANNELS, no, ID } from './helpers.mjs'

const statusCol = ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }]
const updateCol = ['updateTime', '更新时间', 'time', { width: 160 }]

export const settings = [
  // ---------- setting 设置 ----------
  {
    key: 'printTemplate', route: 'setting/printTemplate', title: '打印模板', idBase: ID(33), count: 10,
    columns: [
      ['tempCode', '模板编码', 'str', { kw: true, width: 120 }],
      ['tempName', '模板名称', 'str', { kw: true }],
      ['tempType', '模板类型', 'str', { filter: true, width: 100 }],
      ['paperSize', '纸张', 'str', { filter: true, width: 90 }],
      ['isDefault', '默认', 'int', { enum: { 1: ['是', 'success'], 0: ['否', 'info'] }, width: 70 }],
      statusCol, updateCol
    ],
    row: i => ({ tempCode: 'PT-' + pad(i + 1), tempName: '示例打印模板' + (i + 1), tempType: pick(['面单', '箱唛', '拣货单', 'SKU标签'], i),
      paperSize: pick(['100x100', '100x150', 'A4'], i), isDefault: i < 4 ? 1 : 0, status: i === 9 ? 0 : 1, updateTime: time(i) })
  },
  {
    key: 'brand', route: 'setting/brand', title: '品牌设置', idBase: ID(34), count: 4,
    columns: [
      ['brandCode', '品牌编码', 'str', { kw: true, width: 120 }],
      ['brandName', '品牌名称', 'str', { kw: true }],
      ['domain', '访问域名', 'str', { width: 200 }],
      statusCol, updateCol
    ],
    row: i => ({ brandCode: 'BR-' + pad(i + 1), brandName: '示例品牌' + (i + 1), domain: 'wms-demo-' + (i + 1) + '.example.com', status: i === 3 ? 0 : 1, updateTime: time(i) })
  },
  {
    key: 'agency', route: 'setting/agency', title: '服务商', idBase: ID(35), count: 8,
    columns: [
      ['agencyCode', '服务商编码', 'str', { kw: true, width: 120 }],
      ['agencyName', '服务商名称', 'str', { kw: true }],
      ['agencyType', '类型', 'str', { filter: true, width: 120 }],
      ['contactName', '联系人', 'str', { width: 110 }],
      statusCol, updateCol
    ],
    row: i => ({ agencyCode: 'AG-' + pad(i + 1), agencyName: '示例服务商' + (i + 1), agencyType: pick(['物流服务商', '报关行', '海外仓合作方'], i),
      contactName: '示例联系人' + (i + 1), status: i === 6 ? 0 : 1, updateTime: time(i) })
  },
  {
    key: 'erpAuth', route: 'setting/erpAuth', title: 'ERP 授权', idBase: ID(36), count: 8,
    columns: [
      ['erpName', 'ERP', 'str', { filter: true, width: 110 }],
      ['customerCode', '客户代码', 'str', { filter: true, kw: true, width: 100 }],
      ['appKeyMasked', 'AppKey（脱敏）', 'str', { width: 140 }],
      ['authTime', '授权时间', 'time', { width: 160 }],
      ['expireTime', '到期时间', 'time', { width: 160 }],
      ['status', '状态', 'int', { enum: { 1: ['有效', 'success'], 0: ['已过期', 'info'] }, tabs: true, width: 80 }]
    ],
    row: i => ({ erpName: pick(['示例ERP-A', '示例ERP-B', '示例ERP-C'], i), customerCode: cus(i), appKeyMasked: 'demo_****' + pad(i + 1, 4),
      authTime: time(i), expireTime: i % 4 === 3 ? '2026-11-20 00:00:00' : '2027-11-01 00:00:00', status: i % 4 === 3 ? 0 : 1 })
  },
  {
    key: 'msgNotice', route: 'setting/msgNotice', title: '消息通知', idBase: ID(37), count: 12,
    columns: [
      ['title', '标题', 'str', { kw: true }],
      ['msgType', '消息类型', 'str', { filter: true, width: 90 }],
      ['receiver', '接收人', 'str', { width: 110 }],
      ['readFlag', '状态', 'int', { enum: { 0: ['未读', 'warning'], 1: ['已读', 'info'] }, tabs: true, width: 80 }],
      ['sendTime', '发送时间', 'time', { width: 160 }]
    ],
    row: i => {
      const t = pick(['系统', '订单', '库存', '账单'], i)
      return { title: '示例' + t + '消息' + (i + 1), msgType: t, receiver: '示例员工' + ((i % 4) + 1), readFlag: i % 3 === 0 ? 0 : 1, sendTime: time(i) }
    }
  },
  {
    key: 'warningMsg', route: 'setting/warningMsg', title: '预警消息', idBase: ID(38), count: 12,
    columns: [
      ['warningType', '预警类型', 'str', { filter: true, width: 100 }],
      ['content', '内容', 'str', { kw: true }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['level', '级别', 'int', { enum: { 1: ['提示', 'info'], 2: ['警告', 'warning'], 3: ['严重', 'danger'] }, tabs: true, width: 80 }],
      ['handled', '处理状态', 'int', { enum: { 0: ['未处理', 'warning'], 1: ['已处理', 'success'] }, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const t = pick(['库存不足', '库龄超期', '订单超时'], i)
      return { warningType: t, content: '示例预警：' + t + ' SKU-' + pad(i + 1, 4), whCode: pick(WH, i), level: pick([1, 2, 3, 2], i),
        handled: i % 2, createTime: time(i) }
    }
  },
  {
    key: 'receiveAccount', route: 'setting/receiveAccount', title: '收款账户', idBase: ID(39), count: 5,
    columns: [
      ['accountName', '账户名称', 'str', { kw: true }],
      ['bankName', '开户行', 'str', { filter: true, width: 120 }],
      ['accountNoMasked', '账号（脱敏）', 'str', { width: 160 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      statusCol, updateCol
    ],
    row: i => ({ accountName: '示例收款账户' + (i + 1), bankName: pick(['示例银行A', '示例银行B'], i), accountNoMasked: '**** **** **** 00' + pad(i + 1, 2),
      currency: pick(['USD', 'CNY', 'EUR'], i), status: i === 4 ? 0 : 1, updateTime: time(i) })
  },
  // ---------- toolKit 工具箱 ----------
  {
    key: 'errorPool', route: 'toolKit/errorPool', title: '异常池', idBase: ID(40), count: 14,
    columns: [
      ['errorNo', '异常编号', 'str', { kw: true, width: 150 }],
      ['bizType', '业务类型', 'str', { filter: true, width: 90 }],
      ['bizNo', '业务单号', 'str', { kw: true, width: 150 }],
      ['errorMsg', '异常信息', 'str', {}],
      ['retryCount', '重试次数', 'int', { width: 90 }],
      ['status', '状态', 'int', { enum: { 0: ['待处理', 'warning'], 1: ['已解决', 'success'], 2: ['已忽略', 'info'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ errorNo: no('ERR', i, 5), bizType: pick(['出库单', '入库单', '推送'], i), bizNo: no(pick(['OB', 'ASN', 'PS'], i), i),
      errorMsg: pick(['示例：渠道下单失败', '示例：SKU 不存在', '示例：回传超时'], i), retryCount: i % 4, status: pick([0, 1, 1, 2], i), createTime: time(i) })
  },
  {
    key: 'labelRecord', route: 'toolKit/labelRecord', title: '面单记录', idBase: ID(41), count: 14,
    columns: [
      ['orderNo', '出库单号', 'str', { kw: true, width: 150 }],
      ['trackingNo', '跟踪号', 'str', { kw: true, width: 140 }],
      ['channelName', '物流渠道', 'str', { filter: true, width: 110 }],
      ['labelFormat', '格式', 'str', { filter: true, width: 80 }],
      ['result', '结果', 'int', { enum: OK_FAIL, tabs: true, width: 80 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const r = i % 5 === 4 ? 0 : 1
      return { orderNo: no('OB', i + 100), trackingNo: r ? 'TRK' + pad(600000 + i * 7, 8) : '', channelName: pick(CHANNELS, i),
        labelFormat: pick(['PDF', 'PNG', 'ZPL'], i), result: r, createTime: time(i) }
    }
  },
  {
    key: 'pushRecord', route: 'toolKit/pushRecord', title: '推送记录', idBase: ID(42), count: 16,
    columns: [
      ['pushNo', '推送编号', 'str', { kw: true, width: 150 }],
      ['bizNo', '业务单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['pushType', '推送类型', 'str', { filter: true, width: 100 }],
      ['costMs', '耗时(ms)', 'int', { width: 90 }],
      ['result', '结果', 'int', { enum: OK_FAIL, tabs: true, width: 80 }],
      ['pushTime', '推送时间', 'time', { width: 160 }]
    ],
    row: i => ({ pushNo: no('PS', i, 5), bizNo: no(pick(['OB', 'ASN', 'STK'], i), i), customerCode: cus(i), pushType: pick(['出库回传', '入库回传', '库存同步'], i),
      costMs: 60 + ((i * 53) % 700), result: i % 7 === 5 ? 0 : 1, pushTime: time(i) })
  }
]
