import Create from '@/components/task/create';
import molecule from '@dtinsight/molecule';

export default function Flink(props: any) {
	return <Create {...props} record={molecule.folderTree.getState().folderTree?.current} />;
}
