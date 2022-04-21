import { container } from 'tsyringe';
import EditorActionBarService from './editorActionBarService';
import ExecuteService from './executeService';

const editorActionBarService = container.resolve(EditorActionBarService);
const executeService = container.resolve(ExecuteService);

export { editorActionBarService, executeService };
