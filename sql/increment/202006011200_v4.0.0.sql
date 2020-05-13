ALTER table console_component ADD COLUMN hadoop_version VARCHAR(25) DEFAULT ''  COMMENT '组件hadoop版本';
ALTER table console_component ADD COLUMN upload_file_name VARCHAR(50) DEFAULT ''  COMMENT '上传文件zip名称';
ALTER table console_component ADD COLUMN component_template text COMMENT '前端展示模版json';