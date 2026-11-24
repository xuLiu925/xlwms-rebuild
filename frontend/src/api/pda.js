// PDA 模拟界面（批次 10）用到的接口。路径、参数与请求体按 APK ApiService 还原。
// 其中大部分在真实系统是写操作：只允许对接本地后端（vue.config.js 代理到本机），
// 绝不要把 API_TARGET 指向生产网关。
import request from './request'

const WH = 'WH01'
const get = (url, params) => request.get(url, { params: { whCode: WH, ...params } })
const post = (url, body) => request.post(url, { whCode: WH, ...body })

// ---------- 收货 /pda/receipt ----------
export const receiptList = () => get('/pda/receipt/getAllReceiptList')
export const receiptSearch = param => get('/pda/receipt/getAsn', { param })
export const receiptOrder = asnNo => get('/pda/receipt/getOrderDtl', { asnNo })
export const receiptSkuPage = asnNo => post('/pda/receipt/getOrderDtl/skuDetailPage', { asnNo, current: 1, size: 100 })
export const receiptBox = (asnNo, boxBarcode) => get('/pda/receipt/v2/getBoxDtl', { asnNo, boxBarcode })
export const receiptBoxTypes = asnNo => get('/pda/receipt/listBoxType', { asnNo, current: 1, size: 100 })
export const receiveBoxes = body => post('/pda/receipt/wholeBoxReceipt', body)
export const finishBoxes = body => post('/pda/receipt/finishWholeBoxReceipt', body)
export const receiveBoxTypes = body => post('/pda/receipt/BoxTypeReceipt', body)
export const finishBoxTypes = body => post('/pda/receipt/finishBoxTypeReceipt', body)
export const receiveSkus = body => post('/pda/receipt/skuReceipt', body)
export const receiveWholeOrder = body => post('/pda/receipt/wholeOrderReceipt', body)

// ---------- 上架 /pda/shelf ----------
export const shelfList = () => get('/pda/shelf/merge/page', { current: 1, size: 50 })
export const shelfSearch = content => get('/pda/shelf/merge/scanContent', { content })
export const shelfDetail = putawayNo => get('/pda/shelf/v2/getRecommendCell/v2', { putawayNo, current: 1, size: 100 })
export const shelfScanCell = (cellNo, putawayNo) => get('/pda/shelf/scanCell', { cellNo, putawayNo })
export const shelfConfirm = body => post('/pda/shelf/v2/confirm', body)
export const shelfCompleteAll = body => post('/pda/shelf/commitCompleteOrder', body)

// ---------- 盘点 /pda/inventoryCheck ----------
export const checkList = () => post('/pda/inventoryCheck/list', { current: 1, size: 50 })
export const checkStart = orderSn => post('/pda/inventoryCheck/start', { orderSn })
export const checkOverview = orderSn => post('/pda/inventoryCheck/overview', { orderSn })
export const checkScanSku = body => post('/pda/inventoryCheck/scanSku', body)
export const checkSubmitCell = body => post('/pda/inventoryCheck/submitCell', body)
export const checkComplete = orderSn => post('/pda/inventoryCheck/complete', { orderSn })

// ---------- 库内 /pda/stock ----------
export const stockSearch = queryParam => post('/pda/stock/search', { queryParam, current: 1, size: 50 })
export const cellsOfSku = productSku => get('/pda/stock/cell/list', { productSku, current: 1, size: 50 })
export const stockOfCell = cellNo => post('/pda/stock/customer/cellList', { cellNo, current: 1, size: 100 })
export const stockMove = body => post('/pda/stock/move', body)

// ---------- 退件 /pda/return ----------
export const returnReceiveList = () => get('/pda/return/receive/listOrder', { current: 1, size: 50 })
export const returnReceiveScan = scanContent => get('/pda/return/receive/scanBarcode', { scanContent })
export const returnPackages = orderNo => get('/pda/return/receive/listPackage', { orderNo })
export const returnProducts = orderNo => get('/pda/return/receive/listProduct', { orderNo, current: 1, size: 100 })
export const returnReceiveSubmit = body => post('/pda/return/receive/submit', body)
export const returnPutawayList = () => get('/pda/return/putaway/listOrder', { current: 1, size: 50 })
export const returnPutawayScan = scanContent => post('/pda/return/putaway/scanOrderNo', { scanContent })
export const returnPutawayDetail = (returnNo, barcode) => get('/pda/return/putaway/detail/scanBarcode', { returnNo, barcode })
export const returnPutawayCell = (cellNo, productQuality) => get('/pda/return/putaway/scanCell', { cellNo, productQuality })
export const returnPutawaySubmit = body => post('/pda/return/putaway/submit', body)

// 本地演示专用（真实系统没有）：重置全部内存示例数据
export const resetAll = () => request.post('/demo/reset')
