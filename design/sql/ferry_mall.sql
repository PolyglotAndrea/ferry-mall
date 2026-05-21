CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  nickname VARCHAR(64) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0,
  name VARCHAR(64) NOT NULL,
  sort INT NOT NULL DEFAULT 0,
  visible TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_spu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  subtitle VARCHAR(255),
  cover_url VARCHAR(512),
  price_cent INT NOT NULL,
  market_price_cent INT NOT NULL DEFAULT 0,
  stock INT NOT NULL DEFAULT 0,
  sales INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS member_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  mobile VARCHAR(32),
  nickname VARCHAR(64) NOT NULL,
  avatar_url VARCHAR(512),
  points INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_info (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL UNIQUE,
  member_id BIGINT NOT NULL,
  total_amount_cent INT NOT NULL DEFAULT 0,
  discount_amount_cent INT NOT NULL DEFAULT 0,
  pay_amount_cent INT NOT NULL,
  status TINYINT NOT NULL,
  receiver_name VARCHAR(64) NOT NULL,
  receiver_mobile VARCHAR(32) NOT NULL,
  receiver_address VARCHAR(255) NOT NULL,
  remark VARCHAR(500),
  pay_time DATETIME,
  delivery_time DATETIME,
  receive_time DATETIME,
  cancel_time DATETIME,
  cancel_reason VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  spu_id BIGINT NOT NULL,
  sku_id BIGINT,
  store_id BIGINT NOT NULL DEFAULT 0,
  product_name VARCHAR(128) NOT NULL,
  product_image VARCHAR(512),
  price_cent INT NOT NULL,
  quantity INT NOT NULL,
  total_cent INT NOT NULL,
  INDEX idx_order_id (order_id)
);

INSERT INTO sys_user(username, password, nickname) VALUES ('admin', '{noop}admin123', '系统管理员')
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO product_category(id, parent_id, name, sort) VALUES
(1, 0, '数码家电', 1),
(2, 0, '食品生鲜', 2),
(3, 0, '服饰鞋包', 3)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO product_spu(id, category_id, name, subtitle, cover_url, price_cent, market_price_cent, stock, sales) VALUES
(1, 1, 'Ferry 智能手表', '轻量健康监测，多场景续航', 'https://dummyimage.com/600x600/f2f3f5/333&text=Watch', 39900, 49900, 120, 52),
(2, 2, '山谷冷萃咖啡', '低温慢萃，12瓶装', 'https://dummyimage.com/600x600/f2f3f5/333&text=Coffee', 8900, 9900, 300, 168),
(3, 3, '城市通勤双肩包', '防泼水面料，15寸电脑仓', 'https://dummyimage.com/600x600/f2f3f5/333&text=Bag', 15900, 19900, 80, 37)
ON DUPLICATE KEY UPDATE name = VALUES(name);

CREATE TABLE IF NOT EXISTS merchant_info (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  contact_name VARCHAR(64) NOT NULL,
  contact_mobile VARCHAR(32) NOT NULL,
  license_no VARCHAR(64),
  status TINYINT NOT NULL DEFAULT 10,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS store_info (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  merchant_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  logo_url VARCHAR(512),
  description VARCHAR(255),
  status TINYINT NOT NULL DEFAULT 1,
  score DECIMAL(3,1) NOT NULL DEFAULT 5.0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS settlement_bill (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  merchant_id BIGINT NOT NULL,
  order_amount_cent INT NOT NULL DEFAULT 0,
  commission_cent INT NOT NULL DEFAULT 0,
  payable_cent INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 10,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS logistics_trace (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  logistics_no VARCHAR(64) NOT NULL,
  company VARCHAR(64) NOT NULL,
  trace_content VARCHAR(255) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS aftermarket_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  reason VARCHAR(255) NOT NULL,
  status TINYINT NOT NULL DEFAULT 10,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payment_channel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  channel_code VARCHAR(32) NOT NULL UNIQUE,
  channel_name VARCHAR(64) NOT NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  config_json TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payment_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payment_no VARCHAR(64) NOT NULL UNIQUE,
  order_no VARCHAR(64) NOT NULL,
  channel VARCHAR(32) NOT NULL,
  amount_cent INT NOT NULL,
  status TINYINT NOT NULL DEFAULT 10,
  third_party_no VARCHAR(128),
  callback_payload TEXT,
  paid_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_order_no (order_no)
);

INSERT INTO payment_channel(id, channel_code, channel_name, enabled) VALUES
(1, 'wxpay', '微信支付', 1),
(2, 'alipay', '支付宝', 1)
ON DUPLICATE KEY UPDATE channel_name = VALUES(channel_name);

INSERT INTO merchant_info(id, name, contact_name, contact_mobile, license_no, status) VALUES
(1, '渡船自营', '平台运营', '13800000001', 'FERRY-SELF-001', 20),
(2, '山谷生活馆', '王店长', '13800000002', 'VALLEY-STORE-001', 10)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO store_info(id, merchant_id, name, logo_url, description, status, score) VALUES
(1, 1, '渡船自营旗舰店', 'https://dummyimage.com/160x160/2563eb/fff&text=F', '平台自营品质好物', 1, 4.9),
(2, 2, '山谷生活馆', 'https://dummyimage.com/160x160/16a34a/fff&text=S', '咖啡与生活方式集合店', 1, 4.8)
ON DUPLICATE KEY UPDATE name = VALUES(name);
