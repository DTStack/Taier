import molecule from 'molecule'
import { IActivityBarItem, IExtension, ISidebarPane } from 'molecule/esm/model'
import 'monaco-sql-languages/out/esm/sparksql/sparksql.contribution'
import { Button } from 'molecule/esm/components'

(window as any).MonacoEnvironment = {
    getWorkerUrl: function (moduleId: string, label: string) {
        switch (label) {
            case 'sparksql': {
                return './sparksql.worker.js'
            }
            case 'flinksql': {
                return './flinksql.worker.js'
            }
            case 'hivesql': {
                return './hivesql.worker.js'
            }
            case 'mysql': {
                return './mysql.worker.js'
            }
            case 'plsql': {
                return './plsql.worker.js'
            }
            case 'sql': {
                return './sql.worker.js'
            }
            default: {
                return './editor.worker.js'
            }
        }
    }
}

const TestActiveBar: IActivityBarItem = {
    id: 'TestActiveBar',
    name: 'test',
    icon: 'beaker'
}

const TestSidebarPane: ISidebarPane = {
    id: TestActiveBar.id,
    title: 'TestSidebarPane',
    render () {
        const open = () => {
            molecule.editor.open({
                id: `spark${Date.now()}.sql`,
                name: `spark${Date.now()}.sql`,
                data: {
                    value: 'select * from tb_test',
                    language: 'sparksql'
                }
            })
        }
        return (
            <div style={{ marginTop: '10px' }}>
                <Button onClick={open}>Open Editor</Button>
            </div>
        )
    }
}

export class ExtendsSparkSQL implements IExtension {
    activate (extensionCtx: molecule.IExtensionService): void {
        molecule.activityBar.add(TestActiveBar, true)
        // molecule.activityBar.onClick((current) => {
        //     if (current === TestActiveBar.id) {
        //         molecule.sidebar.setActive(TestSidebarPane.id)
        //     }
        // })
        molecule.sidebar.add(TestSidebarPane, true)
    }
}
