import Create from '@/components/task/create';
import molecule from '@dtinsight/molecule';

// 虚节点
export default function Virtual(props: any) {
	return <Create {...props} record={molecule.folderTree.getState().folderTree?.current} />;
}
