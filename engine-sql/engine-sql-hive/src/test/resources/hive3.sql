insert overwrite table adm.dm_yingxiao_coupon_user_info
partition(dm_etl_date='111')
select
t1.mobile_nbr,
nvl(t2.trans_tm_min,'') as trans_tm_min,--第一次交易时间
nvl(t3.trans_day_cnt,0) as trans_day_cnt_15days,--前15天的交易天数
case when t4.agmt_stat is not null then 1 else 0 end quick_sign_ind,--绑卡状态
nvl(t5.login_day_cnt,0) as login_day_cnt_15days--前15天登陆天数
from adm.dm_yingxiao_coupon_user_info_proc2 t1
left join (select mobile_nbr,trans_tm_min from adm.dm_yingxiao_coupon_user_info_proc where dm_etl_date='11') t2
on t1.mobile_nbr = t2.mobile_nbr
left join (select mobile_nbr, count(distinct day_id) as trans_day_cnt from adm.shichang_sanguimo_order_detail where xiaofei_ind = '1' and day_id between '111' and '111' group by mobile_nbr) t3
on t1.mobile_nbr = t3.mobile_nbr
left join (select mobile_nbr,agmt_stat from edw_pdata_bdpms.t03_indiv_base_agmt_internal where agmt_type = 'QUICK' and agmt_stat='SIGNED' and bdpms_etl_time=111) t4
on t1.mobile_nbr = t4.mobile_nbr
left join (select login_id as mobile_nbr, count(distinct bdpms_etl_time) as login_day_cnt from edw_pdata_bdpms.t01_user_login_info_recd where bdpms_etl_time between 111 and 111 group by login_id) t5
on t1.mobile_nbr = t5.mobile_nbr;