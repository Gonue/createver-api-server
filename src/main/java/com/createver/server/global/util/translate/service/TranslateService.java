package com.createver.server.global.util.translate.service;

import com.createver.server.global.util.translate.LanguageDiscriminationUtils;
import com.createver.server.global.util.translate.Translate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslateService {

    private final Translate translate;

    public String translateIfKorean(String text) {
        if (LanguageDiscriminationUtils.isKorean(text)) {
            return translate.translate(text, "ko", "en");
        }
        return text;
    }
}
