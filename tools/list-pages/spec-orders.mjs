// 批次 2：全局订单 / 入库 / 出库 / 退货中的只读列表页。路径、字段、编码均为本项目占位，数据虚构。
import { pad, pick, day, time, WH, CUS, cus, ON_OFF, CARRIERS, CHANNELS, no, fix2, ID } from './helpers.mjs'

const ASN_STATUS = { 10: ['在途', 'info'], 11: ['收货中', 'warning'], 15: ['已收货', 'primary'], 30: ['已上架', 'success'], 90: ['已取消', 'danger'] }
const PARCEL_STATUS = { 10: ['待审核', 'info'], 20: ['待拣货', 'info'], 30: ['拣货中', 'warning'], 40: ['待复核', 'warning'], 50: ['已出库', 'success'], 80: ['已拦截', 'danger'], 90: ['已取消', 'danger'] }
const RETURN_STATUS = { 10: ['待收货', 'info'], 20: ['已收货', 'warning'], 30: ['已上架', 'success'], 90: ['已取消', 'danger'] }

export const orders = [
  // ---------- globalOrder 全局订单 ----------
  {
    key: 'globalAsn', route: 'globalOrder/asn', title: '全局入库单', idBase: ID(0), count: 16,
    columns: [
      ['asnNo', '入库单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['customerName', '客户名称', 'str', { width: 110 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['boxCount', '箱数', 'int', { width: 70 }],
      ['skuQty', '产品数量', 'int', { width: 90 }],
      ['status', '状态', 'int', { enum: ASN_STATUS, tabs: true, width: 90 }],
      ['etaDate', '预计到达', 'str', { width: 110 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ asnNo: no('ASN', i), ...CUS(i), whCode: pick(WH, i), boxCount: 2 + (i % 9), skuQty: 20 + i * 7,
      status: pick([10, 11, 15, 30, 30, 90], i), etaDate: day(i, 4), createTime: time(i) })
  },
  {
    key: 'globalParcel', route: 'globalOrder/parcel', title: '全局一件代发', idBase: ID(1), count: 18,
    columns: [
      ['orderNo', '出库单号', 'str', { kw: true, width: 150 }],
      ['platformOrderNo', '平台单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['channelName', '物流渠道', 'str', { filter: true, width: 110 }],
      ['trackingNo', '跟踪号', 'str', { kw: true, width: 140 }],
      ['skuQty', '数量', 'int', { width: 70 }],
      ['status', '状态', 'int', { enum: PARCEL_STATUS, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = pick([10, 20, 30, 40, 50, 50, 80, 90], i)
      return { orderNo: no('OB', i), platformOrderNo: 'PO-DEMO-' + pad(i + 1, 5), customerCode: cus(i), whCode: pick(WH, i),
        channelName: pick(CHANNELS, i), trackingNo: s >= 40 && s <= 50 ? 'TRK' + pad(100000 + i * 17, 8) : '', skuQty: 1 + (i % 3), status: s, createTime: time(i) }
    }
  },
  {
    key: 'globalFreight', route: 'globalOrder/freight', title: '全局大货出库', idBase: ID(2), count: 12,
    columns: [
      ['orderNo', '出库单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['boxCount', '箱数', 'int', { width: 70 }],
      ['palletCount', '托数', 'int', { width: 70 }],
      ['deliveryType', '出库方式', 'str', { filter: true, width: 90 }],
      ['status', '状态', 'int', { enum: { 10: ['待处理', 'info'], 20: ['作业中', 'warning'], 30: ['已出库', 'success'], 90: ['已取消', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ orderNo: no('FO', i), customerCode: cus(i), whCode: pick(WH, i), boxCount: 10 + i * 3, palletCount: 1 + (i % 4),
      deliveryType: pick(['卡派', '自提', '快递'], i), status: pick([10, 20, 30, 30, 90], i), createTime: time(i) })
  },
  {
    key: 'globalReturn', route: 'globalOrder/returnOrder', title: '全局退货单', idBase: ID(3), count: 14,
    columns: [
      ['returnNo', '退货单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['returnType', '退货类型', 'str', { filter: true, width: 100 }],
      ['skuQty', '数量', 'int', { width: 70 }],
      ['status', '状态', 'int', { enum: RETURN_STATUS, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ returnNo: no('GR', i), customerCode: cus(i), whCode: pick(WH, i), returnType: pick(['买家退货', '拒收', '异常件'], i),
      skuQty: 1 + (i % 5), status: pick([10, 20, 30, 90], i), createTime: time(i) })
  },
  {
    key: 'directMailOrder', route: 'globalOrder/directMailOrder', title: '直邮订单', idBase: ID(4), count: 12,
    columns: [
      ['orderNo', '订单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['channelName', '物流渠道', 'str', { filter: true, width: 110 }],
      ['trackingNo', '跟踪号', 'str', { kw: true, width: 140 }],
      ['weightKg', '重量(kg)', 'num', { width: 90 }],
      ['status', '状态', 'int', { enum: { 10: ['待打单', 'info'], 20: ['已打单', 'warning'], 30: ['已交接', 'success'], 90: ['已取消', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = pick([10, 20, 30, 30, 90], i)
      return { orderNo: no('DM', i), customerCode: cus(i), channelName: pick(CHANNELS, i), trackingNo: s === 20 || s === 30 ? 'DMT' + pad(200000 + i * 11, 8) : '',
        weightKg: fix2(0.3 + i * 0.27), status: s, createTime: time(i) }
    }
  },
  {
    key: 'directMailBox', route: 'globalOrder/directMailBox', title: '直邮箱', idBase: ID(5), count: 10,
    columns: [
      ['boxNo', '箱号', 'str', { kw: true, width: 150 }],
      ['channelName', '物流渠道', 'str', { filter: true, width: 110 }],
      ['orderCount', '订单数', 'int', { width: 80 }],
      ['weightKg', '重量(kg)', 'num', { width: 90 }],
      ['status', '状态', 'int', { enum: { 10: ['装箱中', 'warning'], 20: ['已封箱', 'primary'], 30: ['已交接', 'success'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ boxNo: no('DMB', i), channelName: pick(CHANNELS, i), orderCount: 5 + i * 2, weightKg: fix2(3 + i * 1.3),
      status: pick([10, 20, 30], i), createTime: time(i) })
  },
  {
    key: 'claim', route: 'globalOrder/claim', title: '理赔单', idBase: ID(6), count: 10,
    columns: [
      ['claimNo', '理赔单号', 'str', { kw: true, width: 150 }],
      ['orderNo', '关联单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['claimType', '理赔类型', 'str', { filter: true, width: 90 }],
      ['claimAmount', '理赔金额', 'money', { width: 100 }],
      ['currency', '币种', 'str', { filter: true, width: 70 }],
      ['status', '状态', 'int', { enum: { 10: ['待处理', 'info'], 20: ['处理中', 'warning'], 30: ['已赔付', 'success'], 40: ['已驳回', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ claimNo: no('CL', i), orderNo: no('OB', i + 30), customerCode: cus(i), claimType: pick(['丢件', '破损', '少件'], i),
      claimAmount: fix2(15 + i * 12.5), currency: pick(['USD', 'EUR'], i), status: pick([10, 20, 30, 40], i), createTime: time(i) })
  },
  {
    key: 'workOrder', route: 'globalOrder/workOrder', title: '工单', idBase: ID(7), count: 14,
    columns: [
      ['workOrderNo', '工单号', 'str', { kw: true, width: 150 }],
      ['title', '标题', 'str', { kw: true }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['workType', '工单类型', 'str', { filter: true, width: 90 }],
      ['priority', '优先级', 'str', { filter: true, width: 80 }],
      ['status', '状态', 'int', { enum: { 10: ['待受理', 'info'], 20: ['处理中', 'warning'], 30: ['已完成', 'success'], 90: ['已关闭', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const t = pick(['换标', '贴标', '拍照', '核查'], i)
      return { workOrderNo: no('WO', i), title: '示例工单-' + t + (i + 1), customerCode: cus(i), workType: t, priority: pick(['高', '中', '低'], i),
        status: pick([10, 20, 30, 30, 90], i), createTime: time(i) }
    }
  },
  // ---------- inbound 入库（列表类） ----------
  {
    key: 'arrivalScan', route: 'inbound/arrivalScan', title: '到货登记', idBase: ID(8), count: 12,
    columns: [
      ['scanNo', '登记号', 'str', { kw: true, width: 150 }],
      ['asnNo', '入库单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['boxCount', '到货箱数', 'int', { width: 90 }],
      ['scanUser', '登记人', 'str', { width: 110 }],
      ['status', '匹配状态', 'int', { enum: { 1: ['已匹配', 'success'], 0: ['未匹配', 'warning'] }, tabs: true, width: 90 }],
      ['scanTime', '登记时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = i % 4 === 3 ? 0 : 1
      return { scanNo: no('AR', i), asnNo: s ? no('ASN', i) : '', customerCode: s ? cus(i) : '', whCode: pick(WH, i), boxCount: 1 + (i % 8),
        scanUser: '示例员工' + ((i % 3) + 1), status: s, scanTime: time(i) }
    }
  },
  {
    key: 'deliveryGoods', route: 'inbound/deliveryGoods', title: '送货预约', idBase: ID(9), count: 10,
    columns: [
      ['appointNo', '预约号', 'str', { kw: true, width: 150 }],
      ['asnNo', '入库单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['appointDate', '预约日期', 'str', { width: 110 }],
      ['vehicleType', '车型', 'str', { filter: true, width: 100 }],
      ['status', '状态', 'int', { enum: { 10: ['待确认', 'info'], 20: ['已确认', 'warning'], 30: ['已到仓', 'success'], 90: ['已取消', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ appointNo: no('AP', i), asnNo: no('ASN', i), customerCode: cus(i), whCode: pick(WH, i), appointDate: day(i, 5),
      vehicleType: pick(['小货车', '厢式货车', '集装箱'], i), status: pick([10, 20, 30, 30, 90], i), createTime: time(i) })
  },
  {
    key: 'palletReceive', route: 'inbound/palletReceive', title: '托盘收货记录', idBase: ID(10), count: 12,
    columns: [
      ['receiptNo', '收货单号', 'str', { kw: true, width: 150 }],
      ['asnNo', '入库单号', 'str', { kw: true, width: 150 }],
      ['trayNo', '托盘号', 'str', { kw: true, width: 120 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['boxCount', '箱数', 'int', { width: 70 }],
      ['skuQty', '产品数量', 'int', { width: 90 }],
      ['operator', '操作人', 'str', { width: 110 }],
      ['status', '状态', 'int', { enum: { 10: ['收货中', 'warning'], 20: ['已完成', 'success'] }, tabs: true, width: 90 }],
      ['receiptTime', '收货时间', 'time', { width: 160 }]
    ],
    row: i => ({ receiptNo: no('PR', i), asnNo: no('ASN', i), trayNo: 'TRAY' + pad(i + 1, 6), customerCode: cus(i), whCode: pick(WH, i),
      boxCount: 4 + (i % 6), skuQty: 40 + i * 5, operator: '示例员工' + ((i % 4) + 1), status: i % 4 === 0 ? 10 : 20, receiptTime: time(i) })
  },
  {
    key: 'palletForming', route: 'inbound/palletForming', title: '组托记录', idBase: ID(11), count: 10,
    columns: [
      ['formNo', '组托单号', 'str', { kw: true, width: 150 }],
      ['trayNo', '托盘号', 'str', { kw: true, width: 120 }],
      ['asnNo', '入库单号', 'str', { kw: true, width: 150 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['boxCount', '箱数', 'int', { width: 70 }],
      ['operator', '操作人', 'str', { width: 110 }],
      ['status', '状态', 'int', { enum: { 10: ['组托中', 'warning'], 20: ['已完成', 'primary'], 30: ['已上架', 'success'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ formNo: no('PF', i), trayNo: 'TRAY' + pad(i + 21, 6), asnNo: no('ASN', i + 3), whCode: pick(WH, i), boxCount: 6 + (i % 5),
      operator: '示例员工' + ((i % 3) + 1), status: pick([10, 20, 30], i), createTime: time(i) })
  },
  {
    key: 'newProduct', route: 'inbound/newProduct', title: '新品维护', idBase: ID(12), count: 12,
    columns: [
      ['sku', 'SKU', 'str', { kw: true, width: 110 }],
      ['productName', '产品名称', 'str', { kw: true }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['asnNo', '来源入库单', 'str', { width: 150 }],
      ['lengthCm', '长(cm)', 'num', { width: 80 }],
      ['widthCm', '宽(cm)', 'num', { width: 80 }],
      ['heightCm', '高(cm)', 'num', { width: 80 }],
      ['weightKg', '重(kg)', 'num', { width: 80 }],
      ['status', '状态', 'int', { enum: { 0: ['待测量', 'warning'], 1: ['已测量', 'success'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = i % 3 === 0 ? 0 : 1
      return { sku: 'NEW-' + pad(i + 1, 4), productName: '示例新品' + (i + 1), customerCode: cus(i), asnNo: no('ASN', i),
        lengthCm: fix2(s ? 10 + i : 0), widthCm: fix2(s ? 8 + i * 0.5 : 0), heightCm: fix2(s ? 5 + i * 0.3 : 0), weightKg: fix2(s ? 0.2 + i * 0.1 : 0),
        status: s, createTime: time(i) }
    }
  },
  {
    key: 'inventoryClaim', route: 'inbound/inventoryClaim', title: '库存认领', idBase: ID(13), count: 10,
    columns: [
      ['claimNo', '认领单号', 'str', { kw: true, width: 150 }],
      ['barcode', '条码', 'str', { kw: true, width: 130 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['cellCode', '暂存库位', 'str', { width: 110 }],
      ['qty', '数量', 'int', { width: 70 }],
      ['claimCustomerCode', '认领客户', 'str', { filter: true, width: 100 }],
      ['status', '状态', 'int', { enum: { 10: ['待认领', 'warning'], 20: ['已认领', 'success'], 30: ['已作废', 'info'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = pick([10, 20, 20, 30], i)
      return { claimNo: no('IC', i), barcode: 'UNK' + pad(500000 + i * 7, 8), whCode: pick(WH, i), cellCode: 'TMP-' + pad(i + 1, 2), qty: 1 + (i % 6),
        claimCustomerCode: s === 20 ? cus(i) : '', status: s, createTime: time(i) }
    }
  },
  // ---------- outbound 出库（列表类） ----------
  {
    key: 'outFreight', route: 'outbound/freight', title: '大货出库', idBase: ID(14), count: 12,
    columns: [
      ['orderNo', '出库单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['boxCount', '箱数', 'int', { width: 70 }],
      ['palletCount', '托数', 'int', { width: 70 }],
      ['deliveryType', '出库方式', 'str', { filter: true, width: 90 }],
      ['appointTime', '预约提货', 'time', { width: 160 }],
      ['status', '状态', 'int', { enum: { 10: ['待拣货', 'info'], 20: ['拣货中', 'warning'], 30: ['待装柜', 'warning'], 40: ['已出库', 'success'], 90: ['已取消', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ orderNo: no('FO', i + 20), customerCode: cus(i), whCode: pick(WH, i), boxCount: 8 + i * 4, palletCount: 1 + (i % 3),
      deliveryType: pick(['卡派', '自提', '快递'], i), appointTime: time(i, 3, 14), status: pick([10, 20, 30, 40, 40, 90], i), createTime: time(i) })
  },
  {
    key: 'waveRule', route: 'outbound/waveRule', title: '波次规则', idBase: ID(15), count: 8,
    columns: [
      ['ruleCode', '规则编码', 'str', { kw: true, width: 120 }],
      ['ruleName', '规则名称', 'str', { kw: true }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['orderType', '订单类型', 'str', { filter: true, width: 100 }],
      ['maxOrderCount', '单波次上限', 'int', { width: 100 }],
      ['priority', '优先级', 'int', { width: 80 }],
      ['status', '状态', 'int', { enum: ON_OFF, tabs: true, width: 80 }],
      ['updateTime', '更新时间', 'time', { width: 160 }]
    ],
    row: i => ({ ruleCode: 'WR-' + pad(i + 1), ruleName: '示例波次规则' + (i + 1), whCode: pick(WH, i), orderType: pick(['一票一件', '一票多件', '混合'], i),
      maxOrderCount: pick([50, 100, 200], i), priority: i + 1, status: i === 7 ? 0 : 1, updateTime: time(i) })
  },
  {
    key: 'scanForm', route: 'outbound/scanForm', title: '交接单', idBase: ID(16), count: 12,
    columns: [
      ['formNo', '交接单号', 'str', { kw: true, width: 150 }],
      ['carrier', '承运商', 'str', { filter: true, width: 120 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['parcelCount', '包裹数', 'int', { width: 80 }],
      ['totalWeightKg', '总重(kg)', 'num', { width: 90 }],
      ['status', '状态', 'int', { enum: { 10: ['待交接', 'warning'], 20: ['已交接', 'success'] }, tabs: true, width: 90 }],
      ['handoverTime', '交接时间', 'time', { width: 160 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = i % 3 === 0 ? 10 : 20
      return { formNo: no('SF', i), carrier: pick(CARRIERS, i), whCode: pick(WH, i), parcelCount: 20 + i * 6, totalWeightKg: fix2(15 + i * 4.4),
        status: s, handoverTime: s === 20 ? time(i, 1, 17) : '', createTime: time(i) }
    }
  },
  {
    key: 'splitOrder', route: 'outbound/split', title: '拆单记录', idBase: ID(17), count: 10,
    columns: [
      ['orderNo', '原出库单号', 'str', { kw: true, width: 150 }],
      ['newOrderNos', '拆分后单号', 'str', { kw: true }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['splitReason', '拆单原因', 'str', { filter: true, width: 100 }],
      ['operator', '操作人', 'str', { width: 110 }],
      ['splitTime', '拆单时间', 'time', { width: 160 }]
    ],
    row: i => ({ orderNo: no('OB', i + 60), newOrderNos: no('OB', i + 60) + '-1,' + no('OB', i + 60) + '-2', customerCode: cus(i),
      splitReason: pick(['超重', '缺货', '渠道限制'], i), operator: '示例员工' + ((i % 3) + 1), splitTime: time(i) })
  },
  {
    key: 'swapOrder', route: 'outbound/swapOrder', title: '换单记录', idBase: ID(18), count: 10,
    columns: [
      ['orderNo', '出库单号', 'str', { kw: true, width: 150 }],
      ['oldTrackingNo', '原跟踪号', 'str', { kw: true, width: 140 }],
      ['newTrackingNo', '新跟踪号', 'str', { kw: true, width: 140 }],
      ['channelName', '物流渠道', 'str', { filter: true, width: 110 }],
      ['reason', '换单原因', 'str', {}],
      ['status', '状态', 'int', { enum: { 10: ['待换单', 'warning'], 20: ['已换单', 'success'], 30: ['失败', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => {
      const s = pick([10, 20, 20, 30], i)
      return { orderNo: no('OB', i + 80), oldTrackingNo: 'TRK' + pad(300000 + i * 9, 8), newTrackingNo: s === 20 ? 'TRK' + pad(400000 + i * 9, 8) : '',
        channelName: pick(CHANNELS, i), reason: pick(['面单破损', '渠道变更', '地址修改'], i), status: s, createTime: time(i) }
    }
  },
  // ---------- returnGoods 退货（列表类） ----------
  {
    key: 'rebrand', route: 'returnGoods/rebrand', title: '换标任务', idBase: ID(19), count: 10,
    columns: [
      ['taskNo', '任务号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['sku', 'SKU', 'str', { kw: true, width: 110 }],
      ['qty', '数量', 'int', { width: 70 }],
      ['newBarcode', '新条码', 'str', { kw: true, width: 130 }],
      ['status', '状态', 'int', { enum: { 10: ['待处理', 'info'], 20: ['处理中', 'warning'], 30: ['已完成', 'success'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ taskNo: no('RB', i), customerCode: cus(i), whCode: pick(WH, i), sku: 'SKU-' + pad(i + 1, 4), qty: 1 + (i % 7),
      newBarcode: 'NB' + pad(700000 + i * 3, 8), status: pick([10, 20, 30], i), createTime: time(i) })
  },
  {
    key: 'returnOutbound', route: 'returnGoods/outbound', title: '退货出库', idBase: ID(20), count: 10,
    columns: [
      ['orderNo', '出库单号', 'str', { kw: true, width: 150 }],
      ['returnNo', '退货单号', 'str', { kw: true, width: 150 }],
      ['customerCode', '客户代码', 'str', { filter: true, width: 100 }],
      ['whCode', '仓库', 'str', { filter: true, width: 80 }],
      ['skuQty', '数量', 'int', { width: 70 }],
      ['outboundType', '出库类型', 'str', { filter: true, width: 100 }],
      ['status', '状态', 'int', { enum: { 10: ['待出库', 'warning'], 20: ['已出库', 'success'], 90: ['已取消', 'danger'] }, tabs: true, width: 90 }],
      ['createTime', '创建时间', 'time', { width: 160 }]
    ],
    row: i => ({ orderNo: no('RO', i), returnNo: no('RT', i), customerCode: cus(i), whCode: pick(WH, i), skuQty: 1 + (i % 4),
      outboundType: pick(['退回客户', '销毁', '转仓'], i), status: pick([10, 20, 20, 90], i), createTime: time(i) })
  }
]
