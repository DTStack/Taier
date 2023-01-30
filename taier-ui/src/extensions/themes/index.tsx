import molecule from '@dtinsight/molecule';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IExtension } from '@dtinsight/molecule/esm/model';

export default class ColorThemeExtensions implements IExtension {
    id: UniqueId = 'colorTheme';
    name = 'color theme';
    activate(): void {
        import('./githubPlus.json').then((content) => {
            const builtinTheme = molecule.colorTheme.getThemeById(content.id);
            if (builtinTheme) {
                molecule.colorTheme.updateTheme({
                    ...builtinTheme,
                    id: content.id,
                    label: `${builtinTheme.label}(推荐)`,
                    colors: {
                        ...builtinTheme.colors,
                        ...content.colors,
                    },
                    tokenColors: [...builtinTheme.tokenColors!, ...content.tokenColors],
                });
            }
        });

        import('./defaultDark.json').then((content) => {
            const builtinTheme = molecule.colorTheme.getThemeById(content.id);
            if (builtinTheme) {
                molecule.colorTheme.updateTheme({
                    ...builtinTheme,
                    colors: {
                        ...builtinTheme.colors,
                        ...content.colors,
                    },
                });
            }
        });

        import('./defaultLight.json').then((content) => {
            const builtinTheme = molecule.colorTheme.getThemeById(content.id);
            if (builtinTheme) {
                molecule.colorTheme.updateTheme({
                    ...builtinTheme,
                    colors: {
                        ...builtinTheme.colors,
                        ...content.colors,
                    },
                });
            }
        });
    }
    dispose(): void {
        throw new Error('Method not implemented.');
    }
}
