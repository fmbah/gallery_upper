ALTER TABLE tb_brand_pic MODIFY miniapp_display_src text NOT NULL COMMENT '前端展示图';
ALTER TABLE tb_brand_pic MODIFY latest_apply_src text NOT NULL COMMENT '最近申请图';
ALTER TABLE tb_brand_pic MODIFY template_id varchar(512) NOT NULL COMMENT '图片模板id集合,使用逗号分割开';
update tb_brand_pic set miniapp_display_src = concat('[\'',miniapp_display_src,'\']'), latest_apply_src = concat('[\'',latest_apply_src,'\']'), template_id = concat('[\'', template_id, '\']') where status = 0;
ALTER TABLE tb_brand_pic ADD source tinyint(2) NOT NULL COMMENT '来源(1平台方 0品牌方)';
