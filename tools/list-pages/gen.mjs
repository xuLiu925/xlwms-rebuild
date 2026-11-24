// 由 spec.mjs 生成通用只读列表页的建表、虚构数据、后端注册表与前端配置。
// 用法（项目根目录）：node tools/list-pages/gen.mjs
import { writeFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'
import { resources, editable } from './spec.mjs'

const root = resolve(dirname(fileURLToPath(import.meta.url)), '../..')
const out = p => resolve(root, p)
const snake = s => s.replace(/[A-Z]/g, c => '_' + c.toLowerCase())
const sqlType = { str: 'VARCHAR(128)', int: 'INT', num: 'DECIMAL(14,2)', money: 'DECIMAL(14,2)', rate: 'DECIMAL(14,6)', time: 'VARCHAR(19)' }
const sqlVal = (v, type) => (type === 'str' || type === 'time') ? `'${String(v ?? '').replace(/'/g, "''")}'` : String(v)
const HEAD = '-- 由 tools/list-pages/gen.mjs 生成，勿手改；全部虚构示例数据。\n'

const schema = [HEAD]
const data = [HEAD]
const registry = []
const front = []

// 规格自检：key/route 唯一、ID 区间不重叠且在 JS 安全整数内、每个资源最多一个页签列
const seen = { key: new Set(), route: new Set() }
const ranges = []
for (const r of resources) {
  for (const f of ['key', 'route']) {
    if (seen[f].has(r[f])) throw new Error('重复的 ' + f + ': ' + r[f])
    seen[f].add(r[f])
  }
  if (r.idBase + r.count >= Number.MAX_SAFE_INTEGER) throw new Error(r.key + ' 的 idBase 超出 JS 安全整数')
  ranges.push([r.idBase + 1, r.idBase + r.count, r.key])
  if (r.columns.filter(c => c[3] && c[3].tabs).length > 1) throw new Error(r.key + ' 有多个 tabs 列')
}
for (const k of editable) if (!resources.some(r => r.key === k)) throw new Error('editable 中的资源不存在: ' + k)
ranges.sort((a, b) => a[0] - b[0]).forEach((x, i, a) => { if (i && x[0] <= a[i - 1][1]) throw new Error('ID 区间重叠: ' + a[i - 1][2] + ' / ' + x[2]) })

for (const r of resources) {
  if (!/^[a-zA-Z][a-zA-Z0-9]*$/.test(r.key)) throw new Error('bad key ' + r.key)
  const table = 'lp_' + snake(r.key)
  const cols = r.columns.map(([name, label, type, o = {}]) => ({ name, label, type, column: snake(name), ...o }))
  schema.push(`DROP TABLE IF EXISTS ${table};`)
  schema.push(`CREATE TABLE ${table} (\n  id BIGINT NOT NULL PRIMARY KEY,\n` +
    cols.map(c => `  ${c.column} ${sqlType[c.type]}`).join(',\n') + '\n);\n')

  const rows = Array.from({ length: r.count }, (_, i) => ({ id: r.idBase + i + 1, ...r.row(i) }))
  const colList = ['id', ...cols.map(c => c.column)].join(',')
  for (const row of rows) {
    for (const c of cols) if (!(c.name in row)) throw new Error(`${r.key} 行缺少字段 ${c.name}`)
    data.push(`INSERT INTO ${table} (${colList}) VALUES (${[String(row.id), ...cols.map(c => sqlVal(row[c.name], c.type))].join(',')});`)
  }
  data.push('')

  const tabs = cols.find(c => c.tabs)
  // 批次 8 编辑元数据：关键字列与枚举列必填；createTime/publishTime 新建时由后端填当前时间，updateTime 每次保存都填；
  // 其他时间列（如最近登录）不在表单里。第一个关键字列在数据里唯一时作为唯一键。
  const isEditable = editable.has(r.key)
  for (const c of cols) {
    c.required = !!(c.kw || c.enum)
    c.auto = c.type === 'time' ? ({ createTime: 'create', publishTime: 'create', updateTime: 'update' }[c.name] || 'none') : null
  }
  const firstKw = cols.find(c => c.kw)
  const unique = isEditable && firstKw && new Set(rows.map(x => x[firstKw.name])).size === rows.length ? firstKw.name : null
  registry.push({
    editable: isEditable, unique, idBase: r.idBase,
    key: r.key, table,
    columns: cols.map(c => ({ name: c.name, column: c.column, type: c.type, kw: !!c.kw, filter: !!(c.filter || c.enum),
      enumLabels: c.enum ? Object.fromEntries(Object.entries(c.enum).map(([k, v]) => [k, v[0]])) : null,
      required: c.required, auto: c.auto })),
    tabsColumn: tabs ? tabs.name : null
  })

  const filterOptions = {}
  for (const c of cols.filter(c => c.filter)) filterOptions[c.name] = [...new Set(rows.map(x => x[c.name]).filter(v => v !== ''))].sort()
  const mock = rows.slice(0, 5).map(row => {
    const m = { id: String(row.id) }
    for (const c of cols) {
      m[c.name] = ['num', 'money', 'rate'].includes(c.type) ? Number(row[c.name]) : row[c.name]
      if (c.enum) m[c.name + 'Name'] = c.enum[row[c.name]][0]
    }
    return m
  })
  front.push({
    key: r.key, route: r.route, title: r.title,
    columns: cols.map(c => ({ name: c.name, label: c.label, type: c.type, width: c.width, kw: !!c.kw, filter: !!c.filter, enum: c.enum || null,
      required: c.required, auto: c.auto })),
    editable: isEditable, unique, tabs: tabs ? tabs.name : null, filterOptions, mock, mockTotal: rows.length
  })
}

writeFileSync(out('backend/src/main/resources/db/schema-list.sql'), schema.join('\n'))
writeFileSync(out('backend/src/main/resources/db/data-list.sql'), data.join('\n'))
writeFileSync(out('backend/src/main/resources/db/list-resources.json'), JSON.stringify(registry, null, 2) + '\n')
writeFileSync(out('frontend/src/config/listPages.generated.js'),
  '// 由 tools/list-pages/gen.mjs 生成，勿手改。接口路径、字段与编码均为本项目占位；mock 为虚构数据。\n' +
  'export default ' + JSON.stringify(front, null, 2) + '\n')
writeFileSync(out('backend/src/test/resources/list-expected.csv'), resources.map(r => r.key + ',' + r.count).join('\n') + '\n')
console.log(`生成 ${resources.length} 个列表页：` + resources.map(r => r.route).join(', '))
