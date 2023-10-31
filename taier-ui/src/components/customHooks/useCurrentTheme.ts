import { useEffect,useState } from 'react';
import molecule from '@dtinsight/molecule';

const useCurrentTheme = () => {
    const [theme, setTheme] = useState<molecule.model.ColorThemeMode>(molecule.colorTheme.getColorThemeMode());

    useEffect(() => {
        molecule.colorTheme.onChange((_, __, themeMode) => {
            setTheme(themeMode);
        });
    }, []);

    return [theme];
};

export default useCurrentTheme;
