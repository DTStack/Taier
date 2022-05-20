import { container } from 'tsyringe';
import EditorActionBarService from './editorActionBarService';
import ExecuteService from './executeService';
import CatalogueService from './catalogueService';
import BreadcrumbService from './breadcrumbService';

const editorActionBarService = container.resolve(EditorActionBarService);
const executeService = container.resolve(ExecuteService);
const catalogueService = container.resolve(CatalogueService);
const breadcrumbService = container.resolve(BreadcrumbService);

export { editorActionBarService, catalogueService, executeService, breadcrumbService };
