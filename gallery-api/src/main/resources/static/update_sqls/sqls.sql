drop view if exists vw_active_cdk;
create view vw_active_cdk as (select bc.brand_id, bc.gmt_create, bc.code, bc.used_user_id, bc.used_time, cb.name, cb.contact_person, cb.contact_phone
                                ,u.nickname, u.wx_sex, u.wx_headimgurl, u.member_expired, u.wx_openid, u.wx_mini_openid, u.wx_unionid, u.recommend_id,
                                u.is_agent, u.cash_balance, u.member_type from tb_brand_cdkey as bc inner join
  tb_company_brand as cb on cb.id = bc.brand_id and cb.expired_time < now() and bc.is_used = true inner join
  tb_user as u on u.id = bc.used_user_id order by bc.gmt_create desc);