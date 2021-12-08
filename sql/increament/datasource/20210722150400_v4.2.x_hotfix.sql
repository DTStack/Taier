-- hbase提示信息的文案修改
update dsc_form_field set place_hold = "集群地址，例如：IP1:Port,IP2:Port,IP3:Port"
where type_version like "Hbase%" and `name` = "hbase_quorum";