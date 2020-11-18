insert overwrite table adm.dm_hongbao_trans_detail partition(day_id='20200820')
select
tdaprovince
,tdaccity
,product_no
,txn_time
,txn_amt
,txn_channel
,business_type
,busi_type
,xiaoorchong
---,substr(txn_time,1,10) day_id  ---一次性补数的时候可以用
from
(select tdaprovince,tdaccity,product_no,min(txn_time) min_time from adm.dm_hongbao_fangli_detail
where day_id>='2017-01-01'  ---保持不变
and day_id <= '20200820'
group by tdaprovince,tdaccity,product_no
)t1
inner join
(select
 mobile_nbr phone
,trans_tm txn_time
,trans_amt txn_amt
,cha_type_cd txn_channel
,biz_code business_type
,biz_type busi_type
,xiaofei_ind xiaoorchong
from adm.shichang_sanguimo_order_detail
where substr(day_id,1,10)='20200820'
--and xiaofei=1
and length(mobile_nbr)=11
)t2 on t1.product_no=t2.phone
where t2.txn_time>=t1.min_time;