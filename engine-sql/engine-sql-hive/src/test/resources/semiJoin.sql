insert overwrite table adm.dm_hongbao_xiaofei_detail_proc
partition(dm_etl_date='20200919')
select * from (
select
mobile_nbr as product_no,
trans_tm,
orders.biz_type,
trans_amt,
1 as trans_num,
orders.day_id
from adm.shichang_sanguimo_order_detail orders
left semi join (
select product_no
from adm.dm_hongbao_user_info
where dm_etl_date='20200919'
and to_date(first_txn_time)<'20200919') users
on orders.mobile_nbr = users.product_no
where orders.day_id='20200919'
and orders.xiaofei_ind = '1'

union all

select /*+MAPJOIN(users)*/
    mobile_nbr as product_no,
    trans_tm,
    orders.biz_type,
    trans_amt,
    1 as trans_num,
    orders.day_id
from adm.shichang_sanguimo_order_detail orders
left semi join (
        select product_no
        from adm.dm_hongbao_user_info
        where dm_etl_date='20200919'
          and to_date(first_txn_time)='20200919') users
    on orders.mobile_nbr = users.product_no
where orders.day_id >= '2018-01-01'
  and orders.day_id <= '20200919'
  and orders.xiaofei_ind = '1') tmp
distribute by 1;