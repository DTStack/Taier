INSERT overwrite TABLE dm_licai_bdpms.t_zq_yingxiao PARTITION (day_id='20200919')
SELECT tz,
tzz,
rs,
khrs,
zhl,
dx,
dxkh,
dxzhl,
rank() OVER (
ORDER BY zhl DESC) pm,
rank() OVER (
ORDER BY dxzhl DESC) dxpm,
(rank() OVER (
ORDER BY zhl DESC)+rank() OVER (
ORDER BY dxzhl DESC))/2 zhpm,
round(1.00*100/(count() over(PARTITION BY 1))*rank() OVER (
ORDER BY zhl),2) fs,
round(1.00*100/(count() over(PARTITION BY 1))*rank() OVER (
ORDER BY dxzhl),2) dxfs,
round(1.00*100/(count() over(PARTITION BY 1))*((rank() OVER (
ORDER BY zhl)+rank() OVER (
ORDER BY dxzhl))/2),2) zhfs
FROM
(--访问
SELECT '访问' tz,
a.cnt tzz,
a.fwrs rs,
b.fwkhrs khrs,
round(1.0000*b.fwkhrs/a.fwrs,4) zhl,
aa.fwrs dx,
aa.fwkhrs dxkh,
aa.zhl dxzhl
FROM
(SELECT CASE
WHEN cnt=1 THEN '1'
WHEN cnt<=10 THEN '2-10'
WHEN cnt<=20 THEN '10-20'
WHEN cnt<=30 THEN '20-30'
WHEN cnt<=40 THEN '30-40'
WHEN cnt<=50 THEN '40-50'
WHEN cnt<=60 THEN '50-60'
WHEN cnt<=70 THEN '60-70'
WHEN cnt<=80 THEN '70-80'
WHEN cnt<=90 THEN '80-90'
WHEN cnt<=100 THEN '90-100'
ELSE '100次以上'
END cnt,
count(t3.mobile_nbr) fwrs
FROM
(SELECT *
FROM dm_licai_bdpms.t_zq_shouyedianji
WHERE day_id = '20200919') t3
JOIN
(SELECT mobile_nbr,
prov_nm,
city_nm
FROM dm_licai_bdpms.t_user_zhudongxiaofei2019
WHERE day_id = '20200919' )b ON t3.mobile_nbr = b.mobile_nbr --WHERE t3.day_id = '$(ct.format(""yyyy-MM-dd""))'

  GROUP BY CASE
               WHEN cnt=1 THEN '1'
               WHEN cnt<=10 THEN '2-10'
               WHEN cnt<=20 THEN '10-20'
               WHEN cnt<=30 THEN '20-30'
               WHEN cnt<=40 THEN '30-40'
               WHEN cnt<=50 THEN '40-50'
               WHEN cnt<=60 THEN '50-60'
               WHEN cnt<=70 THEN '60-70'
               WHEN cnt<=80 THEN '70-80'
               WHEN cnt<=90 THEN '80-90'
               WHEN cnt<=100 THEN '90-100'
               ELSE '100次以上'
           END) a
JOIN
(SELECT CASE
WHEN cnt=1 THEN '1'
WHEN cnt<=10 THEN '2-10'
WHEN cnt<=20 THEN '10-20'
WHEN cnt<=30 THEN '20-30'
WHEN cnt<=40 THEN '30-40'
WHEN cnt<=50 THEN '40-50'
WHEN cnt<=60 THEN '50-60'
WHEN cnt<=70 THEN '60-70'
WHEN cnt<=80 THEN '70-80'
WHEN cnt<=90 THEN '80-90'
WHEN cnt<=100 THEN '90-100'
ELSE '100次以上'
END cnt,
count(t3.mobile_nbr) fwkhrs
FROM
(SELECT *
FROM dm_licai_bdpms.t_zq_shouyedianji
WHERE day_id = '20200919') t3
JOIN
(SELECT mobile_nbr,
prov_nm,
city_nm
FROM dm_licai_bdpms.t_user_zhudongxiaofei2019
WHERE day_id = '20200919' )b ON t3.mobile_nbr = b.mobile_nbr
JOIN
(SELECT DISTINCT d.mobile_nbr,
to_date(d.compl_tm) open_dt
FROM edw_pdata_bdpms.t05_stock_user_open_info_internal d
WHERE d.bdpms_etl_time = '111'
AND to_date(d.compl_tm) >= '2020-04-01'
AND d.securitie_id =18
AND d.tag_id=1
AND d.mobile_nbr NOT IN
(SELECT invited_user
FROM edw_pdata_bdpms.t05_finc_cha_promote_recom_rela_recd))c ON c.mobile_nbr=t3.mobile_nbr --WHERE t3.day_id = '$(ct.format(""yyyy-MM-dd""))'

  GROUP BY CASE
               WHEN cnt=1 THEN '1'
               WHEN cnt<=10 THEN '2-10'
               WHEN cnt<=20 THEN '10-20'
               WHEN cnt<=30 THEN '20-30'
               WHEN cnt<=40 THEN '30-40'
               WHEN cnt<=50 THEN '40-50'
               WHEN cnt<=60 THEN '50-60'
               WHEN cnt<=70 THEN '60-70'
               WHEN cnt<=80 THEN '70-80'
               WHEN cnt<=90 THEN '80-90'
               WHEN cnt<=100 THEN '90-100'
               ELSE '100次以上'
           END)b ON a.cnt=b.cnt
JOIN --短信

 (SELECT a.cnt,
         a.fwrs,
         b.fwkhrs,
         round(1.0000*b.fwkhrs/a.fwrs,4) zhl
  FROM
    (SELECT CASE
                WHEN cnt=1 THEN '1'
                WHEN cnt<=10 THEN '2-10'
                   WHEN cnt<=20 THEN '10-20'
                WHEN cnt<=30 THEN '20-30'
                WHEN cnt<=40 THEN '30-40'
                WHEN cnt<=50 THEN '40-50'
                WHEN cnt<=60 THEN '50-60'
                WHEN cnt<=70 THEN '60-70'
                WHEN cnt<=80 THEN '70-80'
                WHEN cnt<=90 THEN '80-90'
                WHEN cnt<=100 THEN '90-100'
                ELSE '100次以上'
            END cnt,
            count(t3.mobile_nbr) fwrs
     FROM
       (SELECT *
        FROM dm_licai_bdpms.t_zq_shouyedianji
        WHERE day_id = '20200919') t3
     JOIN dm_licai_bdpms.t_user_duanxinchuda a1 ON t3.mobile_nbr = a1.receive_number
     AND a1.day_id = '20200919' --WHERE t3.day_id = '$(ct.format(""yyyy-MM-dd""))'

     GROUP BY CASE
                  WHEN cnt=1 THEN '1'
                  WHEN cnt<=10 THEN '2-10'
                  WHEN cnt<=20 THEN '10-20'
                  WHEN cnt<=30 THEN '20-30'
                  WHEN cnt<=40 THEN '30-40'
                  WHEN cnt<=50 THEN '40-50'
                  WHEN cnt<=60 THEN '50-60'
                  WHEN cnt<=70 THEN '60-70'
                  WHEN cnt<=80 THEN '70-80'
                  WHEN cnt<=90 THEN '80-90'
                  WHEN cnt<=100 THEN '90-100'
                  ELSE '100次以上'
              END) a
  JOIN
    (SELECT CASE
                WHEN cnt=1 THEN '1'
                WHEN cnt<=10 THEN '2-10'
                WHEN cnt<=20 THEN '10-20'
                WHEN cnt<=30 THEN '20-30'
                WHEN cnt<=40 THEN '30-40'
                WHEN cnt<=50 THEN '40-50'
                WHEN cnt<=60 THEN '50-60'
                WHEN cnt<=70 THEN '60-70'
                 WHEN cnt<=80 THEN '70-80'
                WHEN cnt<=90 THEN '80-90'
                WHEN cnt<=100 THEN '90-100'
                ELSE '100次以上'
            END cnt,
            count(t3.mobile_nbr) fwkhrs
     FROM
       (SELECT *
        FROM dm_licai_bdpms.t_zq_shouyedianji
        WHERE day_id = '20200919') t3
     JOIN dm_licai_bdpms.t_user_duanxinchudazhuanhua a1 ON t3.mobile_nbr = a1.receive_number
     AND a1.day_id = '20200919' --WHERE t3.day_id = '$(ct.format(""yyyy-MM-dd""))'

     GROUP BY CASE
                  WHEN cnt=1 THEN '1'
                  WHEN cnt<=10 THEN '2-10'
                  WHEN cnt<=20 THEN '10-20'
                  WHEN cnt<=30 THEN '20-30'
                  WHEN cnt<=40 THEN '30-40'
                  WHEN cnt<=50 THEN '40-50'
                  WHEN cnt<=60 THEN '50-60'
                  WHEN cnt<=70 THEN '60-70'
                  WHEN cnt<=80 THEN '70-80'
                  WHEN cnt<=90 THEN '80-90'
                  WHEN cnt<=100 THEN '90-100'
                  ELSE '100次以上'
              END) b ON a.cnt=b.cnt)aa ON aa.cnt=a.cnt
UNION ALL --业务
SELECT tz,
tzz,
rs,
khrs,
zhl,
dx,
dxkh,
dxzhl
FROM
tmp_zhengquan.t_zq_yingxiao_one
WHERE day_id = '20200919'
UNION ALL --城市
SELECT '城市' tz,
e.city_lvl tzz,
e.csrs rs,
f.cskhrs khrs,
round(1.0000*f.cskhrs/e.csrs,4) zhl,
aa.csrs dx,
aa.cskhrs dxkh,
aa.zhl dxzhl
FROM
(SELECT count(DISTINCT b.mobile_nbr) csrs,
city_lvl
FROM
(SELECT mobile_nbr,
prov_nm,
city_nm
FROM dm_licai_bdpms.t_user_zhudongxiaofei2019
WHERE day_id = '20200919' )b
JOIN dm_licai_bdpms.t_citv_lvl_2019 f ON b.city_nm=f.city_nm
GROUP BY city_lvl)e
JOIN
(SELECT count(DISTINCT b.mobile_nbr) cskhrs,
city_lvl
FROM
(SELECT mobile_nbr,
prov_nm,
city_nm
FROM dm_licai_bdpms.t_user_zhudongxiaofei2019
WHERE day_id = '20200919' )b
JOIN
(SELECT DISTINCT d.mobile_nbr,
to_date(d.compl_tm) open_dt
FROM edw_pdata_bdpms.t05_stock_user_open_info_internal d
WHERE d.bdpms_etl_time = '111'
AND to_date(d.compl_tm) >= '2020-04-01'
AND d.securitie_id =18
AND d.tag_id=1
AND d.mobile_nbr NOT IN
(SELECT invited_user
FROM edw_pdata_bdpms.t05_finc_cha_promote_recom_rela_recd))c ON c.mobile_nbr=b.mobile_nbr
JOIN dm_licai_bdpms.t_citv_lvl_2019 f ON b.city_nm=b.city_nm
GROUP BY city_lvl)f ON e.city_lvl=f.city_lvl --短信
JOIN
(SELECT c.city_lvl,
c.csrs,
b.cskhrs,
round(1.0000*b.cskhrs/c.csrs,4) zhl
FROM
(SELECT city_lvl,
count(DISTINCT mobile_nbr) csrs
FROM
(SELECT DISTINCT t.mobile_nbr,
coalesce(t41.City_Nm,t42.City_Nm,t43.City_Nm,'other') AS city_nm
FROM
(SELECT receive_number mobile_nbr,
to_date(finished_date)
FROM dm_licai_bdpms.t_user_duanxinchuda
WHERE day_id = '20200919')t
LEFT JOIN
(SELECT DISTINCT t4.Mobile_Nbr,
t4.Clec_Cd,
t4.Prov_nm,
t4.prov_cd,
t4.City_nm,
t4.city_cd
FROM edw_pdata_bdpms.t06_mobile_np_internal t4 -- 携号转网信息表

           WHERE t4.bdpms_etl_time='111' ) t41 ON t.Mobile_Nbr = t41.Mobile_Nbr
        LEFT JOIN
          (SELECT DISTINCT a.Id_Part,
                           a.Clec_Cd,
                           a.Prov_Nm,
                           a.Prov_Cd,
                           a.City_Nm,
                           a.City_Cd
           FROM edw_pdata_bdpms.t06_section_attr_lif_internal a -- 号段归属表取8个号段

           WHERE a.bdpms_etl_time='111'
             AND length(a.Id_Part)=8) t42 ON substr(t.Mobile_Nbr,1,8) = t42.Id_Part
        LEFT JOIN
          (SELECT DISTINCT a1.Id_Part,
                           a1.Clec_Cd,
                           a1.Prov_Nm,
                           a1.Prov_Cd,
                           a1.City_Nm,
                           a1.City_Cd
           FROM edw_pdata_bdpms.t06_section_attr_lif_internal a1 -- 号段归属表取7个号段
            WHERE a1.bdpms_etl_time='111'
             AND length(a1.Id_Part)=7) t43 ON substr(t.Mobile_Nbr,1,7) = t43.Id_Part -- GROUP BY coalesce(t41.City_Nm,t42.City_Nm,t43.City_Nm,'other')
)b
JOIN dm_licai_bdpms.t_citv_lvl_2019 f ON b.city_nm=f.city_nm
GROUP BY city_lvl)c
JOIN
(SELECT city_lvl,
count(DISTINCT mobile_nbr) cskhrs
FROM
(SELECT DISTINCT t.mobile_nbr,
coalesce(t41.City_Nm,t42.City_Nm,t43.City_Nm,'other') AS city_nm
FROM
(SELECT receive_number mobile_nbr,
to_date(finished_date)
FROM dm_licai_bdpms.t_user_duanxinchudazhuanhua
WHERE day_id = '20200919')t
LEFT JOIN
(SELECT DISTINCT t4.Mobile_Nbr,
t4.Clec_Cd,
t4.Prov_nm,
t4.prov_cd,
t4.City_nm,
t4.city_cd
FROM edw_pdata_bdpms.t06_mobile_np_internal t4 -- 携号转网信息表

           WHERE t4.bdpms_etl_time='111' ) t41 ON t.Mobile_Nbr = t41.Mobile_Nbr
        LEFT JOIN
          (SELECT DISTINCT a.Id_Part,
                           a.Clec_Cd,
                           a.Prov_Nm,
                           a.Prov_Cd,
                           a.City_Nm,
                           a.City_Cd
           FROM edw_pdata_bdpms.t06_section_attr_lif_internal a -- 号段归属表取8个号段

           WHERE a.bdpms_etl_time='111'
             AND length(a.Id_Part)=8) t42 ON substr(t.Mobile_Nbr,1,8) = t42.Id_Part
        LEFT JOIN
          (SELECT DISTINCT a1.Id_Part,
                           a1.Clec_Cd,
                           a1.Prov_Nm,
                           a1.Prov_Cd,
                           a1.City_Nm,
                           a1.City_Cd
           FROM edw_pdata_bdpms.t06_section_attr_lif_internal a1 -- 号段归属表取7个号段

           WHERE a1.bdpms_etl_time='111'
                            AND length(a1.Id_Part)=7) t43 ON substr(t.Mobile_Nbr,1,7) = t43.Id_Part -- GROUP BY coalesce(t41.City_Nm,t42.City_Nm,t43.City_Nm,'other')
)b
JOIN dm_licai_bdpms.t_citv_lvl_2019 f ON b.city_nm=f.city_nm
GROUP BY city_lvl)b ON c.city_lvl=b.city_lvl)aa ON aa.city_lvl=e.city_lvl
UNION ALL --年龄
SELECT '年龄' tz,
g.age tzz,
g.nlrs rs,
h.nlkhrs khrs,
round(1.0000*h.nlkhrs/g.nlrs,4) zhl,
aa.nlrs dx,
aa.nlkhrs dxkh,
aa.zhl dxzhl
FROM
(SELECT CASE
WHEN age = '20200919'
and mobile_stat='正常') a ON a.mobile_nbr=b.mobile_nbr
GROUP BY CASE
WHEN age = '20200919'
AND mobile_stat='正常') a ON a.mobile_nbr=b.mobile_nbr
JOIN
(SELECT DISTINCT d.mobile_nbr,
to_date(d.compl_tm) open_dt
FROM edw_pdata_bdpms.t05_stock_user_open_info_internal d
WHERE d.bdpms_etl_time = '111'
AND to_date(d.compl_tm) >= '2020-04-01'
AND d.securitie_id =18
AND d.tag_id=1
AND d.mobile_nbr NOT IN
(SELECT invited_user
FROM edw_pdata_bdpms.t05_finc_cha_promote_recom_rela_recd))c ON c.mobile_nbr=b.mobile_nbr --WHERE a.day_id = '$(ct.format(""yyyy-MM-dd""))'
-- AND a.mobile_stat='正常'

  GROUP BY CASE
               WHEN age <5 THEN '小于5'
               WHEN age <10 THEN '5-10'
               WHEN age <20 THEN '10-20'
               WHEN age <30 THEN '20-30'
               WHEN age <40 THEN '30-40'
               WHEN age <50 THEN '40-50'
               WHEN age <60 THEN '50-60'
               WHEN age <70 THEN '60-70'
               WHEN age <80 THEN '70-80'
               WHEN age <90 THEN '80-90'
               WHEN age <100 THEN '90-100'
               ELSE '100+'
           END)h ON g.age=h.age
JOIN --短信

 (SELECT c.age,
         c.nlrs,
         b.nlkhrs,
         round(1.0000*b.nlkhrs/c.nlrs,4) zhl
  FROM
    (SELECT CASE
                WHEN age <5 THEN '小于5'
                WHEN age <10 THEN '5-10'
                WHEN age <20 THEN '10-20'
                WHEN age <30 THEN '20-30'
                WHEN age <40 THEN '30-40'
                WHEN age <50 THEN '40-50'
                WHEN age <60 THEN '50-60'
                WHEN age <70 THEN '60-70'
                WHEN age <80 THEN '70-80'
                WHEN age <90 THEN '80-90'
                WHEN age <100 THEN '90-100'
                ELSE '100+'
            END AS age,
            count(DISTINCT a.mobile_nbr) nlrs
     FROM
       (SELECT *
        FROM adm.user_profile_user_base_label
        WHERE day_id >= '20200919'
                     AND mobile_stat='正常') a
     JOIN dm_licai_bdpms.t_user_duanxinchuda a1 ON a.mobile_nbr = a1.receive_number
     AND a1.day_id = '20200919' --WHERE a.day_id = '$(ct.format(""yyyy-MM-dd""))'
-- AND a.mobile_stat='正常'

     GROUP BY CASE
                  WHEN age <5 THEN '小于5'
                  WHEN age <10 THEN '5-10'
                  WHEN age <20 THEN '10-20'
                  WHEN age <30 THEN '20-30'
                  WHEN age <40 THEN '30-40'
                  WHEN age <50 THEN '40-50'
                  WHEN age <60 THEN '50-60'
                  WHEN age <70 THEN '60-70'
                  WHEN age <80 THEN '70-80'
                  WHEN age <90 THEN '80-90'
                  WHEN age <100 THEN '90-100'
                  ELSE '100+'
              END)c
  JOIN
    (SELECT CASE
                WHEN age <5 THEN '小于5'
                WHEN age <10 THEN '5-10'
                WHEN age <20 THEN '10-20'
                WHEN age <30 THEN '20-30'
                WHEN age <40 THEN '30-40'
                WHEN age <50 THEN '40-50'
                WHEN age <60 THEN '50-60'
                WHEN age <70 THEN '60-70'
                WHEN age <80 THEN '70-80'
                WHEN age <90 THEN '80-90'
                WHEN age <100 THEN '90-100'
                ELSE '100+'
            END AS age,
            count(DISTINCT a.mobile_nbr) nlkhrs
     FROM
       (SELECT *
        FROM adm.user_profile_user_base_label
        WHERE day_id >= '20200919'
          AND mobile_stat='正常') a
     JOIN dm_licai_bdpms.t_user_duanxinchudazhuanhua a1 ON a.mobile_nbr = a1.receive_number
     AND a1.day_id = '20200919' -- WHERE a.day_id = '$(ct.format(""yyyy-MM-dd""))'
-- AND a.mobile_stat='正常'
GROUP BY CASE
WHEN age = '2020-04-01'
AND d.securitie_id =18
AND d.tag_id=1
AND d.mobile_nbr NOT IN
(SELECT invited_user
FROM edw_pdata_bdpms.t05_finc_cha_promote_recom_rela_recd))c ON c.mobile_nbr=b.mobile_nbr
JOIN
(SELECT *
FROM adm.user_profile_user_base_label
WHERE day_id = '20200919'
AND mobile_stat='正常') a ON a.mobile_nbr=b.mobile_nbr --WHERE a.day_id = '$(ct.format(""yyyy-MM-dd""))'
-- AND a.mobile_stat='正常'

  GROUP BY round(aging,0))j ON i.aging=j.aging --短信
JOIN
(SELECT c.aging,
c.zlrs,
b.zlkhrs,
round(1.0000*b.zlkhrs/c.zlrs,4) zhl
FROM
(SELECT round(aging,0) aging,
count(DISTINCT a.mobile_nbr) zlrs
FROM
(SELECT *
FROM adm.user_profile_user_base_label
WHERE day_id = '20200919'
AND mobile_stat='正常') a
JOIN dm_licai_bdpms.t_user_duanxinchuda a1 ON a.mobile_nbr = a1.receive_number
AND a1.day_id = '20200919' -- WHERE a.day_id = '$(ct.format(""yyyy-MM-dd""))'
-- AND a.mobile_stat='正常'

     GROUP BY round(aging,0))c
  JOIN
    (SELECT round(aging,0) aging,
            count(DISTINCT a.mobile_nbr) zlkhrs
     FROM
       (SELECT *
        FROM adm.user_profile_user_base_label
        WHERE day_id = '20200919'
          AND mobile_stat='正常') a
     JOIN dm_licai_bdpms.t_user_duanxinchudazhuanhua a1 ON a.mobile_nbr = a1.receive_number
     AND a1.day_id = '20200919' -- WHERE a.day_id = '$(ct.format(""yyyy-MM-dd""))'
      --   AND a.mobile_stat='正常'

     GROUP BY round(aging,0))b ON c.aging=b.aging) aa ON aa.aging=i.aging
UNION ALL SELECT tz,
tzz,
rs,
khrs,
zhl,
dx,
dxkh,
dxzhl
FROM dm_licai_bdpms.t_zq_yingxiao_two
WHERE day_id = '20200919'
UNION ALL SELECT tz,
tzz,
rs,
khrs,
zhl,
dx,
dxkh,
dxzhl
FROM tmp_zhengquan.t_zq_yingxiao_three
WHERE day_id = '20200919'
);

