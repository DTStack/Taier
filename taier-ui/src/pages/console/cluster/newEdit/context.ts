import { createContext, useContext } from 'react';
import type { FormInstance } from 'antd';

export const FormContext = createContext<FormInstance | null>(null);

export const useContextForm = () => {
    const form = useContext(FormContext)!;
    return form;
};
