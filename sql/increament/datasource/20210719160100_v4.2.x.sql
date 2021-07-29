-- 修改MySQL文案
update dsc_form_field set tooltip = '示例：jdbc:mysql://host:3306/dbName' where type_version = 'MySQL' and `name` = 'jdbcUrl';