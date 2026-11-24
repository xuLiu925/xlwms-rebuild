// 观察自公开 app bundle 中的接口路径（仅路径，参数/响应结构未知，需登录后抓包确认）
export const HOME = {
  stockSummary: '/home/stockSummary',
  balance: '/home/balance',
  todoData: '/home/todoData',
  orderCountAnalysis: '/home/order/count/analysis',
  orderCountDetail: '/home/order/count/detail'
}
export const COMMON = {
  listCustomer: '/customer/listCustomer',
  translate: '/common/language/getTranslate',
  fileUpload: '/common/file/upload/batch',
  excelExport: '/excel/export',
  taskCenter: '/system/taskCenter/task/page'
}
