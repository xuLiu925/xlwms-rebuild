// 左侧菜单的模块名与非通用页面的标题（通用列表页的标题来自 listPages.generated.js）。
// 模块与子路由取自公开 bundle 的路由表；中文名是本项目的翻译，未必与真实菜单一致。
export const moduleTitles = {
  customer: '客户',
  globalOrder: '全局订单',
  stock: '库存',
  basicData: '基础数据',
  inbound: '入库',
  outbound: '出库',
  returnGoods: '退货',
  logisticsManage: '物流管理',
  logisticsPrice: '物流报价',
  quotesReceivable: '应收报价',
  receivableSettlement: '应收结算',
  statisticalAnalysis: '统计分析',
  auth: '权限',
  setting: '设置',
  toolKit: '工具箱'
}

export const pageTitles = {
  'inbound/ASN': '入库管理',
  'inbound/receivePutaway': '收货上架',
  'inbound/shelf': '上架',
  'outbound/parcel': '一件代发',
  'outbound/wave': '波次',
  'outbound/secondSort': '二次分拣',
  'outbound/review': '复核',
  'outbound/boxReview': '按箱复核',
  'outbound/weight': '称重出库',
  'stock/sku': '产品库存',
  'stock/box': '按箱库存',
  'stock/return': '退货库存',
  'basicData/product': '产品资料',
  'basicData/area': '库区',
  'basicData/cell': '库位',
  'basicData/tray': '托盘',
  'basicData/packMaterial': '包材',
  'statisticalAnalysis/inventoryAgeAnalysis': '库龄分析',
  'setting/globalSetting': '全局设置',
  'toolKit/freightCalculation': '运费试算',
  'toolKit/zoneTest': '分区测试'
}
