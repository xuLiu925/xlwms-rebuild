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
