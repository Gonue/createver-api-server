package com.createver.server.global.util.aws.service;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslateService {

    private final AmazonTranslate amazonTranslate;

    public String translateIfKorean(String text) {
         if (!isEnglish(text)) {
             TranslateTextRequest request = new TranslateTextRequest()
                     .withText(text)
                     .withSourceLanguageCode("ko")
                     .withTargetLanguageCode("en");
             TranslateTextResult result = amazonTranslate.translateText(request);
             return result.getTranslatedText();
         }
         return text;
     }

     private boolean isEnglish(String text) {
         for (char c : text.toCharArray()) {
             if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' ')) {
                 return false;
             }
         }
         return true;
     }
 }
