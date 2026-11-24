import Vue from 'vue'
import Router from 'vue-router'
import listPages from '../config/listPages.generated'
import { moduleTitles, pageTitles } from '../config/menu'
import Layout from '../views/layout/Layout.vue'
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
  'basicData/packMaterial': () => import('../views/basicData/PackMaterial.vue'),
  // 批次 3：作业/配置/工具页（本地有状态示例，接口路径为本项目占位）
  'inbound/receivePutaway': () => import('../views/inbound/ReceivePutaway.vue'),
  'inbound/shelf': () => import('../views/inbound/Shelf.vue'),
  'outbound/secondSort': () => import('../views/outbound/SecondSort.vue'),
  'outbound/review': () => import('../views/outbound/Review.vue'),
  'outbound/boxReview': () => import('../views/outbound/BoxReview.vue'),
  'outbound/weight': () => import('../views/outbound/Weight.vue'),
  'statisticalAnalysis/inventoryAgeAnalysis': () => import('../views/statistics/InventoryAge.vue'),
  'setting/globalSetting': () => import('../views/setting/GlobalSetting.vue'),
  'toolKit/freightCalculation': () => import('../views/toolKit/FreightCalculation.vue'),
  'toolKit/zoneTest': () => import('../views/toolKit/ZoneTest.vue')
}
const childRoute = (m, k) => {
  const key = m + '/' + k
  const path = '/' + key
  if (pages[key]) return { path, component: pages[key] }
  // 通用只读列表页（配置由 tools/list-pages/gen.mjs 生成）
  if (listConfigs[key]) return { path, component: GenericList, props: { config: listConfigs[key] } }
  return { path, component: Stub(key) }
}
const pageRoutes = Object.entries(modules).flatMap(([m, kids]) => kids.map(k => childRoute(m, k)))
// 左侧菜单：模块 → 子页面
const menu = Object.entries(modules).map(([m, kids]) => ({
  path: '/' + m,
  title: moduleTitles[m] || m,
  children: kids.map(k => {
    const key = m + '/' + k
    return { path: '/' + key, title: pageTitles[key] || (listConfigs[key] && listConfigs[key].title) || key, stub: !pages[key] && !listConfigs[key] }
  })
}))
const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  {
    path: '/', component: Layout, props: { menu }, redirect: '/dashboard',
    children: [
      { path: '/dashboard', component: () => import('../views/Dashboard.vue') },
      // 本地演示页：模拟 PDA 出库作业，只连本地后端
      { path: '/pdaDemo', component: () => import('../views/pda/OutboundDemo.vue') },
      ...pageRoutes
    ]
  }
]
const router = new Router({ mode: 'history', routes })
router.beforeEach((to, from, next) => {
  if (to.path !== '/login' && !localStorage.getItem('token')) return next({ path: '/login', query: { redirect: to.fullPath } })
  next()
})
export default router
