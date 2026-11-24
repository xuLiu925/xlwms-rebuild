// 由 spec.mjs 生成通用只读列表页的建表、虚构数据、后端注册表与前端配置。
// 用法（项目根目录）：node tools/list-pages/gen.mjs
import { writeFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'
import { resources } from './spec.mjs'

const root = resolve(dirname(fileURLToPath(import.meta.url)), '../..')
const out = p => resolve(root, p)
const snake = s => s.replace(/[A-Z]/g, c => '_' + c.toLowerCase())
const sqlType = { str: 'VARCHAR(128)', int: 'INT', money: 'DECIMAL(14,2)', rate: 'DECIMAL(14,6)', time: 'VARCHAR(19)' }
const sqlVal = (v, type) => (type === 'str' || type === 'time') ? `'${String(v ?? '').replace(/'/g, "''")}'` : String(v)
const HEAD = '-- 由 tools/list-pages/gen.mjs 生成，勿手改；全部虚构示例数据。\n'

const schema = [HEAD]
const data = [HEAD]
const registry = []
const front = []

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
  registry.push({
    key: r.key, table,
    columns: cols.map(c => ({ name: c.name, column: c.column, type: c.type, kw: !!c.kw, filter: !!(c.filter || c.enum),
      enumLabels: c.enum ? Object.fromEntries(Object.entries(c.enum).map(([k, v]) => [k, v[0]])) : null })),
    tabsColumn: tabs ? tabs.name : null
  })

  const filterOptions = {}
  for (const c of cols.filter(c => c.filter)) filterOptions[c.name] = [...new Set(rows.map(x => x[c.name]).filter(v => v !== ''))].sort()
  const mock = rows.slice(0, 5).map(row => {
    const m = { id: String(row.id) }
    for (const c of cols) {
      m[c.name] = c.type === 'money' || c.type === 'rate' ? Number(row[c.name]) : row[c.name]
      if (c.enum) m[c.name + 'Name'] = c.enum[row[c.name]][0]
    }
    return m
  })
  front.push({
    key: r.key, route: r.route, title: r.title,
    columns: cols.map(c => ({ name: c.name, label: c.label, type: c.type, width: c.width, kw: !!c.kw, filter: !!c.filter, enum: c.enum || null })),
    tabs: tabs ? tabs.name : null, filterOptions, mock, mockTotal: rows.length
  })
}

writeFileSync(out('backend/src/main/resources/db/schema-list.sql'), schema.join('\n'))
writeFileSync(out('backend/src/main/resources/db/data-list.sql'), data.join('\n'))
writeFileSync(out('backend/src/main/resources/db/list-resources.json'), JSON.stringify(registry, null, 2) + '\n')
writeFileSync(out('frontend/src/config/listPages.generated.js'),
  '// 由 tools/list-pages/gen.mjs 生成，勿手改。接口路径、字段与编码均为本项目占位；mock 为虚构数据。\n' +
  'export default ' + JSON.stringify(front, null, 2) + '\n')
console.log(`生成 ${resources.length} 个列表页：` + resources.map(r => r.route).join(', '))
