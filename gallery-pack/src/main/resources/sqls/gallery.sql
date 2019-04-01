create table tb_admin
(
  id           bigint auto_increment
    primary key,
  username     varchar(20) collate utf8mb4_bin    not null
  comment '帐号',
  hashed_pwd   varchar(32)                        not null
  comment '加密密码',
  gmt_create   datetime default CURRENT_TIMESTAMP not null,
  gmt_modified datetime default CURRENT_TIMESTAMP not null,
  remark       varchar(256) default ''            not null,
  role_id      bigint default '0'                 not null
  comment '角色id，默认为0',
  brand_id     bigint default '0'                 not null
  comment '品牌id，默认为0',
  constraint tb_admin_id_uindex
  unique (id),
  constraint tb_admin_username_uindex
  unique (username)
);

create table tb_admin_menu
(
  id           bigint auto_increment
    primary key,
  p_id         bigint default '0'                 not null
  comment '父id',
  menu_name    varchar(45)                        not null
  comment '菜单名',
  menu_icon    varchar(45) default ''             not null
  comment '角标',
  menu_address varchar(100)                       not null
  comment '页面跳转路径',
  weight       int default '100'                  not null
  comment '排序权重，从大到小',
  gmt_create   datetime default CURRENT_TIMESTAMP not null,
  gmt_modified datetime default CURRENT_TIMESTAMP not null,
  constraint tb_admin_menu_id_uindex
  unique (id)
)
  comment '菜单数据';

create table tb_admin_role
(
  id           bigint auto_increment
    primary key,
  name         varchar(20)             not null
  comment '角色名称',
  descri       varchar(256) default '' not null
  comment '简介',
  gmt_create   datetime                not null,
  gmt_modified datetime                not null,
  constraint tb_admin_role_id_uindex
  unique (id)
)
  comment '角色数据';

create table tb_admin_role_menu
(
  id           bigint auto_increment
    primary key,
  role_id      bigint                             not null,
  menu_id      bigint                             not null,
  gmt_create   datetime default CURRENT_TIMESTAMP not null,
  gmt_modified datetime default CURRENT_TIMESTAMP not null,
  constraint tb_admin_role_menu_id_uindex
  unique (id)
)
  comment '角色菜单数据';

create table tb_brand_cdkey
(
  id           int unsigned auto_increment
    primary key,
  brand_id     int unsigned        not null,
  code         varchar(12)         not null
  comment '激活码',
  is_used      tinyint(2) unsigned not null
  comment '使用状态 0:未使用(未激活) 1:已使用(已激活)',
  used_user_id int unsigned        null,
  used_time    datetime            not null,
  gmt_create   datetime            not null,
  gmt_modified datetime            not null,
  constraint code_UNIQUE
  unique (code)
)
  comment '品牌激活码';

create index fk_tb_brand_cdkey_tb_company_brand1_idx
  on tb_brand_cdkey (brand_id);

create index fk_tb_brand_cdkey_tb_user1_idx
  on tb_brand_cdkey (used_user_id);

create table tb_brand_pic
(
  id                  int(10) auto_increment
    primary key,
  pic_name            varchar(24)  not null
  comment '图片名称',
  brand_id            int          not null
  comment '品牌id',
  miniapp_display_src text         not null
  comment '前端展示图',
  latest_apply_src    text         not null
  comment '最近申请图',
  status              tinyint      not null
  comment '状态(0: 审核中(提交方)/待审核(审核方) 1: 审核成功 2:审核失败)',
  remark              varchar(32)  not null
  comment '备注',
  gmt_create          datetime     not null,
  gmt_modified        datetime     not null,
  template_id         varchar(512) not null
  comment '图片模板id集合,使用逗号分割开',
  source              tinyint(2)   not null
  comment '来源(0平台方 1品牌方)',
  constraint tb_brand_pic_id_uindex
  unique (id)
)
  comment '品牌方图片';

create table tb_company_brand
(
  id                    int unsigned auto_increment
    primary key,
  name                  varchar(45)  not null,
  introduction          varchar(255) not null,
  contact_person        varchar(12)  not null,
  contact_phone         varchar(16)  not null,
  gmt_create            datetime     not null,
  gmt_modified          datetime     not null,
  expired_time          datetime     not null
  comment '品牌过期时间',
  brand_personal_userid int(10)      not null
  comment '品牌个人号id'
)
  comment '公司品牌';

create table tb_drawcash_log
(
  id            int unsigned auto_increment
    primary key,
  user_id       int unsigned                              not null,
  draw_cash     decimal(9, 2) unsigned                    not null
  comment '提现金额',
  taxation_cash decimal(9, 2) unsigned                    not null
  comment '税',
  type          enum ('WX_WALLET')                        not null,
  gmt_create    datetime                                  not null,
  gmt_modified  datetime                                  not null,
  realname      varchar(45)                               not null
  comment '真实姓名',
  status        enum ('WAIT_PROCESS', 'FINISHED', 'FAIL') not null,
  fail_msg      varchar(255)                              not null,
  remark        varchar(128)                              not null
)
  comment '提现表';

create index fk_tb_drawcash_log_tb_user1_idx
  on tb_drawcash_log (user_id);

create table tb_incomexpense
(
  id              int unsigned auto_increment
    primary key,
  user_id         int unsigned                           not null,
  type            enum ('SHARE_PROFIT', 'WITHDRAW_CASH') not null,
  income          decimal(9, 2) unsigned                 not null,
  expense         decimal(9, 2) unsigned                 not null,
  balance         decimal(9, 2) unsigned                 not null,
  tradedate       date                                   not null,
  gmt_create      datetime                               not null,
  share_profit_id int unsigned                           not null,
  remark          varchar(128)                           not null,
  sub_type        tinyint(2)                             not null
  comment '0非分享获益,1一级分成, 2二级分成',
  payment_id      int(10)                                not null
  comment '订单id'
);

create index fk_tb_incomexpense_tb_share_profit1_idx
  on tb_incomexpense (share_profit_id);

create index fk_tb_incomexpense_tb_user1_idx
  on tb_incomexpense (user_id);

create table tb_label
(
  id           int unsigned auto_increment
    primary key,
  name         varchar(24) not null,
  gmt_create   datetime    not null,
  gmt_modified datetime    not null
)
  comment '模板标签';

create table tb_share_profit
(
  id           int unsigned auto_increment
    primary key,
  user_id      int unsigned           not null,
  paymnet_id   int unsigned           not null,
  profit       decimal(9, 2) unsigned not null,
  gmt_create   datetime               not null,
  gmt_modified datetime               not null,
  sub_type     tinyint(2) unsigned    not null
  comment '类型(1:一级分益 2:二级分益)',
  remark       varchar(128)           not null
)
  comment '分享收益表';

create index fk_tb_share_profit_tb_user1_idx
  on tb_share_profit (user_id);

create index fk_tb_share_profit_tb_user_paymnet1_idx
  on tb_share_profit (paymnet_id);

create table tb_slide
(
  id           int unsigned auto_increment
    primary key,
  type         tinyint(2) unsigned not null
  comment '位置（首页：1；分享获益：2；会员权益：3；）',
  image_url    varchar(192)        not null,
  link_url     varchar(192)        not null,
  gmt_create   datetime            not null,
  gmt_modified datetime            not null,
  weight       tinyint(2) unsigned not null
  comment '权重排序'
)
  comment '轮播图';

create table tb_template
(
  id                      int unsigned auto_increment
    primary key,
  category_id             int unsigned             not null,
  brand_id                int unsigned default '0' not null,
  ratio                   tinyint(2)               not null
  comment '1- 1:1; 2- 4:3;  3- 16:9;',
  is_enabled              tinyint(1)               not null,
  preview_image_url       mediumtext               not null,
  descri                  mediumtext               not null,
  name                    varchar(24)              not null
  comment '模板名称',
  gmt_create              datetime                 not null,
  gmt_modified            datetime                 not null,
  gratis                  tinyint                  not null
  comment '是否免费(0 否 1 是)',
  phone_preview_image_url mediumtext               not null
  comment '后台上传手机端预览图'
);

create index fk_tb_template_tb_company_brand1_idx
  on tb_template (brand_id);

create index fk_tb_template_tb_template_category1_idx
  on tb_template (category_id);

create table tb_template_category
(
  id                   int unsigned auto_increment
    primary key,
  type                 enum ('brand_center', 'category') not null
  comment 'brand_center: 品牌中心
category: 普通类目',
  title                varchar(45)                       not null,
  weight               smallint(5) unsigned              not null,
  is_hot               tinyint(1) unsigned               not null,
  introduction         varchar(128)                      not null,
  background_image_url varchar(192)                      not null
  comment '分类背景图',
  template_filters     text                              not null
  comment '模板滤镜，数组形式。如：[{"num":"1", "url": "xxx"}, {}...]',
  gmt_create           datetime                          not null,
  gmt_modified         datetime                          not null
)
  comment '模板分类';

create table tb_template_labels
(
  id          int unsigned auto_increment
    primary key,
  template_id int unsigned not null,
  label_id    int unsigned not null,
  gmt_create  datetime     not null,
  constraint tb_template_labels_id_uindex
  unique (id)
);

create index fk_tb_template_labels_tb_label1_idx
  on tb_template_labels (label_id);

create index fk_tb_template_labels_tb_template1
  on tb_template_labels (template_id);

create table tb_template_statistics
(
  id            int unsigned auto_increment
    primary key,
  template_id   int unsigned not null,
  category_id   int unsigned not null,
  brand_id      int unsigned not null,
  gmt_create    datetime     not null,
  visitor_count int unsigned not null
  comment '访问次数',
  share_count   int unsigned not null
  comment '分享次数',
  used_count    int unsigned not null
)
  comment '模板统计数据';

create index fk_tb_template_statistics_tb_company_brand1_idx
  on tb_template_statistics (brand_id);

create index fk_tb_template_statistics_tb_template1_idx
  on tb_template_statistics (template_id);

create index fk_tb_template_statistics_tb_template_category1_idx
  on tb_template_statistics (category_id);

create table tb_user
(
  id             int unsigned auto_increment
    primary key,
  wx_openid      varchar(32)            null,
  wx_unionid     varchar(32)            null,
  wx_mini_openid varchar(32)            null
  comment '小程序openid',
  wx_sex         tinyint(2)             not null,
  wx_headimgurl  varchar(192)           not null,
  member_expired datetime               not null,
  recommend_id   int unsigned           not null
  comment '推荐者ID',
  gmt_create     datetime               not null,
  gmt_modified   datetime               not null,
  member_type    tinyint(2) unsigned    not null
  comment '0: 非会员
5: 金卡会员
6: 铂金会员
10: 钻石会员',
  is_agent       tinyint(1) unsigned    not null
  comment '是否代理',
  cash_balance   decimal(9, 2) unsigned not null
  comment '收益余额',
  nickname       varchar(36)            not null,
  constraint wx_unionid_UNIQUE
  unique (wx_unionid)
);

create table tb_user_payment
(
  id             int unsigned auto_increment
    primary key,
  user_id        int unsigned            not null,
  order_no       varchar(16)             not null,
  amount         decimal(9, 2) unsigned  not null,
  status         enum ('paid', 'unpay')  not null,
  transaction_id varchar(36)             not null,
  gmt_payment    datetime                not null,
  gmt_create     datetime                not null,
  gmt_modified   datetime                not null,
  recharge_type  tinyint(2) unsigned     not null
  comment '支付类型：
5: 半年会员
6: 全年会员
10: 终身会员
0: 非会员
1: 品牌会员',
  cdk_code       varchar(12) default ''  not null
  comment '品牌会员类型订单对应激活码(目前只有第一次加入品牌会员时,需要生成订单)',
  remark         varchar(256) default '' not null
  comment '备注',
  constraint tb_user_payment_order_no_uindex
  unique (order_no)
)
  comment '用户支付表';

create index fk_tb_user_paymnet_tb_user1_idx
  on tb_user_payment (user_id);

create view vw_active_cdk as (select
                                `bc`.`brand_id`       AS `brand_id`,
                                `bc`.`gmt_create`     AS `gmt_create`,
                                `bc`.`code`           AS `code`,
                                `bc`.`used_user_id`   AS `used_user_id`,
                                `bc`.`used_time`      AS `used_time`,
                                `cb`.`name`           AS `name`,
                                `cb`.`contact_person` AS `contact_person`,
                                `cb`.`contact_phone`  AS `contact_phone`,
                                `u`.`nickname`        AS `nickname`,
                                `u`.`wx_sex`          AS `wx_sex`,
                                `u`.`wx_headimgurl`   AS `wx_headimgurl`,
                                `u`.`member_expired`  AS `member_expired`,
                                `u`.`wx_openid`       AS `wx_openid`,
                                `u`.`wx_mini_openid`  AS `wx_mini_openid`,
                                `u`.`wx_unionid`      AS `wx_unionid`,
                                `u`.`recommend_id`    AS `recommend_id`,
                                `u`.`is_agent`        AS `is_agent`,
                                `u`.`cash_balance`    AS `cash_balance`,
                                `u`.`member_type`     AS `member_type`
                              from ((`gallery`.`tb_brand_cdkey` `bc`
                                join `gallery`.`tb_company_brand` `cb`
                                  on (((`cb`.`id` = `bc`.`brand_id`) and (`cb`.`expired_time` > now()) and
                                       (`bc`.`is_used` = TRUE)))) join `gallery`.`tb_user` `u`
                                  on ((`u`.`id` = `bc`.`used_user_id`)))
                              order by `bc`.`gmt_create` desc);

