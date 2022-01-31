INSERT INTO `rdos_batch_task_template`(`task_type`,`type`,`content`)
VALUES
  ('0', '1',
'create table if not exists ods_order_header (
     order_header_id     string comment ''订单头id''
    ,order_date          bigint comment ''订单日期''
    ,shop_id             string comment ''店铺id''
    ,customer_id         string comment ''客户id''
    ,order_status        bigint comment ''订单状态''
    ,pay_date            bigint comment ''支付日期''

)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;

create table if not exists ods_order_detail (
     order_header_id     string comment ''订单头id''
    ,order_detail_id     string comment ''订单明细id''
    ,item_id             string comment ''商品id''
    ,quantity            double comment ''商品数量''
    ,unit_price          double comment ''商品单价''
    ,dist_amout          double comment ''折扣金额''
)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;


create table if not exists exam_ods_shop_info (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
    ,status                 string comment ''店铺状态,open/closed''
)comment ''店铺维度表''
PARTITIONED BY (ds string) ;'),
('0','2',
'create table if not exists exam_dwd_sales_ord_df (
     order_header_id     string comment ''订单头id''
    ,order_detail_id     string comment ''订单明细id''
    ,order_date          bigint comment ''订单日期''
    ,pay_date            bigint comment ''付款日期''
    ,shop_id             string comment ''店铺id''
    ,customer_id         string comment ''客户id''
    ,item_id             string comment ''商品id''
    ,quantity            bigint comment ''商品数量''
    ,unit_price          double comment ''商品单价''
    ,amount              double comment ''总金额''
)comment ''销售订单明细表''
PARTITIONED BY (ds string) ;


INSERT OVERWRITE TABLE exam_dwd_sales_ord_df PARTITION(ds = ''${bdp.system.bizdate}'')
select
 d.order_header_id
,d.order_detail_id
,h.order_date
,h.pay_date
,h.shop_id
,h.customer_id
,d.item_id
,d.quantity
,d.unit_price
,d.quantity*d.unit_price-d.dist_amout as amount
from ods_order_header as h join ods_order_detail as d on h.order_header_id = d.order_header_id
where h.ds = ''${bdp.system.bizdate}'' and d.ds= ''${bdp.system.bizdate}''
and h.order_status = 0;
'),
('0','3',
'create table if not exists exam_dws_sales_shop_1d (
     stat_date              string comment ''统计日期''
    ,shop_id                string comment ''订单明细id''
    ,ord_quantity_1d        bigint comment ''最近一天订单数量''
    ,ord_amount_1d          double comment ''最近一天订单金额''
    ,pay_quantity_1d        bigint comment ''最近一天付款数量''
    ,pay_amount_1d          double comment ''最近一天付款金额''
)comment ''最近一天门店粒度销售汇总表''
PARTITIONED BY (ds string) ;

INSERT OVERWRITE TABLE exam_dws_sales_shop_1d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,shop_id
,sum(case when order_date = ''${bdp.system.bizdate}'' then quantity end) as ord_quantity_1d
,sum(case when order_date = ''${bdp.system.bizdate}'' then amount end)   as ord_amount_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then quantity end) as pay_quantity_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then amount end)   as pay_amount_1d
from
exam_dwd_sales_ord_df
where ds = ''${bdp.system.bizdate}''
group by shop_id;'),
('0','4',
'create table if not exists exam_ads_sales_all_d (
     stat_date              string comment ''统计日期''
    ,ord_quantity           bigint comment ''订单数量''
    ,ord_amount             double comment ''订单金额''
    ,pay_quantity           bigint comment ''付款数量''
    ,pay_amount             double comment ''付款金额''
    ,shop_cnt               bigint comment ''有交易的店铺数量''
)comment ''订单交易总表''
PARTITIONED BY (ds string) lifecycle 7;

INSERT OVERWRITE TABLE exam_ads_sales_all_d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,sum(ord_quantity_1d) as ord_quantity
,sum(ord_amount_1d)   as ord_amount
,sum(pay_quantity_1d) as pay_quantity
,sum(pay_amount_1d)   as pay_amount
,count(distinct shop_id) as shop_cnt
from
exam_dws_sales_shop_1d
where ds = ''${bdp.system.bizdate}''
group by shop_id;'),
('0','5',
'create table if not exists exam_dim_shop (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
)comment ''店铺维度表''
PARTITIONED BY (ds string) lifecycle 365;

INSERT OVERWRITE TABLE exam_dim_shop PARTITION(ds = ''${bdp.system.bizdate}'')
select
 shop_id
,shop_name
,shop_type
,address
from exam_ods_shop_info
where ds = ''${bdp.system.bizdate}''
and status = ''open'';');
insert into rdos_batch_task_template(task_type,type,content)
value(15,0,'create table if not exists customer_base
(
    id                    varchar(20),
	cust_name             varchar(20),
	cust_phone            varchar(20),
	cust_wechat           varchar(20),
	cust_cefi_number      varchar(20),
	cust_car_number       varchar(20),
	cust_house_number     varchar(20),
	cust_job              varchar(20),
	cust_company          varchar(20),
	cust_work_address     varchar(20),
	cust_bank_number      varchar(20),
	cust_gate_card        varchar(20)
);
-- COMMENT ON customer_base IS ''用户基础表'';


create table if not exists customer_in_call
(
    id                        varchar,
    in_call_phone_number      varchar,
    in_call_time              varchar,--本来这个字段是时间戳类型的，但是libra不支持时间戳timestamp关键字，所以改成varchar类型
    in_call_duration          bigint,
    in_call_consult_problem   varchar
);
-- COMMENT ON customer_in_call IS ''用户来电记录表'';


create table if not exists customer_in_and_out
(
    id                      varchar,
    cust_gate_card          varchar,
    in_or_out               varchar,
    in_or_out_time          varchar
);
-- COMMENT ON customer_in_and_out IS ''客户出入记录表'';


create table if not exists customer_complain(
    id                        varchar,
    complain_phone            varchar,
    complain_name             varchar,
    complain_problem          varchar,
    complain_time             varchar
);

-- COMMENT ON customer_complain IS ''客户投诉记录表'';

-- --注意，Libra不支持时间戳类型的关键字
');
insert into rdos_batch_task_template(task_type,type,content)
value(15,1,'
create table if not exists ods_order_header (
    order_header_id     varchar,
    order_date          bigint,
    shop_id             bigint,
    customer_id         varchar,
    order_status        bigint,
    pay_date            bigint
);

create table if not exists ods_order_detail (
    order_header_id     varchar,
    order_detail_id     varchar,
    item_id             varchar,
    quantity            varchar,
    unit_price          varchar,
    dist_amout          varchar
);

create table if not exists exam_ods_shop_info (
    shop_id                bigint,
    shop_name              varchar,
    shop_type              varchar,
    address                varchar,
    status                 varchar
);
');
insert into rdos_batch_task_template(task_type,type,content)
value(15,2,'
create table if not exists exam_dwd_sales_ord_df (
    order_header_id     varchar,
    order_detail_id     varchar,
    order_date          bigint,
    pay_date            bigint,
    shop_id             bigint,
    customer_id         varchar,
    item_id             varchar,
    quantity            varchar,
    unit_price          varchar,
    amount              varchar
);

INSERT INTO exam_dwd_sales_ord_df
select
 d.order_header_id
,d.order_detail_id
,h.order_date
,h.pay_date
,h.shop_id
,h.customer_id
,d.item_id
,d.quantity
,d.unit_price
from ods_order_header as h join ods_order_detail as d
on h.order_header_id = d.order_header_id and h.order_status = 0;
');
insert into rdos_batch_task_template(task_type,type,content)
value(15,3,'create table if not exists exam_dws_sales_shop_1d (
     stat_date              string comment ''统计日期''
    ,shop_id                string comment ''订单明细id''
    ,ord_quantity_1d        bigint comment ''最近一天订单数量''
    ,ord_amount_1d          double comment ''最近一天订单金额''
    ,pay_quantity_1d        bigint comment ''最近一天付款数量''
    ,pay_amount_1d          double comment ''最近一天付款金额''
)comment ''最近一天门店粒度销售汇总表''
PARTITIONED BY (ds string) ;

INSERT OVERWRITE TABLE exam_dws_sales_shop_1d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,shop_id
,sum(case when order_date = ''${bdp.system.bizdate}'' then quantity end) as ord_quantity_1d
,sum(case when order_date = ''${bdp.system.bizdate}'' then amount end)   as ord_amount_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then quantity end) as pay_quantity_1d
,sum(case when pay_date = ''${bdp.system.bizdate}''   then amount end)   as pay_amount_1d
from
exam_dwd_sales_ord_df
where ds = ''${bdp.system.bizdate}''
group by shop_id;
');
insert into rdos_batch_task_template(task_type,type,content)
value(15,4,'create table if not exists exam_ads_sales_all_d (
     stat_date              string comment ''统计日期''
    ,ord_quantity           bigint comment ''订单数量''
    ,ord_amount             double comment ''订单金额''
    ,pay_quantity           bigint comment ''付款数量''
    ,pay_amount             double comment ''付款金额''
    ,shop_cnt               bigint comment ''有交易的店铺数量''
)comment ''订单交易总表''
PARTITIONED BY (ds string) lifecycle 7;

INSERT OVERWRITE TABLE exam_ads_sales_all_d PARTITION(ds = ''${bdp.system.bizdate}'')
select
 ''${bdp.system.bizdate}'' as stat_date
,sum(ord_quantity_1d) as ord_quantity
,sum(ord_amount_1d)   as ord_amount
,sum(pay_quantity_1d) as pay_quantity
,sum(pay_amount_1d)   as pay_amount
,count(distinct shop_id) as shop_cnt
from
exam_dws_sales_shop_1d
where ds = ''${bdp.system.bizdate}''
group by shop_id;
');
insert into rdos_batch_task_template(task_type,type,content)
value(15,5,'create table if not exists exam_dim_shop (
     shop_id                string comment ''店铺id''
    ,shop_name              string comment ''店铺名称''
    ,shop_type              string comment ''店铺类型''
    ,address                string comment ''店铺地址''
)comment ''店铺维度表''
PARTITIONED BY (ds string) lifecycle 365;

INSERT OVERWRITE TABLE exam_dim_shop PARTITION(ds = ''${bdp.system.bizdate}'')
select
 shop_id
,shop_name
,shop_type
,address
from exam_ods_shop_info
where ds = ''${bdp.system.bizdate}''
and status = ''open'';
');


INSERT INTO `rdos_batch_sys_parameter` VALUES ('1', 'bdp.system.bizdate', 'yyyyMMdd-1', now(), now(), '0');
INSERT INTO `rdos_batch_sys_parameter` VALUES ('2', 'bdp.system.cyctime', 'yyyyMMddHHmmss', now(), now(), '0');
INSERT INTO `rdos_batch_sys_parameter` VALUES ('3', 'bdp.system.currmonth', 'yyyyMM-0', now(), now(), '0');
INSERT INTO `rdos_batch_sys_parameter` VALUES ('4', 'bdp.system.premonth', 'yyyyMM-1', now(), now(), '0');
INSERT INTO `rdos_batch_sys_parameter` VALUES ('5', 'bdp.system.runtime', '${bdp.system.currenttime}', now(), now(), '0');
INSERT INTO	`rdos_batch_sys_parameter` VALUES ('6', 'bdp.system.bizdate2', 'yyyy-MM-dd,-1', now(), now(), '0');