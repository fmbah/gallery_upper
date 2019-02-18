ALTER TABLE tb_template ADD phone_preview_image_url mediumtext NOT NULL COMMENT '后台上传手机端预览图';

ALTER TABLE tb_incomexpense ADD sub_type tinyint(2) NOT NULL COMMENT '1一级分成, 2二级分成';

ALTER TABLE tb_incomexpense MODIFY sub_type tinyint(2) NOT NULL COMMENT '0非分享获益,1一级分成, 2二级分成';

ALTER TABLE tb_incomexpense ADD payment_id int(10) NOT NULL COMMENT '订单id';