insert overwrite table adm.shichang_sanguimo_order_detail partition(src_table, day_id)
-- 3.0出账用户
select t1.Trans_Serial_Id as pk_id
,coalesce(t1.out_mobile_nbr,' ') as mobile_nbr
,coalesce(t2.Prov_nm,t3.Prov_Nm,t4.Prov_Nm,'other_prov') as prov_nm
,coalesce(t2.City_nm,t3.City_nm,t4.City_nm,'other_city') as city_nm
,coalesce(t2.Clec_Cd,t3.Clec_Cd,t4.Clec_Cd,'non_phonenum') as carrier_nm
,t1.Trans_Amt
,t1.favour_Amt
,t1.trans_tm
,t1.Trans_Cha_Cd as Cha_Type_Cd
,t1.sale_group_nm as department
,t1.biz_type
,t1.in_merchant_id as merchant_id
,t1.in_store_id as store_id
,t1.Prod_Cd as biz_code
,jiaoyi_ind
,xiaofei_ind
,'shichangbu_order_detail' as src_table
,t1.day_id
from adm.shichangbu_order_detail as t1
LEFT JOIN
(SELECT DISTINCT t4.Mobile_Nbr,t4.Clec_Cd,t4.Prov_nm,t4.prov_cd,t4.City_nm,t4.city_cd
FROM edw_pdata_bdpms.t06_mobile_np_internal t4 -- 携号转网信息表
WHERE t4.bdpms_etl_time='20200820' --固定取当月第一天
) t2
ON t1.out_mobile_nbr = t2.Mobile_Nbr
LEFT JOIN
(SELECT distinct a.Id_Part,a.Clec_Cd,a.Prov_Nm,a.Prov_Cd,a.City_Nm,a.City_Cd
FROM edw_pdata_bdpms.t06_section_attr_lif_internal a -- 号段归属表取8个号段
WHERE a.bdpms_etl_time='20200820' --固定取当月第一天
and length(a.Id_Part)=8) t3
ON substr(t1.out_mobile_nbr,1,8) = t3.Id_Part
LEFT JOIN
(select distinct a1.Id_Part,a1.Clec_Cd,a1.Prov_Nm,a1.Prov_Cd,a1.City_Nm,a1.City_Cd
from edw_pdata_bdpms.t06_section_attr_lif_internal a1 -- 号段归属表取7个号段
where a1.bdpms_etl_time='20200820' --固定取当月第一天
and length(a1.Id_Part)=7) t4
on substr(t1.out_mobile_nbr,1,7) = t4.Id_Part
where 1=1
and day_id = '20200820'
and (is3_ind='1' or (src_table='dm_licai_order_detail' and sub_biz_type in('申购')))
and (out_type_cd in ('PERS','PERSON') or out_type_cd is null)
and (biz_type not in ('代收付','认证支付','结算易','企业账户','银行卡收单') or biz_type is null)

union all
-- 3.0入账用户
select t1.Trans_Serial_Id as pk_id
,coalesce(t1.in_mobile_nbr,' ') as mobile_nbr
,coalesce(t2.Prov_nm,t3.Prov_Nm,t4.Prov_Nm,'other_prov') as prov_nm
,coalesce(t2.City_nm,t3.City_nm,t4.City_nm,'other_city') as city_nm
,coalesce(t2.Clec_Cd,t3.Clec_Cd,t4.Clec_Cd,'non_phonenum') as carrier_nm
,t1.Trans_Amt
,t1.favour_Amt
,t1.trans_tm
,t1.Trans_Cha_Cd as Cha_Type_Cd
,t1.sale_group_nm as department
,t1.biz_type
,t1.out_merchant_id as merchant_id
,'' as store_id
,t1.Prod_Cd as biz_code
,'1' as jiaoyi_ind
,'0' as xiaofei_ind
,'shichangbu_order_detail' as src_table
,t1.day_id
from adm.shichangbu_order_detail as t1
LEFT JOIN
(SELECT DISTINCT t4.Mobile_Nbr,t4.Clec_Cd,t4.Prov_nm,t4.prov_cd,t4.City_nm,t4.city_cd
FROM edw_pdata_bdpms.t06_mobile_np_internal t4 -- 携号转网信息表
WHERE t4.bdpms_etl_time='20200820' --固定取当月第一天
) t2
ON t1.in_mobile_nbr = t2.Mobile_Nbr
LEFT JOIN
(SELECT distinct a.Id_Part,a.Clec_Cd,a.Prov_Nm,a.Prov_Cd,a.City_Nm,a.City_Cd
FROM edw_pdata_bdpms.t06_section_attr_lif_internal a -- 号段归属表取8个号段
WHERE a.bdpms_etl_time='20200820' --固定取当月第一天
and length(a.Id_Part)=8) t3
ON substr(t1.in_mobile_nbr,1,8) = t3.Id_Part
LEFT JOIN
(select distinct a1.Id_Part,a1.Clec_Cd,a1.Prov_Nm,a1.Prov_Cd,a1.City_Nm,a1.City_Cd
from edw_pdata_bdpms.t06_section_attr_lif_internal a1 -- 号段归属表取7个号段
where a1.bdpms_etl_time='20200820' --固定取当月第一天
and length(a1.Id_Part)=7) t4
on substr(t1.in_mobile_nbr,1,7) = t4.Id_Part
where 1=1
and day_id = '20200820'
and src_table in ('t05_tradecenter_acq_dtl_init','T05_TRADECENTER_CAPITAL_INIT')
and (in_type_cd in ('PERS','PERSON') or in_type_cd is null)
and (biz_type not in ('代收付','认证支付','结算易','企业账户','银行卡收单') or biz_type is null)

union all

-- 个账表脚本
-- 个账必须使用该表中的省份地市
select z1.pk_id
, coalesce(z1.mobile_nbr,' ') as mobile_nbr
, coalesce(z2.prov_nm, 'other_prov') as prov_nm
, coalesce(z3.city_nm, 'other_city') as city_nm
, z1.carrier_nm
, z1.trans_amt
, z1.favour_amt
, z1.trans_tm
, z1.Cha_Type_Cd
, z1.department
, case when z1.biz_type ='话补红包' then '翼支付红包'
when z1.biz_type ='信用卡还款' then '信用卡还款'
when z1.biz_type ='个人红包' then '个人红包'
when z1.biz_type ='当面付' then '转账'
when z1.biz_type ='提现' then '提现'
when z1.biz_type ='交费助手充值' then '充值'
when z1.biz_type ='二维码' then '收款码'
when z1.biz_type ='代扣' then '交费助手'
when z1.biz_type ='转账（转入）' then '转账'
when z1.biz_type ='转账（转出）' then '转账'
else 'gezhang_other' end as biz_type
, z1.merchant_id
, z1.store_id
, z1.biz_code
, z1.jiaoyi_ind
, z1.xiaofei_ind
, 'onlinepayment' as src_table
, z1.day_id
from (
select order_id as pk_id
, phone as mobile_nbr
, prov_code as prov_cd
, city_code as city_cd
, carrier_name as carrier_nm
, trans_amt
, 0 as favour_amt
, trans_tm
, Cha_Type_Cd
, department
, busi_type as biz_type
, out_bill_merchant_id as merchant_id
, tml_id as store_id
, src_biz_type_cd as biz_code
, cast(jiaoyi as int) as jiaoyi_ind
, cast(xiaofei as int) as xiaofei_ind
, day_id
from edw_pmart_bdpms.pmt_onlinepayment_label_detail
where 1 = 1
and day_id = '20200820'
and (jiaoyi = '1' or xiaofei = '1')
) as z1
left join
(select prov_cd,prov_nm from edw_pdata_bdpms.T06_prov_portion_lif_internal as z2
where bdpms_etl_time = '20200820'
and region_id is not null
group by prov_cd,prov_nm)z2
on z1.prov_cd = z2.prov_cd
left join
(select city_cd,city_nm from edw_pdata_bdpms.T06_addr_city_lif_internal as z3
where bdpms_etl_time = '20200820'
and prov_cd<>'0'
group by city_cd,city_nm)z3
on z1.city_cd = z3.city_cd

union all

-----红包金充值脚本
select order_id as pk_id
,coalesce(z1.product_no,' ') as mobile_nbr
,coalesce(t2.Prov_nm,t3.Prov_Nm,t4.Prov_Nm,'other_prov') as prov_nm
,coalesce(t2.City_nm,t3.City_nm,t4.City_nm,'other_city') as city_nm
,coalesce(t2.Clec_Cd,t3.Clec_Cd,t4.Clec_Cd,'non_phonenum') as carrier_nm
,z1.txn_amt as trans_amt
, 0 as favour_amt
, txn_time as trans_tm
, channel as cha_type_cd
, '个金' as department
, '翼支付红包金' as biz_type
, '' as merchant_id
, '' as store_id
, jiekou as biz_code
, 1 as jiaoyi_ind
, 0 as xiaofei_ind
, 'fangli_detail' as src_table
, z1.day_id
from adm.dm_hongbao_fangli_detail z1
LEFT JOIN
(SELECT DISTINCT t4.Mobile_Nbr,t4.Clec_Cd,t4.Prov_nm,t4.prov_cd,t4.City_nm,t4.city_cd
FROM edw_pdata_bdpms.t06_mobile_np_internal t4 -- 携号转网信息表
WHERE t4.bdpms_etl_time='20200820' --固定取当月第一天
) t2
ON z1.product_no = t2.Mobile_Nbr
LEFT JOIN
(SELECT distinct a.Id_Part,a.Clec_Cd,a.Prov_Nm,a.Prov_Cd,a.City_Nm,a.City_Cd
FROM edw_pdata_bdpms.t06_section_attr_lif_internal a -- 号段归属表取8个号段
WHERE a.bdpms_etl_time='20200820' --固定取当月第一天
and length(a.Id_Part)=8) t3
ON substr(z1.product_no,1,8) = t3.Id_Part
LEFT JOIN
(select distinct a1.Id_Part,a1.Clec_Cd,a1.Prov_Nm,a1.Prov_Cd,a1.City_Nm,a1.City_Cd
from edw_pdata_bdpms.t06_section_attr_lif_internal a1 -- 号段归属表取7个号段
where a1.bdpms_etl_time='20200820' --固定取当月第一天
and length(a1.Id_Part)=7) t4
on substr(z1.product_no,1,7) = t4.Id_Part
where z1.day_id = '20200820'
and jiekou = '权益金接口'

--权益金营销返利
union all
select z1.pk_id
,z1.Mobile_Nbr as mobile_nbr
,coalesce(t2.Prov_nm,t3.Prov_Nm,t4.Prov_Nm,'other_prov') as prov_nm
,coalesce(t2.City_nm,t3.City_nm,t4.City_nm,'other_city') as city_nm
,coalesce(t2.Clec_Cd,t3.Clec_Cd,t4.Clec_Cd,'non_phonenum') as carrier_nm
,z1.par_val as trans_amt
,0 as favour_Amt
,z1.create_tm as trans_tm
,'' as Cha_Type_Cd
,'客户经营事业群' as department
,'翼支付红包' as biz_type
,'' as merchant_id
,'' as store_id
,'' as biz_code
,1 as jiaoyi_ind
,0 as xiaofei_ind
,'T03_COUPON_INFO_internal' as src_table
,substr(z1.create_tm, 1, 10) as day_id
from (select * from EDW_PDATA_BDPMS.T03_COUPON_INFO_internal
where bdpms_etl_time = '20200820'
and substr(create_tm,1,10) = '20200820') z1
inner join(select
batch_no
,market_cfg_id
from EDW_PDATA_BDPMS.t07_khjy_market_coupon_relation_internal
where bdpms_etl_time = '20200820'
and substr(market_cfg_id,1,2) <> 'A2'
)b on z1.Batch_Id = b.batch_no
inner join(select
activity_id
,activity_purpose
from adm.dm_yingxiao_activity_info
where activity_purpose in('用户拉新','拉动收入','用户促活')
)c on b.market_cfg_id = c.activity_id
LEFT JOIN
(SELECT DISTINCT t4.Mobile_Nbr,t4.Clec_Cd,t4.Prov_nm,t4.prov_cd,t4.City_nm,t4.city_cd
FROM edw_pdata_bdpms.t06_mobile_np_internal t4 -- 携号转网信息表
WHERE t4.bdpms_etl_time='20200820' --固定取当月第一天
) t2
ON z1.Mobile_Nbr = t2.Mobile_Nbr
LEFT JOIN
(SELECT distinct a.Id_Part,a.Clec_Cd,a.Prov_Nm,a.Prov_Cd,a.City_Nm,a.City_Cd
FROM edw_pdata_bdpms.t06_section_attr_lif_internal a -- 号段归属表取8个号段
WHERE a.bdpms_etl_time='20200820' --固定取当月第一天
and length(a.Id_Part)=8) t3
ON substr(z1.Mobile_Nbr,1,8) = t3.Id_Part
LEFT JOIN
(select distinct a1.Id_Part,a1.Clec_Cd,a1.Prov_Nm,a1.Prov_Cd,a1.City_Nm,a1.City_Cd
from edw_pdata_bdpms.t06_section_attr_lif_internal a1 -- 号段归属表取7个号段
where a1.bdpms_etl_time='20200820' --固定取当月第一天
and length(a1.Id_Part)=7) t4
on substr(z1.Mobile_Nbr,1,7) = t4.Id_Part