import molecule from '@dtinsight/molecule';
import Create from '@/components/task/create';
import {connect} from '@dtinsight/molecule/esm/react';
import taskSaveService from '@/services/taskSaveService';
import type {IOfflineTaskProps} from '@/interface';

// 虚节点
const Virtual = connect(molecule.editor, ({current}: molecule.model.IEditor) => {
	const handleSubmit = () => {
		return new Promise<boolean>((resolve) => {
			taskSaveService.save().finally(() => {
				resolve(false);
			});
		});
	};

	if (!current) {
		return null;
	}

	const isWorkflow = !!(current.tab?.data as IOfflineTaskProps).flowId;

	return (
		<Create
			record={current.tab?.data}
			isRenderPosition={!isWorkflow}
			isRequest={false}
			onSubmit={handleSubmit}
		/>
	);
});

export default Virtual;
