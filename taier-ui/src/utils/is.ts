// TODO 后续 is 相关的判断方法都放该文件下
import { DATA_SOURCE_ENUM } from '@/constant';

export const isSchemaRequired = (type?: DATA_SOURCE_ENUM) => {
	return !!(
		type &&
		[
			DATA_SOURCE_ENUM.POSTGRESQL,
			DATA_SOURCE_ENUM.KINGBASE8,
			DATA_SOURCE_ENUM.SQLSERVER,
			DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
		].includes(type)
	);
};
