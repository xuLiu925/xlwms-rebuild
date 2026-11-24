// 左侧菜单分组。前 9 组（入库……基础数据）的顺序、组名、子菜单名与路径，取自领星 WMS 页面菜单的只读观察；
// 那是一个权限受限账号能看到的部分。之后的分组是该账号看不到、但公开路由表里有的模块，沿用本项目原来的中文名。
// 每项为 [路由 key, 菜单名]；菜单名省略时用 pageTitles / 通用列表页标题。
// link：没有子菜单的一级菜单直接指向一个页面。
// 参考菜单里有、本项目还没有页面的路径，会自动生成“待实现”占位路由（见 router/index.js）。
export const menuGroups = [
  {
    key: 'inbound', title: '入库', icon: 'el-icon-download',
    items: [
      ['inbound/arrivalScan', '到仓扫描'], ['inbound/ASN', '入库管理'], ['inbound/shelf', '上架管理'],
      ['inbound/newProduct', '新品维护'], ['inbound/inventoryClaim', '入库认领'],
      // 以下为参考菜单里没有显示、但公开路由表里有的入库页面
      ['inbound/receivePutaway'], ['inbound/deliveryGoods'], ['inbound/palletReceive'], ['inbound/palletForming']
    ]
  },
  {
    key: 'outbound', title: '出库', icon: 'el-icon-upload2',
    items: [
      ['outbound/parcel', '一件代发'], ['outbound/freight', '备货中转'], ['outbound/wave', '波次管理'],
      ['outbound/secondSort', '二次分拣'], ['outbound/review', '复核/验货'], ['outbound/boxReview', '包裹复核'],
      ['outbound/weight', '称重'], ['outbound/swapBarcode', '换标'], ['outbound/split', '拆单'],
      ['outbound/swapOrder', '换单'], ['outbound/abnormal', '异常件'], ['outbound/intercept', '截单'],
      ['outbound/waveRule'], ['outbound/scanForm']
    ]
  },
  // 参考系统的“退件”“工单”路径是 /returns/list、/workOrder/list，本项目对应的列表页在全局订单下
  { key: 'returns', title: '退件', icon: 'el-icon-refresh-left', link: 'globalOrder/returnOrder' },
  { key: 'transfer', title: '转运', icon: 'el-icon-truck', link: 'transfer/index' },
  { key: 'workOrder', title: '工单', icon: 'el-icon-tickets', link: 'globalOrder/workOrder' },
  {
    key: 'report', title: '报表', icon: 'el-icon-data-analysis',
    items: [
      ['report/staffEfficiency', '人效看板'], ['report/inbound', '入库效率'], ['report/outbound', '出库效率'],
      ['statisticalAnalysis/inventoryAgeAnalysis']
    ]
  },
  {
    key: 'returnGoods', title: 'FBA退货', icon: 'el-icon-sell',
    items: [['returnGoods/warehousing', '退货入库'], ['returnGoods/rebrand', '换标服务'], ['returnGoods/outbound', '退货出库']]
  },
  {
    key: 'stock', title: '库内', icon: 'el-icon-house',
    items: [
      ['stock/sku', '产品库存'], ['stock/box', '箱库存'], ['stock/return', '退货库存'], ['stock/defective', '次品处理'],
      ['stock/checking', '盘点'], ['stock/serial', '序列号管理'], ['stock/workVideo', '作业视频']
    ]
  },
  {
    key: 'basicData', title: '基础数据', icon: 'el-icon-s-grid',
    items: [
      ['basicData/product', '产品'], ['basicData/area', '库区'], ['basicData/cell', '库位'], ['basicData/packMaterial', '包材'],
      ['basicData/seedWall', '播种墙'], ['basicData/pickCart', '拣货车'], ['basicData/tray']
    ]
  },
  // ---- 以下模块在参考账号的菜单里看不到 ----
  {
    key: 'globalOrder', title: '全局订单', icon: 'el-icon-s-order',
    items: [['globalOrder/asn'], ['globalOrder/parcel'], ['globalOrder/freight'], ['globalOrder/directMailOrder'],
      ['globalOrder/directMailBox'], ['globalOrder/claim']]
  },
  { key: 'customer', title: '客户', icon: 'el-icon-user', items: [['customer/list'], ['customer/recharge']] },
  { key: 'logisticsManage', title: '物流管理', icon: 'el-icon-position', items: [['logisticsManage/channel'], ['logisticsManage/channelGroup']] },
  {
    key: 'logisticsPrice', title: '物流报价', icon: 'el-icon-price-tag',
    items: [['logisticsPrice/fuelPlan'], ['logisticsPrice/zoneTemp'], ['logisticsPrice/surcharge'], ['logisticsPrice/freightTemp']]
  },
  {
    key: 'quotesReceivable', title: '应收报价', icon: 'el-icon-document',
    items: [['quotesReceivable/logisticsPriceSetting'], ['quotesReceivable/operateBill'], ['quotesReceivable/warehouseRent'],
      ['quotesReceivable/quotationScheme']]
  },
  {
    key: 'receivableSettlement', title: '应收结算', icon: 'el-icon-wallet',
    items: [['receivableSettlement/businessExpenses'], ['receivableSettlement/billsReceivable'], ['receivableSettlement/expend'],
      ['receivableSettlement/billTemplate']]
  },
  { key: 'auth', title: '权限', icon: 'el-icon-lock', items: [['auth/account'], ['auth/role']] },
  {
    key: 'setting', title: '设置', icon: 'el-icon-setting',
    items: [['setting/globalSetting'], ['setting/printTemplate'], ['setting/notice'], ['setting/brand'], ['setting/agency'],
      ['setting/erpAuth'], ['setting/msgNotice'], ['setting/warningMsg'], ['setting/receiveAccount'], ['setting/exchangeRate']]
  },
  {
    key: 'toolKit', title: '工具箱', icon: 'el-icon-suitcase',
    items: [['toolKit/freightCalculation'], ['toolKit/requestLog'], ['toolKit/errorPool'], ['toolKit/labelRecord'],
      ['toolKit/pushRecord'], ['toolKit/zoneTest'], ['toolKit/taskCenter']]
  }
]
