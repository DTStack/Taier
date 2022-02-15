import { TASK_TYPE_ENUM } from '@/constant';
import molecule from '@dtinsight/molecule';
import { connect } from '@dtinsight/molecule/esm/react';

const Language = connect(molecule.editor, ({ current }: molecule.model.IEditor) => {
	if (!current) return null;

	const renderLanguage = () => {
		const dataType = current.tab?.data?.taskType;
		switch (dataType) {
			case TASK_TYPE_ENUM.SQL: {
				return 'SparkSQL';
			}
			case TASK_TYPE_ENUM.SYNC: {
				return 'DataSync';
			}
			default: {
				return null;
			}
		}
	};

	return <span>{renderLanguage()}</span>;
});

export default Language;
