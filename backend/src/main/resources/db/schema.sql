-- 本地开发示例库 DDL。时间类字段用 VARCHAR 存储，直接对应页面展示字符串，
-- 也避免 MySQL / H2 的日期格式差异。每次启动重建，数据全部虚构。

DROP TABLE IF EXISTS stock_sku;
CREATE TABLE stock_sku (
  id            BIGINT       NOT NULL PRIMARY KEY,
  sku           VARCHAR(64),
  barcode       VARCHAR(64),
  product_name  VARCHAR(128),
  category      VARCHAR(64),
  customer_code VARCHAR(32),
  customer_name VARCHAR(64),
  stock_attr    VARCHAR(16),
  total_qty     INT,
  available_qty INT,
  locked_qty    INT
);

DROP TABLE IF EXISTS bl_delivery;
CREATE TABLE bl_delivery (
  id            BIGINT       NOT NULL PRIMARY KEY,
  delivery_no   VARCHAR(64),
  customer_code VARCHAR(32),
  customer_name VARCHAR(64),
  sku_kind      VARCHAR(32),
  channel_name  VARCHAR(64),
  carrier       VARCHAR(64),
  country_code  VARCHAR(8),
  tracking_no   VARCHAR(64),
  parcel_qty    INT,
  platform      VARCHAR(64),
  status        INT,
  status_name   VARCHAR(32),
  wave_no       VARCHAR(64),
  label_printed TINYINT,
  ship_deadline VARCHAR(32),
  create_time   VARCHAR(32)
);

DROP TABLE IF EXISTS bl_asn;
CREATE TABLE bl_asn (
  id            BIGINT       NOT NULL PRIMARY KEY,
  asn_no        VARCHAR(64),
  source_no     VARCHAR(64),
  inbound_type  INT,
  customer_code VARCHAR(32),
  customer_name VARCHAR(64),
  wh_code       VARCHAR(32),
  status        INT,
  box_count     INT,
  receipt_count INT,
  sku_qty_sum   INT,
  eta_time      VARCHAR(32),
  create_time   VARCHAR(32)
);

DROP TABLE IF EXISTS stock_box;
CREATE TABLE stock_box (
  id            BIGINT       NOT NULL PRIMARY KEY,
  box_no        VARCHAR(64),
  sku           VARCHAR(64),
  product_name  VARCHAR(128),
  customer_code VARCHAR(32),
  customer_name VARCHAR(64),
  cell_code     VARCHAR(32),
  box_spec      VARCHAR(32),
  stock_attr    VARCHAR(16),
  qty           INT,
  shelf_time    VARCHAR(32)
);

DROP TABLE IF EXISTS stock_return;
CREATE TABLE stock_return (
  id            BIGINT       NOT NULL PRIMARY KEY,
  return_no     VARCHAR(64),
  tracking_no   VARCHAR(64),
  sku           VARCHAR(64),
  product_name  VARCHAR(128),
  customer_code VARCHAR(32),
  customer_name VARCHAR(64),
  qty           INT,
  reason        VARCHAR(64),
  status        INT,
  status_name   VARCHAR(32),
  cell_code     VARCHAR(32),
  receive_time  VARCHAR(32)
);

DROP TABLE IF EXISTS basic_product;
CREATE TABLE basic_product (
  id            BIGINT       NOT NULL PRIMARY KEY,
  sku           VARCHAR(64),
  barcode       VARCHAR(64),
  product_name  VARCHAR(128),
  category      VARCHAR(64),
  customer_code VARCHAR(32),
  customer_name VARCHAR(64),
  length_mm     INT,
  width_mm      INT,
  height_mm     INT,
  weight_g      INT,
  status        INT,
  status_name   VARCHAR(16),
  create_time   VARCHAR(32)
);

DROP TABLE IF EXISTS basic_area;
CREATE TABLE basic_area (
  id          BIGINT       NOT NULL PRIMARY KEY,
  area_code   VARCHAR(32),
  area_name   VARCHAR(64),
  wh_code     VARCHAR(32),
  area_type   VARCHAR(16),
  cell_count  INT,
  status      INT,
  status_name VARCHAR(16),
  remark      VARCHAR(128)
);

DROP TABLE IF EXISTS basic_cell;
CREATE TABLE basic_cell (
  id           BIGINT       NOT NULL PRIMARY KEY,
  cell_code    VARCHAR(32),
  area_code    VARCHAR(32),
  area_name    VARCHAR(64),
  wh_code      VARCHAR(32),
  cell_type    VARCHAR(16),
  usage_status INT,
  usage_name   VARCHAR(16),
  create_time  VARCHAR(32)
);

DROP TABLE IF EXISTS basic_tray;
CREATE TABLE basic_tray (
  id          BIGINT       NOT NULL PRIMARY KEY,
  tray_no     VARCHAR(32),
  wh_code     VARCHAR(32),
  area_code   VARCHAR(32),
  cell_code   VARCHAR(32),
  status      INT,
  status_name VARCHAR(16),
  sku_kinds   INT,
  total_qty   INT,
  bind_time   VARCHAR(32)
);

DROP TABLE IF EXISTS basic_pack_material;
CREATE TABLE basic_pack_material (
  id            BIGINT       NOT NULL PRIMARY KEY,
  material_code VARCHAR(32),
  material_name VARCHAR(64),
  material_type VARCHAR(16),
  spec          VARCHAR(32),
  unit          VARCHAR(8),
  stock_qty     INT,
  status        INT,
  status_name   VARCHAR(16)
);
