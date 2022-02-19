import { IColorTheme, IExtension } from "@dtinsight/molecule/esm/model";

const TaierColorThemeExtension: IExtension = require('./package.json');

// The below handle for theme extension is temporary,
// we will automatic load the extension package.

// Default
const defaultLight: IColorTheme = require('./themes/light_defaults.json');
const lightPlus: IColorTheme = require('./themes/light_plus.json');
// Merge default light into plus
Object.assign(lightPlus, defaultLight);

const themes = TaierColorThemeExtension.contributes?.themes || [];

// Merge the lightPlus into theme
Object.assign(themes[0], lightPlus);

export { TaierColorThemeExtension };
