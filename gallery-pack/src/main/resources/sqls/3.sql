ALTER TABLE tb_drawcash_log ADD payment_no varchar(32) NULL COMMENT '微信付款单号';
ALTER TABLE tb_drawcash_log ADD payment_time varchar(32) NULL COMMENT '付款成功时间';
ALTER TABLE tb_drawcash_log ADD partner_trade_no varchar(32) NULL COMMENT '商户订单号';

ALTER TABLE tb_drawcash_log ALTER COLUMN payment_no SET DEFAULT '';
ALTER TABLE tb_drawcash_log ALTER COLUMN payment_time SET DEFAULT '';
ALTER TABLE tb_drawcash_log ALTER COLUMN partner_trade_no SET DEFAULT '';