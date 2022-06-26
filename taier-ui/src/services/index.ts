import { container } from 'tsyringe';
import EditorActionBarService from './editorActionBarService';
import ExecuteService from './executeService';
import CatalogueService from './catalogueService';
import BreadcrumbService from './breadcrumbService';
import RightBarService from './rightBarService';

const editorActionBarService = container.resolve(EditorActionBarService);
const executeService = container.resolve(ExecuteService);
const catalogueService = container.resolve(CatalogueService);
const breadcrumbService = container.resolve(BreadcrumbService);
const rightBarService = container.resolve(RightBarService);

export {
	editorActionBarService,
	catalogueService,
	executeService,
	breadcrumbService,
	rightBarService,
};
