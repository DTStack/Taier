insert overwrite table dm_xfjr_bdpms.stats_cfq_yy_active_users partition(day_id = '20200910')

select
        count(distinct case when datediff( '20200910',today.complete_dt ) < 30 then today.product_no end) new_complete_users_num,   --近30天新增竣工用户数
        count(distinct case when datediff( '20200910',today.complete_dt ) < 30 and today.login_id is not null then today.product_no end) new_complete_uv,   --近30天新增竣工用户当日浏览
UV,
        count(distinct case when datediff( '20200910',today.complete_dt ) < 30 then today.mobile_nbr end) new_complete_consume_uv,  --近30天新增竣工用户当日消费UV
        count(distinct today.product_no) in_loan_users_num,     --在贷用户数
        count(distinct case when today.login_id is not null then today.product_no end) in_loan_uv,      --在贷用户活跃UV
        count(distinct today.mobile_nbr) in_loan_consume_uv,    --在贷用户主动消费UV
        count(distinct case when lastday.product_no is not null and today.login_id is not null then today.product_no end),      --前一日在贷用户活跃UV当日留存UV
        null,
        null,
        null,
        null
from
(
        select
                product_no
        from
        (
                select date_format(complete_time, 'yyyy-MM-dd') complete_dt, product_no, account_id, operate_no
                from ods_installmix_bdpms.t_info_mix_business_order_internal
                where bdpms_etl_time = cast(concat('20200910','000000') as bigint)
                and txn_status = '4'
                and (merchant_code not like '%TEST%' or (accept_channel = '05' and merchant_code is null))
                group by date_format(complete_time, 'yyyy-MM-dd'), product_no, account_id, operate_no
        )a      --全量竣工用户
        join
        (
                select account_id
                from ods_installmix_bdpms.t_info_repay_plan_cfq_internal
                where bdpms_etl_time = cast(concat('20200910','000000') as bigint)
                and status = 'OPEN'
                and archive_flag='0'
                group by account_id
        )b      --当前在贷
        on a.account_id = b.account_id
        join
        (
                SELECT login_id
                FROM edw_pdata_bdpms.T01_USER_LOGIN_INFO_RECD
                                WHERE bdpms_etl_time = cast(concat('20200910','000000') as bigint)
                AND to_date(login_tm) = '20200910'
                AND login_stat='SUCCESS'
                group by login_id
        )d      --客户端活跃UV
        on a.product_no = d.login_id
)lastday        --上一日在贷用户访问
full outer join
(
        select a.complete_dt, a.product_no, d.login_id, c.mobile_nbr
        from
        (
                select date_format(complete_time, 'yyyy-MM-dd') complete_dt, product_no, account_id, operate_no
                from ods_installmix_bdpms.t_info_mix_business_order_internal
                where bdpms_etl_time = concat('20200910','000000')
                and txn_status = '4'
                and (merchant_code not like '%TEST%' or (accept_channel = '05' and merchant_code is null))
                group by date_format(complete_time, 'yyyy-MM-dd'), product_no, account_id, operate_no
        )a      --全量竣工用户
        join
        (
                select account_id
                from ods_installmix_bdpms.t_info_repay_plan_cfq_internal
                where bdpms_etl_time = concat('20200910','000000')
                and status = 'OPEN'
                and archive_flag='0'
                group by account_id
        )b      --当前在贷
        on a.account_id = b.account_id
        left join
        (
                SELECT mobile_nbr, substr(day_id,1,10) day_id
                FROM adm.shichang_sanguimo_order_detail a
                WHERE trans_amt >= 100
                AND xiaofei_ind = 1
                AND
                (
                        biz_type NOT IN ('交费助手代扣','代扣','交费助手','橙分期','电信场景险')
                        AND merchant_id !='3178033687559551'
                        OR biz_code IN ('71000060215','71000060216','71000060217','71000060218')
                                                OR (merchant_id ='3178033687559551' AND biz_code ='68800020205')
                )
                AND substr(day_id, 1, 10) = '20200910'
                group by mobile_nbr, substr(day_id,1,10)
        )c      --主动消费行为
        on a.product_no = c.mobile_nbr
        left join
        (
                SELECT login_id
                FROM edw_pdata_bdpms.T01_USER_LOGIN_INFO_RECD
                WHERE bdpms_etl_time = concat('20200910','000000')
                AND to_date(login_tm) = '20200910'
                AND login_stat='SUCCESS'
                group by login_id
        )d      --客户端活跃UV
        on a.product_no = d.login_id
)today
on lastday.product_no = today.product_nor