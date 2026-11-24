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

DROP TABLE IF EXISTS lp_global_asn;
CREATE TABLE lp_global_asn (
  id BIGINT NOT NULL PRIMARY KEY,
  asn_no VARCHAR(128),
  customer_code VARCHAR(128),
  customer_name VARCHAR(128),
  wh_code VARCHAR(128),
  box_count INT,
  sku_qty INT,
  status INT,
  eta_date VARCHAR(128),
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_global_parcel;
CREATE TABLE lp_global_parcel (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  platform_order_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  channel_name VARCHAR(128),
  tracking_no VARCHAR(128),
  sku_qty INT,
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_global_freight;
CREATE TABLE lp_global_freight (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  box_count INT,
  pallet_count INT,
  delivery_type VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_global_return;
CREATE TABLE lp_global_return (
  id BIGINT NOT NULL PRIMARY KEY,
  return_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  return_type VARCHAR(128),
  sku_qty INT,
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_direct_mail_order;
CREATE TABLE lp_direct_mail_order (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  customer_code VARCHAR(128),
  channel_name VARCHAR(128),
  tracking_no VARCHAR(128),
  weight_kg DECIMAL(14,2),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_direct_mail_box;
CREATE TABLE lp_direct_mail_box (
  id BIGINT NOT NULL PRIMARY KEY,
  box_no VARCHAR(128),
  channel_name VARCHAR(128),
  order_count INT,
  weight_kg DECIMAL(14,2),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_claim;
CREATE TABLE lp_claim (
  id BIGINT NOT NULL PRIMARY KEY,
  claim_no VARCHAR(128),
  order_no VARCHAR(128),
  customer_code VARCHAR(128),
  claim_type VARCHAR(128),
  claim_amount DECIMAL(14,2),
  currency VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_work_order;
CREATE TABLE lp_work_order (
  id BIGINT NOT NULL PRIMARY KEY,
  work_order_no VARCHAR(128),
  title VARCHAR(128),
  customer_code VARCHAR(128),
  work_type VARCHAR(128),
  priority VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_arrival_scan;
CREATE TABLE lp_arrival_scan (
  id BIGINT NOT NULL PRIMARY KEY,
  scan_no VARCHAR(128),
  asn_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  box_count INT,
  scan_user VARCHAR(128),
  status INT,
  scan_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_delivery_goods;
CREATE TABLE lp_delivery_goods (
  id BIGINT NOT NULL PRIMARY KEY,
  appoint_no VARCHAR(128),
  asn_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  appoint_date VARCHAR(128),
  vehicle_type VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_pallet_receive;
CREATE TABLE lp_pallet_receive (
  id BIGINT NOT NULL PRIMARY KEY,
  receipt_no VARCHAR(128),
  asn_no VARCHAR(128),
  tray_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  box_count INT,
  sku_qty INT,
  operator VARCHAR(128),
  status INT,
  receipt_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_pallet_forming;
CREATE TABLE lp_pallet_forming (
  id BIGINT NOT NULL PRIMARY KEY,
  form_no VARCHAR(128),
  tray_no VARCHAR(128),
  asn_no VARCHAR(128),
  wh_code VARCHAR(128),
  box_count INT,
  operator VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_new_product;
CREATE TABLE lp_new_product (
  id BIGINT NOT NULL PRIMARY KEY,
  sku VARCHAR(128),
  product_name VARCHAR(128),
  customer_code VARCHAR(128),
  asn_no VARCHAR(128),
  length_cm DECIMAL(14,2),
  width_cm DECIMAL(14,2),
  height_cm DECIMAL(14,2),
  weight_kg DECIMAL(14,2),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_inventory_claim;
CREATE TABLE lp_inventory_claim (
  id BIGINT NOT NULL PRIMARY KEY,
  claim_no VARCHAR(128),
  barcode VARCHAR(128),
  wh_code VARCHAR(128),
  cell_code VARCHAR(128),
  qty INT,
  claim_customer_code VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_out_freight;
CREATE TABLE lp_out_freight (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  box_count INT,
  pallet_count INT,
  delivery_type VARCHAR(128),
  appoint_time VARCHAR(19),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_wave_rule;
CREATE TABLE lp_wave_rule (
  id BIGINT NOT NULL PRIMARY KEY,
  rule_code VARCHAR(128),
  rule_name VARCHAR(128),
  wh_code VARCHAR(128),
  order_type VARCHAR(128),
  max_order_count INT,
  priority INT,
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_scan_form;
CREATE TABLE lp_scan_form (
  id BIGINT NOT NULL PRIMARY KEY,
  form_no VARCHAR(128),
  carrier VARCHAR(128),
  wh_code VARCHAR(128),
  parcel_count INT,
  total_weight_kg DECIMAL(14,2),
  status INT,
  handover_time VARCHAR(19),
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_split_order;
CREATE TABLE lp_split_order (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  new_order_nos VARCHAR(128),
  customer_code VARCHAR(128),
  split_reason VARCHAR(128),
  operator VARCHAR(128),
  split_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_swap_order;
CREATE TABLE lp_swap_order (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  old_tracking_no VARCHAR(128),
  new_tracking_no VARCHAR(128),
  channel_name VARCHAR(128),
  reason VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_rebrand;
CREATE TABLE lp_rebrand (
  id BIGINT NOT NULL PRIMARY KEY,
  task_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  sku VARCHAR(128),
  qty INT,
  new_barcode VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_return_outbound;
CREATE TABLE lp_return_outbound (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  return_no VARCHAR(128),
  customer_code VARCHAR(128),
  wh_code VARCHAR(128),
  sku_qty INT,
  outbound_type VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_fuel_plan;
CREATE TABLE lp_fuel_plan (
  id BIGINT NOT NULL PRIMARY KEY,
  plan_code VARCHAR(128),
  plan_name VARCHAR(128),
  carrier VARCHAR(128),
  fuel_rate DECIMAL(14,2),
  effective_date VARCHAR(128),
  expire_date VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_zone_temp;
CREATE TABLE lp_zone_temp (
  id BIGINT NOT NULL PRIMARY KEY,
  temp_code VARCHAR(128),
  temp_name VARCHAR(128),
  carrier VARCHAR(128),
  country VARCHAR(128),
  zone_count INT,
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_surcharge;
CREATE TABLE lp_surcharge (
  id BIGINT NOT NULL PRIMARY KEY,
  charge_code VARCHAR(128),
  charge_name VARCHAR(128),
  carrier VARCHAR(128),
  charge_type VARCHAR(128),
  amount DECIMAL(14,2),
  currency VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_freight_temp;
CREATE TABLE lp_freight_temp (
  id BIGINT NOT NULL PRIMARY KEY,
  temp_code VARCHAR(128),
  temp_name VARCHAR(128),
  channel_name VARCHAR(128),
  billing_mode VARCHAR(128),
  currency VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_logistics_price_setting;
CREATE TABLE lp_logistics_price_setting (
  id BIGINT NOT NULL PRIMARY KEY,
  customer_code VARCHAR(128),
  customer_name VARCHAR(128),
  channel_name VARCHAR(128),
  freight_temp_name VARCHAR(128),
  discount_rate DECIMAL(14,6),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_operate_bill;
CREATE TABLE lp_operate_bill (
  id BIGINT NOT NULL PRIMARY KEY,
  item_code VARCHAR(128),
  item_name VARCHAR(128),
  billing_unit VARCHAR(128),
  unit_price DECIMAL(14,2),
  currency VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_warehouse_rent;
CREATE TABLE lp_warehouse_rent (
  id BIGINT NOT NULL PRIMARY KEY,
  plan_code VARCHAR(128),
  plan_name VARCHAR(128),
  wh_code VARCHAR(128),
  free_days INT,
  unit_price DECIMAL(14,2),
  currency VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_quotation_scheme;
CREATE TABLE lp_quotation_scheme (
  id BIGINT NOT NULL PRIMARY KEY,
  scheme_code VARCHAR(128),
  scheme_name VARCHAR(128),
  customer_count INT,
  currency VARCHAR(128),
  effective_date VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_business_expenses;
CREATE TABLE lp_business_expenses (
  id BIGINT NOT NULL PRIMARY KEY,
  fee_no VARCHAR(128),
  biz_no VARCHAR(128),
  customer_code VARCHAR(128),
  fee_type VARCHAR(128),
  amount DECIMAL(14,2),
  currency VARCHAR(128),
  status INT,
  fee_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_bills_receivable;
CREATE TABLE lp_bills_receivable (
  id BIGINT NOT NULL PRIMARY KEY,
  bill_no VARCHAR(128),
  customer_code VARCHAR(128),
  customer_name VARCHAR(128),
  bill_period VARCHAR(128),
  amount DECIMAL(14,2),
  currency VARCHAR(128),
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_expend;
CREATE TABLE lp_expend (
  id BIGINT NOT NULL PRIMARY KEY,
  expend_no VARCHAR(128),
  customer_code VARCHAR(128),
  bill_no VARCHAR(128),
  amount DECIMAL(14,2),
  currency VARCHAR(128),
  balance_after DECIMAL(14,2),
  expend_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_bill_template;
CREATE TABLE lp_bill_template (
  id BIGINT NOT NULL PRIMARY KEY,
  temp_code VARCHAR(128),
  temp_name VARCHAR(128),
  bill_cycle VARCHAR(128),
  fee_types VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_print_template;
CREATE TABLE lp_print_template (
  id BIGINT NOT NULL PRIMARY KEY,
  temp_code VARCHAR(128),
  temp_name VARCHAR(128),
  temp_type VARCHAR(128),
  paper_size VARCHAR(128),
  is_default INT,
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_brand;
CREATE TABLE lp_brand (
  id BIGINT NOT NULL PRIMARY KEY,
  brand_code VARCHAR(128),
  brand_name VARCHAR(128),
  domain VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_agency;
CREATE TABLE lp_agency (
  id BIGINT NOT NULL PRIMARY KEY,
  agency_code VARCHAR(128),
  agency_name VARCHAR(128),
  agency_type VARCHAR(128),
  contact_name VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_erp_auth;
CREATE TABLE lp_erp_auth (
  id BIGINT NOT NULL PRIMARY KEY,
  erp_name VARCHAR(128),
  customer_code VARCHAR(128),
  app_key_masked VARCHAR(128),
  auth_time VARCHAR(19),
  expire_time VARCHAR(19),
  status INT
);

DROP TABLE IF EXISTS lp_msg_notice;
CREATE TABLE lp_msg_notice (
  id BIGINT NOT NULL PRIMARY KEY,
  title VARCHAR(128),
  msg_type VARCHAR(128),
  receiver VARCHAR(128),
  read_flag INT,
  send_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_warning_msg;
CREATE TABLE lp_warning_msg (
  id BIGINT NOT NULL PRIMARY KEY,
  warning_type VARCHAR(128),
  content VARCHAR(128),
  wh_code VARCHAR(128),
  level INT,
  handled INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_receive_account;
CREATE TABLE lp_receive_account (
  id BIGINT NOT NULL PRIMARY KEY,
  account_name VARCHAR(128),
  bank_name VARCHAR(128),
  account_no_masked VARCHAR(128),
  currency VARCHAR(128),
  status INT,
  update_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_error_pool;
CREATE TABLE lp_error_pool (
  id BIGINT NOT NULL PRIMARY KEY,
  error_no VARCHAR(128),
  biz_type VARCHAR(128),
  biz_no VARCHAR(128),
  error_msg VARCHAR(128),
  retry_count INT,
  status INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_label_record;
CREATE TABLE lp_label_record (
  id BIGINT NOT NULL PRIMARY KEY,
  order_no VARCHAR(128),
  tracking_no VARCHAR(128),
  channel_name VARCHAR(128),
  label_format VARCHAR(128),
  result INT,
  create_time VARCHAR(19)
);

DROP TABLE IF EXISTS lp_push_record;
CREATE TABLE lp_push_record (
  id BIGINT NOT NULL PRIMARY KEY,
  push_no VARCHAR(128),
  biz_no VARCHAR(128),
  customer_code VARCHAR(128),
  push_type VARCHAR(128),
  cost_ms INT,
  result INT,
  push_time VARCHAR(19)
);
