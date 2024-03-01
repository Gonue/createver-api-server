package com.createver.server.global.util.aws.service;

import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Translate Service 테스트")
@ExtendWith(MockitoExtension.class)
class TranslateServiceTest {

    @InjectMocks
    private TranslateService translateService;

    @Mock
    private AmazonTranslate amazonTranslate;

    @Test
    @DisplayName("한국어 텍스트 입력 시 영어로 번역")
    void translateIfKorean_withKoreanText_translatesText() {
        // Given
        String koreanText = "안녕하세요";
        String translatedText = "Hello";
        TranslateTextResult mockResult = new TranslateTextResult();
        mockResult.setTranslatedText(translatedText);
        when(amazonTranslate.translateText(any(TranslateTextRequest.class))).thenReturn(mockResult);

        // When
        String result = translateService.translateIfKorean(koreanText);

        // Then
        assertEquals(translatedText, result);
        verify(amazonTranslate, times(1)).translateText(any(TranslateTextRequest.class));
    }

    @Test
    @DisplayName("영어 텍스트 입력 시 번역 없이 반환")
    void translateIfKorean_withEnglishText_returnsSameText() {
        // Given
        String englishText = "Hello";

        // When
        String result = translateService.translateIfKorean(englishText);

        // Then
        assertEquals(englishText, result);
        verify(amazonTranslate, never()).translateText(any(TranslateTextRequest.class));
    }

    @Test
    @DisplayName("영어 알파벳과 공백 이외의 문자가 포함된 경우 번역 실행")
    void translateIfKorean_withNonEnglishCharacters_translatesText() {
        // Given
        String mixedText = "Hello, 안녕하세요!";
        String translatedText = "Hello, Hello!";
        TranslateTextResult mockResult = new TranslateTextResult();
        mockResult.setTranslatedText(translatedText);
        when(amazonTranslate.translateText(any(TranslateTextRequest.class))).thenReturn(mockResult);

        // When
        String result = translateService.translateIfKorean(mixedText);

        // Then
        assertEquals(translatedText, result);
        verify(amazonTranslate, times(1)).translateText(any(TranslateTextRequest.class));
    }


}