export const SUPPORTED_LANGUAGES = ["en", "pl"] as const;

export type SupportedLanguage = (typeof SUPPORTED_LANGUAGES)[number];

export const DEFAULT_LANGUAGE: SupportedLanguage = "pl";

export const LANGUAGE_NAMES: Record<SupportedLanguage, string> = {
  en: "English",
  pl: "Polski",
};
