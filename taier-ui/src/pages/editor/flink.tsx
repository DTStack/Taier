import molecule from '@dtinsight/molecule';
import { connect } from '@dtinsight/molecule/esm/react';

import Create from '@/components/task/create';
import taskSaveService from '@/services/taskSaveService';

const Flink = connect(molecule.editor, ({ current }: molecule.model.IEditor) => {
    if (!current) {
        return null;
    }

    const handleSubmit = () => {
        return new Promise<boolean>((resolve) => {
            taskSaveService.save().finally(() => {
                resolve(false);
            });
        });
    };

    return <Create record={current.tab?.data} isRequest={false} onSubmit={handleSubmit} />;
});

export default Flink;
