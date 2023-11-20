/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Editor from '@/components/editor';
import type { IRightBarComponentProps } from '@/services/rightBarService';
import { updateValuesInData } from '../editor/dataSync';

export default function EnvParams({ current }: IRightBarComponentProps) {
    const handleValueChanged = (value: string) => {
        if (current?.tab) {
            updateValuesInData({ taskParams: value });
        }
    };

    return (
        <Editor
            value={current?.tab?.data.taskParams || ''}
            language="ini"
            options={{
                automaticLayout: true,
                minimap: { enabled: false },
            }}
            onChange={handleValueChanged}
        />
    );
}
