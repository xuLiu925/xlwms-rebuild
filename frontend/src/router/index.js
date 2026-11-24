import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)
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
const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: () => import('../views/Dashboard.vue') },
  ...Object.entries(modules).map(([m, kids]) => ({
    path: '/' + m, component: { render: h => h('router-view') },
    children: kids.map(k => ({ path: k, component: m + '/' + k === 'inbound/ASN' ? () => import('../views/inbound/ASN.vue') : Stub(m + '/' + k) }))
  }))
]
const router = new Router({ mode: 'history', routes })
router.beforeEach((to, from, next) => {
  if (to.path !== '/login' && !localStorage.getItem('token')) return next({ path: '/login', query: { redirect: to.fullPath } })
  next()
})
export default router
