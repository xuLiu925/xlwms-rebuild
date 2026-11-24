import Vue from 'vue'
import Router from 'vue-router'
import listPages from '../config/listPages.generated'
Vue.use(Router)
const GenericList = () => import('../views/common/GenericList.vue')
const listConfigs = Object.fromEntries(listPages.map(c => [c.route, c]))
const Stub = name => ({ render: h => h('div', { style: 'padding:24px' }, name + '（待实现）') })
// 模块与子路由取自公开 bundle 的路由表
const modules = {
  customer: ['list', 'recharge'],
  globalOrder: ['asn', 'parcel', 'freight', 'returnOrder', 'directMailOrder', 'directMailBox', 'claim', 'workOrder'],
  stock: ['sku', 'box', 'return'],
  basicData: ['product', 'area', 'cell', 'tray', 'packMaterial', 'seedWall', 'pickCart'],
  inbound: ['arrivalScan', 'ASN', 'deliveryGoods', 'receivePutaway', 'palletReceive', 'shelf', 'palletForming', 'newProduct', 'inventoryClaim'],
  outbound: ['parcel', 'freight', 'wave', 'waveRule', 'secondSort', 'review', 'boxReview', 'weight', 'scanForm', 'split', 'swapOrder', 'abnormal', 'intercept'],
  returnGoods: ['warehousing', 'rebrand', 'outbound'],
  logisticsManage: ['channel', 'channelGroup'],
  logisticsPrice: ['fuelPlan', 'zoneTemp', 'surcharge', 'freightTemp'],
  quotesReceivable: ['logisticsPriceSetting', 'operateBill', 'warehouseRent', 'quotationScheme'],
  receivableSettlement: ['businessExpenses', 'billsReceivable', 'expend', 'billTemplate'],
  statisticalAnalysis: ['inventoryAgeAnalysis'],
  auth: ['account', 'role'],
  setting: ['globalSetting', 'printTemplate', 'notice', 'brand', 'agency', 'erpAuth', 'msgNotice', 'warningMsg', 'receiveAccount', 'exchangeRate'],
  toolKit: ['freightCalculation', 'requestLog', 'errorPool', 'labelRecord', 'pushRecord', 'zoneTest', 'taskCenter']
}
// 已实现的页面；其余子路由显示占位
const pages = {
  'inbound/ASN': () => import('../views/inbound/ASN.vue'),
  'outbound/parcel': () => import('../views/outbound/Parcel.vue'),
  'outbound/wave': () => import('../views/outbound/Wave.vue'),
  'stock/sku': () => import('../views/stock/Sku.vue'),
  'stock/box': () => import('../views/stock/Box.vue'),
  'stock/return': () => import('../views/stock/Return.vue'),
  'basicData/product': () => import('../views/basicData/Product.vue'),
  'basicData/area': () => import('../views/basicData/Area.vue'),
  'basicData/cell': () => import('../views/basicData/Cell.vue'),
  'basicData/tray': () => import('../views/basicData/Tray.vue'),
  'basicData/packMaterial': () => import('../views/basicData/PackMaterial.vue')
}
const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: () => import('../views/Dashboard.vue') },
  // 本地演示页：模拟 PDA 出库作业，只连本地后端
  { path: '/pdaDemo', component: () => import('../views/pda/OutboundDemo.vue') },
  ...Object.entries(modules).map(([m, kids]) => ({
    path: '/' + m, component: { render: h => h('router-view') },
    children: kids.map(k => {
      const key = m + '/' + k
      if (pages[key]) return { path: k, component: pages[key] }
      // 通用只读列表页（配置由 tools/list-pages/gen.mjs 生成）
      if (listConfigs[key]) return { path: k, component: GenericList, props: { config: listConfigs[key] } }
      return { path: k, component: Stub(key) }
    })
  }))
]
const router = new Router({ mode: 'history', routes })
router.beforeEach((to, from, next) => {
  if (to.path !== '/login' && !localStorage.getItem('token')) return next({ path: '/login', query: { redirect: to.fullPath } })
  next()
})
export default router
