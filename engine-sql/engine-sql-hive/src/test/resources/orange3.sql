INSERT OVERWRITE TABLE DM_BI_BDPMS.yx_active_users_det_year PARTITION(bdpms_etl_time=111)

select
area_code

,area_name

,city_code

,city_name
,Activity_Id
,Biz_Nbr
,market_cfg_name
,start_date
,end_date
,rebate_fund_type
,code_desc_1
,'20200919' day_id
from DM_BI_BDPMS.yx_active_users_det_year
where day_id='20200919'
and substr('20200919',5,4) <> '0101'

union all
select
coalesce(t41.Prov_Cd,t42.Prov_Cd,t43.Prov_Cd) area_code
,coalesce(t41.Prov_Nm,t42.Prov_Nm,t43.Prov_Nm) area_name

,coalesce(t41.City_Cd,t42.City_Cd,t43.City_Cd) city_code

,coalesce(t41.City_Nm,t42.City_Nm,t43.City_Nm) city_name

,t.Activity_Id
,t.Biz_Nbr
,tmc.Activity_Nm market_cfg_name
,tmc.Activity_Start_Dt start_date
,tmc.Activity_End_Dt end_date

,tmc.rebate_fund_type
,m2.targcde_desc code_desc_1
,'20200919' day_id
from (select Activity_Id,Biz_Nbr
from edw_pdata_bdpms.T05_CAMP_REBATE_TXN t1

where bdpms_etl_time = 111
and t1.Trans_Type_Cd = '01'
and (t1.Cancel_Cd is null or lower(t1.Cancel_Cd) = 'null')
and t1.Rebate_Stat_Cd = '0'
group by Activity_Id,Biz_Nbr
) t
LEFT JOIN (select distinct Mobile_Nbr,Clec_Cd,Prov_Cd,City_Cd,Prov_Nm,City_Nm
from edw_pdata_bdpms.t06_mobile_np_internal
where bdpms_etl_time=111) t41
ON t.Biz_Nbr = t41.Mobile_Nbr
LEFT JOIN (select Id_Part,Clec_Cd,Prov_Cd,City_Cd,Prov_Nm,City_Nm from edw_pdata_bdpms.t06_section_attr_lif_internal where bdpms_etl_time=111 and length(Id_Part)=8) t42
ON substr(t.Biz_Nbr,1,8) = t42.Id_Part
LEFT JOIN (select Id_Part,Clec_Cd,Prov_Cd,City_Cd,Prov_Nm,City_Nm from edw_pdata_bdpms.t06_section_attr_lif_internal where bdpms_etl_time=111 and length(Id_Part)=7) t43
ON substr(t.Biz_Nbr,1,7) = t43.Id_Part

left join (select Activity_Id,Activity_Nm,Activity_Start_Dt,Activity_End_Dt,
case
when Rebate_Goods_Type_Cd = '1' then
'现金'
when Rebate_Goods_Type_Cd = '2' then
'代金券'
when Rebate_Goods_Type_Cd = '3' then
'流量'
when Rebate_Goods_Type_Cd = '4' then
'红包'
when Rebate_Goods_Type_Cd = '6' then
'集卡'
when Rebate_Goods_Type_Cd = '7' then
'红包金'
else
Rebate_Goods_Type_Cd
end rebate_fund_type,
Owned_Group,
Activity_Type_Cd
from EDW_PDATA_BDPMS.T07_CAMP_INTERNAL tmc
where 1=1--pt_td='#wide_day_id#' -- 系统时间-1
and Verify_Cd='2'
and bdpms_etl_time=111
) tmc
on tmc.Activity_Id = t.Activity_Id
LEFT JOIN ( SELECT targcde_cd,targcde_desc FROM edw_pdata_bdpms.t99_std_cde_map_info
WHERE UPPER(TRIM(srctab_cd))<=>UPPER('S21_T_MARKET_CFG')
AND UPPER(TRIM(cde_type))<=>UPPER('BUSINESS_GOUP') ) m2
ON m2.targcde_cd <=> tmc.owned_group;