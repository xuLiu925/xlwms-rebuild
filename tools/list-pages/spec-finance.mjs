// 批次 2：物流报价 / 应收报价 / 应收结算中的只读列表页。金额、费率全部虚构，路径与编码为本项目占位。
import { pad, pick, day, time, WH, CUS, cus, ON_OFF, CARRIERS, CHANNELS, no, fix2, ID } from './helpers.mjs'

const updated = i => ({ status: i % 5 === 4 ? 0 : 1, updateTime: time(i) })
const statusCol = ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }]
const updateCol = ['updateTime', '更新时间', 'time', { width: 160 }]

export const finance = [
  // ---------- logisticsPrice 物流报价 ----------
  {
    key: 'fuelPlan', route: 'logisticsPrice/fuelPlan', title: '燃油费方案', idBase: ID(21), count: 8,
    columns: [
      ['planCode', '方案编码', 'str', { kw: true, width: 120 }],
      ['planName', '方案名称', 'str', { kw: true }],
      ['carrier', '承运商', 'str', { filter: true, width: 120 }],
      ['fuelRate', '燃油费率(%)', 'num', { width: 110 }],
      ['effectiveDate', '生效日期', 'str', { width: 110 }],
      ['expireDate', '失效日期', 'str', { width: 110 }],
      statusCol, updateCol
    ],
    row: i => ({ planCode: 'FP-' + pad(i + 1), planName: '示例燃油方案' + (i + 1), carrier: pick(CARRIERS, i), fuelRate: fix2(12 + i * 0.75),
      effectiveDate: day(i), expireDate: '2026-12-31', ...updated(i) })
  },
  {
    key: 'zoneTemp', route: 'logisticsPrice/zoneTemp', title: '分区模板', idBase: ID(22), count: 8,
    columns: [
      ['tempCode', '模板编码', 'str', { kw: true, width: 120 }],
      ['tempName', '模板名称', 'str', { kw: true }],
      ['carrier', '承运商', 'str', { filter: true, width: 120 }],
      ['country', '国家', 'str', { filter: true, width: 70 }],
      ['zoneCount', '分区数', 'int', { width: 80 }],
      statusCol, updateCol
    ],
    row: i => ({ tempCode: 'ZT-' + pad(i + 1), tempName: '示例分区模板' + (i + 1), carrier: pick(CARRIERS, i), country: pick(['US', 'CA', 'GB', 'DE'], i),
      zoneCount: pick([8, 9, 12], i), ...updated(i) })
  },
  {
    key: 'surcharge', route: 'logisticsPrice/surcharge', title: '附加费', idBase: ID(23), count: 10,
    columns: [
      ['chargeCode', '费用编码', 'str', { kw: true, width: 120 }],
      ['chargeName', '费用名称', 'str', { kw: true }],
      ['carrier', '承运商', 'str', { filter: true, width: 120 }],
      ['chargeType', '计费方式', 'str', { filter: true, width: 100 }],
      ['amount', '金额', 'money', { width: 90 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      statusCol, updateCol
    ],
    row: i => ({ chargeCode: 'SC-' + pad(i + 1), chargeName: pick(['偏远附加费', '超长附加费', '住宅地址费', '签名服务费', '超重附加费'], i),
      carrier: pick(CARRIERS, i), chargeType: pick(['按票', '按件', '按重量'], i), amount: fix2(2.5 + i * 1.8), currency: pick(['USD', 'EUR'], i), ...updated(i) })
  },
  {
    key: 'freightTemp', route: 'logisticsPrice/freightTemp', title: '运费模板', idBase: ID(24), count: 8,
    columns: [
      ['tempCode', '模板编码', 'str', { kw: true, width: 120 }],
      ['tempName', '模板名称', 'str', { kw: true }],
      ['channelName', '物流渠道', 'str', { filter: true, width: 110 }],
      ['billingMode', '计费模式', 'str', { filter: true, width: 100 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      statusCol, updateCol
    ],
    row: i => ({ tempCode: 'FT-' + pad(i + 1), tempName: '示例运费模板' + (i + 1), channelName: pick(CHANNELS, i), billingMode: pick(['首重续重', '阶梯价'], i),
      currency: pick(['USD', 'EUR'], i), ...updated(i) })
  },
  // ---------- quotesReceivable 应收报价 ----------
  {
    key: 'logisticsPriceSetting', route: 'quotesReceivable/logisticsPriceSetting', title: '物流报价设置', idBase: ID(25), count: 10,
    columns: [
      ['customerCode', '客户代码', 'str', { filter: true, kw: true, width: 100 }],
      ['customerName', '客户名称', 'str', { kw: true, width: 110 }],
      ['channelName', '物流渠道', 'str', { filter: true, width: 110 }],
      ['freightTempName', '运费模板', 'str', {}],
      ['discountRate', '折扣率', 'rate', { width: 100 }],
      statusCol, updateCol
    ],
    row: i => ({ ...CUS(i), channelName: pick(CHANNELS, i), freightTempName: '示例运费模板' + ((i % 8) + 1), discountRate: (0.8 + (i % 5) * 0.05).toFixed(6), ...updated(i) })
  },
  {
    key: 'operateBill', route: 'quotesReceivable/operateBill', title: '操作费报价', idBase: ID(26), count: 10,
    columns: [
      ['itemCode', '费用项编码', 'str', { kw: true, width: 120 }],
      ['itemName', '费用项', 'str', { kw: true }],
      ['billingUnit', '计费单位', 'str', { filter: true, width: 90 }],
      ['unitPrice', '单价', 'money', { width: 90 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      statusCol, updateCol
    ],
    row: i => ({ itemCode: 'OP-' + pad(i + 1), itemName: pick(['入库卸货', '上架', '拣货', '打包', '贴标', '出库装车', '退货处理', '拍照', '换标', '销毁'], i),
      billingUnit: pick(['票', '件', '箱', '托'], i), unitPrice: fix2(0.3 + i * 0.45), currency: 'USD', ...updated(i) })
  },
  {
    key: 'warehouseRent', route: 'quotesReceivable/warehouseRent', title: '仓租报价', idBase: ID(27), count: 8,
    columns: [
      ['planCode', '方案编码', 'str', { kw: true, width: 120 }],
      ['planName', '方案名称', 'str', { kw: true }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['freeDays', '免租天数', 'int', { width: 90 }],
      ['unitPrice', '单价(每立方米/天)', 'money', { width: 140 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      statusCol, updateCol
    ],
    row: i => ({ planCode: 'WR-' + pad(i + 1), planName: '示例仓租方案' + (i + 1), whCode: pick(WH, i), freeDays: pick([30, 60, 90], i),
      unitPrice: fix2(0.2 + i * 0.05), currency: 'USD', ...updated(i) })
  },
  {
    key: 'quotationScheme', route: 'quotesReceivable/quotationScheme', title: '报价方案', idBase: ID(28), count: 8,
    columns: [
      ['schemeCode', '方案编码', 'str', { kw: true, width: 120 }],
      ['schemeName', '方案名称', 'str', { kw: true }],
      ['customerCount', '适用客户数', 'int', { width: 100 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      ['effectiveDate', '生效日期', 'str', { width: 110 }],
      ['status', '状态', 'int', { enum: { 0: ['草稿', 'info'], 1: ['生效', 'success'], 2: ['失效', 'warning'] }, tabs: true, width: 80 }],
      updateCol
    ],
    row: i => ({ schemeCode: 'QS-' + pad(i + 1), schemeName: '示例报价方案' + (i + 1), customerCount: i % 4, currency: pick(['USD', 'EUR'], i),
      effectiveDate: day(i), status: pick([1, 1, 0, 2], i), updateTime: time(i) })
  },
  // ---------- receivableSettlement 应收结算 ----------
  {
    key: 'businessExpenses', route: 'receivableSettlement/businessExpenses', title: '业务费用', idBase: ID(29), count: 18,
    columns: [
      ['feeNo', '费用单号', 'str', { kw: true, width: 150 }],
      ['bizNo', '业务单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['feeType', '费用类型', 'str', { filter: true, width: 90 }],
      ['amount', '金额', 'money', { width: 100 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      ['status', '状态', 'int', { enum: { 10: ['未出账', 'warning'], 20: ['已出账', 'success'] }, tabs: true, width: 90 }],
      ['feeTime', '计费时间', 'time', { width: 160 }]
    ],
    row: i => ({ feeNo: no('FE', i, 5), bizNo: no(pick(['OB', 'ASN', 'FO'], i), i), customerCode: cus(i), feeType: pick(['操作费', '仓租', '运费', '附加费'], i),
      amount: fix2(3.2 + i * 4.15), currency: 'USD', status: i % 3 === 0 ? 10 : 20, feeTime: time(i) })
  },
  {
    key: 'billsReceivable', route: 'receivableSettlement/billsReceivable', title: '应收账单', idBase: ID(30), count: 12,
    columns: [
      ['billNo', '账单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['customerName', '客户名称', 'str', { width: 110 }],
      ['billPeriod', '账期', 'str', { filter: true, width: 90 }],
      ['amount', '账单金额', 'money', { width: 110 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      ['status', '状态', 'int', { enum: { 10: ['待确认', 'warning'], 20: ['已确认', 'primary'], 30: ['已结清', 'success'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ billNo: no('BL', i), ...CUS(i), billPeriod: pick(['2026-09', '2026-10', '2026-11'], Math.floor(i / 4)), amount: fix2(800 + i * 215.3),
      currency: 'USD', status: i < 4 ? 30 : i < 8 ? 20 : 10, createTime: time(i) })
  },
  {
    key: 'expend', route: 'receivableSettlement/expend', title: '扣款记录', idBase: ID(31), count: 14,
    columns: [
      ['expendNo', '扣款单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['billNo', '关联账单', 'str', { kw: true, width: 150 }],
      ['amount', '扣款金额', 'money', { width: 110 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      ['balanceAfter', '扣后余额', 'money', { width: 110 }],
      ['expendTime', '扣款时间', 'time', { width: 160 }]
    ],
    row: i => ({ expendNo: no('EX', i), customerCode: cus(i), billNo: no('BL', i % 12), amount: fix2(50 + i * 33.3), currency: 'USD',
      balanceAfter: fix2(5000 - i * 120.5), expendTime: time(i) })
  },
  {
    key: 'billTemplate', route: 'receivableSettlement/billTemplate', title: '账单模板', idBase: ID(32), count: 6,
    columns: [
      ['tempCode', '模板编码', 'str', { kw: true, width: 120 }],
      ['tempName', '模板名称', 'str', { kw: true }],
      ['billCycle', '出账周期', 'str', { filter: true, width: 90 }],
      ['feeTypes', '包含费用', 'str', {}],
      statusCol, updateCol
    ],
    row: i => ({ tempCode: 'BT-' + pad(i + 1), tempName: '示例账单模板' + (i + 1), billCycle: pick(['月结', '半月结', '周结'], i),
      feeTypes: pick(['操作费,仓租', '操作费,仓租,运费', '全部'], i), ...updated(i) })
  }
]
