// import { useTranslation } from "react-i18next";
// import { Globe } from "lucide-react";
// import { css } from "@emotion/css";
// import colorPalette from "@/constants/colorPalette";
// import { styles } from "@/constants/styles";
// import {
//   SUPPORTED_LANGUAGES,
//   LANGUAGE_NAMES,
//   type SupportedLanguage,
// } from "@/i18n";

// const LanguageSwitcher = () => {
//   const { i18n } = useTranslation();

//   const changeLanguage = (lang: SupportedLanguage) => {
//     i18n.changeLanguage(lang);
//   };

//   return (
//     <div
//       className={css`
//         display: flex;
//         gap: 8px;
//         align-items: center;
//         padding: 8px 10px;
//       `}
//     >
//       <Globe size={16} color={colorPalette.textMuted} />

//       {SUPPORTED_LANGUAGES.map((lang) => (
//         <button
//           key={lang}
//           onClick={() => changeLanguage(lang)}
//           className={css`
//             padding: 4px 8px;
//             background: ${i18n.language === lang
//               ? colorPalette.backgroundTertiary
//               : "transparent"};
//             color: ${i18n.language === lang
//               ? colorPalette.textActive
//               : colorPalette.textMuted};
//             border: none;
//             border-radius: ${styles.borderRadius.small};
//             cursor: pointer;
//             transition: all 0.2s;
//             font-size: 12px;
//             font-weight: 500;

//             &:hover {
//               background: ${colorPalette.backgroundTertiary};
//               color: ${colorPalette.textActive};
//             }
//           `}
//           title={LANGUAGE_NAMES[lang]}
//         >
//           {lang.toUpperCase()}
//         </button>
//       ))}
//     </div>
//   );
// };

// export default LanguageSwitcher;
