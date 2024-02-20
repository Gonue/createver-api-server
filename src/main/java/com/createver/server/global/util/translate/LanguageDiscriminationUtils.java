package com.createver.server.global.util.translate;

public class LanguageDiscriminationUtils {

    private LanguageDiscriminationUtils() {
    }
    public static String translateIfKorean(String prompt, Translate translate) {
        if (isKorean(prompt)) {
            return translate.translate(prompt, "ko", "en");
        }
        return prompt;
    }

    public static boolean isKorean(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                return true;
            }
        }
        return false;
    }
}
