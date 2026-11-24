-- 由 tools/list-pages/gen.mjs 生成，勿手改；全部虚构示例数据。

DROP TABLE IF EXISTS lp_customer;
CREATE TABLE lp_customer (
  id BIGINT NOT NULL PRIMARY KEY,
  customer_code VARCHAR(128),
  customer_name VARCHAR(128),
  contact_name VARCHAR(128),
  settle_currency VARCHAR(128),
  balance DECIMAL(14,2),
  credit_limit DECIMAL(14,2),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_customer_recharge;
CREATE TABLE lp_customer_recharge (
  id BIGINT NOT NULL PRIMARY KEY,
  recharge_no VARCHAR(128),
  customer_code VARCHAR(128),
  customer_name VARCHAR(128),
  amount DECIMAL(14,2),
  currency VARCHAR(128),
  pay_method VARCHAR(128),
  status INT,
  apply_time VARCHAR(19),
  audit_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_seed_wall;
CREATE TABLE lp_seed_wall (
  id BIGINT NOT NULL PRIMARY KEY,
  wall_code VARCHAR(128),
  wall_name VARCHAR(128),
  wh_code VARCHAR(128),
  row_count INT,
  col_count INT,
  grid_count INT,
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_pick_cart;
CREATE TABLE lp_pick_cart (
  id BIGINT NOT NULL PRIMARY KEY,
  cart_code VARCHAR(128),
  cart_name VARCHAR(128),
  wh_code VARCHAR(128),
  grid_count INT,
  bind_wave_no VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_account;
CREATE TABLE lp_account (
  id BIGINT NOT NULL PRIMARY KEY,
  username VARCHAR(128),
  real_name VARCHAR(128),
  role_name VARCHAR(128),
  wh_codes VARCHAR(128),
  status INT,
  last_login_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_role;
CREATE TABLE lp_role (
  id BIGINT NOT NULL PRIMARY KEY,
  role_code VARCHAR(128),
  role_name VARCHAR(128),
  user_count INT,
  remark VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_channel;
CREATE TABLE lp_channel (
  id BIGINT NOT NULL PRIMARY KEY,
  channel_code VARCHAR(128),
  channel_name VARCHAR(128),
  carrier VARCHAR(128),
  service_type VARCHAR(128),
  wh_code VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_channel_group;
CREATE TABLE lp_channel_group (
  id BIGINT NOT NULL PRIMARY KEY,
  group_code VARCHAR(128),
  group_name VARCHAR(128),
  channel_count INT,
  remark VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_exchange_rate;
CREATE TABLE lp_exchange_rate (
  id BIGINT NOT NULL PRIMARY KEY,
  from_currency VARCHAR(128),
  to_currency VARCHAR(128),
  rate DECIMAL(14,6),
  effective_date VARCHAR(128),
  update_user VARCHAR(128)
);

DROP TABLE IF EXISTS lp_notice;
CREATE TABLE lp_notice (
  id BIGINT NOT NULL PRIMARY KEY,
  title VARCHAR(128),
  notice_type VARCHAR(128),
  publisher VARCHAR(128),
  status INT,
  publish_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_request_log;
CREATE TABLE lp_request_log (
  id BIGINT NOT NULL PRIMARY KEY,
  request_no VARCHAR(128),
  api_name VARCHAR(128),
  customer_code VARCHAR(128),
  cost_ms INT,
  result INT,
  request_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_task_center;
CREATE TABLE lp_task_center (
  id BIGINT NOT NULL PRIMARY KEY,
  task_no VARCHAR(128),
  task_name VARCHAR(128),
  task_type VARCHAR(128),
  progress INT,
  status INT,
  create_user VARCHAR(128),
  create_time VARCHAR(19),
  finish_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_return_order;
CREATE TABLE lp_return_order (
  id BIGINT NOT NULL PRIMARY KEY,
  return_no VARCHAR(128),
  customer_code VARCHAR(128),
  customer_name VARCHAR(128),
  source_order_no VARCHAR(128),
  tracking_no VARCHAR(128),
  return_type VARCHAR(128),
  sku_qty INT,
  wh_code VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_outbound_abnormal;
CREATE TABLE lp_outbound_abnormal (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  wave_no VARCHAR(128),
  customer_code VARCHAR(128),
  abnormal_type VARCHAR(128),
  remark VARCHAR(128),
  status INT,
  report_user VARCHAR(128),
  report_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_outbound_intercept;
CREATE TABLE lp_outbound_intercept (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  customer_code VARCHAR(128),
  intercept_reason VARCHAR(128),
  order_status_name VARCHAR(128),
  status INT,
  apply_time VARCHAR(19)
);
