import { IExtension } from 'molecule/esm/model'
import { ExtendsAccBar } from './activityBar'
import { ExtendsSparkSQL } from './sparksql/extendsSparkSQL'

export const extensions: IExtension[] = [
    new ExtendsAccBar(),
    new ExtendsSparkSQL()
]
