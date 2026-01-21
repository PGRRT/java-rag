import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";

import { SUPPORTED_LANGUAGES } from "./constants";
import enTranslations from "./locales/en.json";
import plTranslations from "./locales/pl.json";

// Type-safe resources
const resources = {
  en: {
    translation: enTranslations,
  },
  pl: {
    translation: plTranslations,
  },
} as const;

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    fallbackLng: "en",

    // Supported languages
    supportedLngs: SUPPORTED_LANGUAGES,

    // Language detection
    detection: {
      order: ["localStorage", "navigator"],
      lookupLocalStorage: "i18nextLng",
      caches: ["localStorage"],
      excludeCacheFor: ["cimode"],
    },
  });

export default i18n;
