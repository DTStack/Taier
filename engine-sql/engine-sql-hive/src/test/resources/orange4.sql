INSERT overwrite TABLE dm_licai_bdpms.t_zq_yingxiao_yw_mx PARTITION (day_id='20200919')
SELECT e.biz_type,
e.ywkhrs phone,
prov_nm,
city_nm
FROM
(SELECT c.biz_type,
c.ywrs,
d.ywkhrs,
round(1.0000*d.ywkhrs/c.ywrs,4) zhl
FROM
(SELECT a.biz_type,
count(DISTINCT a.mobile_nbr) ywrs
FROM
(SELECT *
FROM adm.shichang_sanguimo_order_detail a
WHERE a.xiaofei_ind= 1
AND a.trans_amt>=100
AND a.day_id >= '2020-04-01'
AND length(a.mobile_nbr)=11)a
JOIN
(SELECT mobile_nbr,
prov_nm,
city_nm
FROM dm_licai_bdpms.t_user_zhudongxiaofei2019
WHERE day_id = '20200919' )b ON a.mobile_nbr = b.mobile_nbr
GROUP BY a.biz_type)c
JOIN
(SELECT a.biz_type,
count(DISTINCT a.mobile_nbr) ywkhrs
FROM
(SELECT *
FROM adm.shichang_sanguimo_order_detail a
WHERE a.xiaofei_ind= 1
AND a.trans_amt>=100
AND a.day_id >= '2020-04-01'
AND length(a.mobile_nbr)=11)a
JOIN
(SELECT mobile_nbr,
prov_nm,
city_nm
FROM dm_licai_bdpms.t_user_zhudongxiaofei2019
WHERE day_id = '20200919' )b ON a.mobile_nbr = b.mobile_nbr
JOIN
(SELECT DISTINCT d.mobile_nbr,
d.open_dt
FROM edw_pdata_bdpms.t03_fin_stock_open_internal d
WHERE d.bdpms_etl_time = '20200919'
AND d.open_dt >= '2020-04-01'

          AND d.secu_corp = '18'
          AND d.mobile_nbr NOT IN
            (SELECT invited_user
             FROM edw_pdata_bdpms.t05_finc_cha_promote_recom_rela_recd))c ON c.mobile_nbr=a.mobile_nbr
     GROUP BY a.biz_type)d ON c.biz_type=d.biz_type
  WHERE d.ywkhrs>70
    AND c.biz_type NOT IN ('提现',
                           '转账')
  ORDER BY zhl DESC
  LIMIT 15) c
JOIN
(SELECT DISTINCT a.biz_type,
a.mobile_nbr ywkhrs,
prov_nm,
city_nm
FROM adm.shichang_sanguimo_order_detail a
WHERE a.xiaofei_ind= 1
AND a.trans_amt>=100
AND a.day_id >= '2020-04-01'
AND length(a.mobile_nbr)=11) e ON e.biz_type = c.biz_type