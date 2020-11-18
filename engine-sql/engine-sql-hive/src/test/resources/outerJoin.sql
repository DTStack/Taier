insert OVERWRITE  table dim.dim_OnlineRewardConfig
select
nvl(b.sortid,a.sortid) sortid
,nvl(b.type,a.type) type
,nvl(b.starttime,a.starttime) starttime
,nvl(b.onlinetime,a.onlinetime) onlinetime
,nvl(b.propid,a.propid) propid
,nvl(b.count,a.count) `count`
,coalesce(b.addtime,a.addtime,date_format('200808','yyyy-MM-dd HH:mm:ss.sss')) as addtime
,nvl(b.kindid,a.kindid) kindid
,nvl(b.rewardtypeid,a.rewardtypeid) rewardtypeid
,nvl(b.status,a.status) status
,nvl(b.rewardlevel,a.rewardlevel) rewardlevel
,nvl(b.memberorder,a.memberorder) memberorder
,case when b.sortid is null then  date_format('200808','yyyy-MM-dd HH:mm:ss.sss')
      when a.sortid is null then  nvl(b.addtime,date_format('200808','yyyy-MM-dd HH:mm:ss.sss'))
      when a.type<>b.type  or  a.starttime<>b.starttime or a.onlinetime<>b.onlinetime or a.propid<>b.propid or a.count<>b.count or a.kindid<>b.kindid or a.rewardtypeid<>b.rewardtypeid  or a.status<>b.status  or a. rewardlevel<>b.rewardlevel or  a.memberorder<>b.memberorder
	  then  date_format('200808','yyyy-MM-dd HH:mm:ss.sss')
     else  a.modifytime end  modifytime
,case when a.sortid is null then 1
      when b.sortid is null then 0
	  else a.valid end as valid
from dim.dim_OnlineRewardConfig a
FULL OUTER JOIN
(select * from ods.ods_t_onlinerewardconfig_df  where etl_date='200808')b
on a.sortid=b.sortid;