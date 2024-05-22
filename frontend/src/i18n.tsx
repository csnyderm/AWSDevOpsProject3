import i18next from "i18next";
import { initReactI18next } from "react-i18next";

//Import all translation files
import translationEnglish from "./translations/English/english.json"
import translationSpanish from "./translations/Spanish/spanish.json"


    const resources = {
        en: { translation : translationEnglish },
        es: { translation : translationSpanish }
    }

    i18next.use(initReactI18next).init({
        resources,
        lng: "en"  // default language
    });

export default i18next;