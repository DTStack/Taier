import { createContext } from 'react';

export const Context = createContext<{
    optionCollections: Record<string, any[]>;
    dispatch: React.Dispatch<{
        type: 'update';
        payload: {
            field: string;
            collection: any[];
        };
    }>;
    transformerFactory: Record<string, (value: any, index: number, array: any[]) => any | undefined>;
}>({ optionCollections: {}, dispatch: () => {}, transformerFactory: {} });
