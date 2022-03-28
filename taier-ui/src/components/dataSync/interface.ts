import type { BINARY_ROW_KEY_FLAG, DATA_SOURCE_ENUM } from '@/constant';

/**
 * 前端表单保存的值
 */
export interface ISourceFormField {
	sourceId?: number;
	table?: string | string[];
	/**
	 * Only used in Oracle and PostgreSQL
	 */
	schema?: string;
	where?: string;
	splitPK?: string;
	extralConfig?: string;
	increColumn?: string | number;
	/**
	 * Only used in HDFS
	 */
	path?: string;
	/**
	 * Only used in HDFS
	 */
	fileType?: 'orc' | 'text' | 'parquet';
	/**
	 * Only used in HDFS
	 */
	fieldDelimiter?: string;
	encoding?: 'utf-8' | 'gbk';
	/**
	 * Only used in Hive and SparkShrift
	 */
	partition?: string;
	/**
	 * 开始行健
	 */
	startRowkey?: string;
	/**
	 * 结束行健
	 */
	endRowkey?: string;
	/**
	 * 行健二进制转换
	 */
	isBinaryRowkey?: Valueof<typeof BINARY_ROW_KEY_FLAG>;
	/**
	 * 每次 RPC 请求获取行数
	 */
	scanCacheSize?: number;
	/**
	 * 每次 RPC 请求获取列数
	 */
	scanBatchSize?: number;
	/**
	 * 索引
	 */
	index?: string;
	/**
	 * 索引类型
	 */
	indexType?: string;
	query?: string;
}

export interface ITargetFormField {
	sourceId?: number;
	table?: string;
	preSql?: string;
	postSql?: string;
	schema?: string;
	extralConfig?: string;
	partition?: string;
	path?: string;
	fileName?: string;
	fileType?: 'orc' | 'text' | 'parquet';
	fieldDelimiter?: string;
	encoding?: 'utf-8' | 'gbk';
	writeMode?: 'NONCONFLICT' | 'APPEND' | 'insert' | 'replace' | 'update';
	nullMode?: 'skip' | 'empty';
	writeBufferSize?: number;
	index?: string;
	indexType?: string;
	bulkAction?: number;
}

/**
 * 传给后端的值
 */
export interface ISourceMapProps {
	name?: string;
	sourceId?: number;
	column?: IDataColumnsProps[];
	increColumn?: string | number;
	sourceList?: {
		key: string;
		tables?: string[] | string;
		type: DATA_SOURCE_ENUM;
		name: string;
		sourceId?: number;
	}[];
	extralConfig?: string;
	schema?: string;
	type?: Omit<ISourceFormField, 'sourceId'> & { type?: DATA_SOURCE_ENUM };

	[key: string]: any;
}

export interface ITargetMapProps {
	sourceId?: number;
	name?: string;
	extralConfig?: string;
	column?: IDataColumnsProps[];
	type?: Omit<ITargetFormField, 'sourceId'> & { type?: DATA_SOURCE_ENUM; rowkey?: string };

	[key: string]: any;
}

export interface IKeyMapProps {
	source: IDataColumnsProps[];
	target: IDataColumnsProps[];
}

export interface IDataColumnsProps {
	comment?: string;
	isPart?: boolean;
	key: string | number;
	part?: boolean;
	type: string;
	value?: string;
	index?: string;
	cf?: string;
	format?: string;
}

export interface IChannelFormProps {
	speed: string;
	channel: string;
	record?: number;
	percentage?: number;
	isRestore?: boolean;
	isSaveDirty?: boolean;
	tableName?: string;
	lifeDay?: string | number;
	restoreColumnName?: string | number;
}
