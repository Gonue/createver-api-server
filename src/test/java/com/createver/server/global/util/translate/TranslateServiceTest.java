package com.createver.server.global.util.translate;

import com.createver.server.global.util.translate.service.TranslateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Translate Service 테스트")
@ExtendWith(MockitoExtension.class)
class TranslateServiceTest {

    @Mock
    private Translate translate;

    @InjectMocks
    private TranslateService translateService;

    @Test
    @DisplayName("한국어 텍스트 번역")
    void translateIfKorean_KoreanText() {
        // 한국어 텍스트 "안녕하세요"가 "Hello"로 번역되도록 설정
        when(translate.translate("안녕하세요", "ko", "en")).thenReturn("Hello");
        String koreanText = "안녕하세요";
        String translatedText = translateService.translateIfKorean(koreanText);

        assertEquals("Hello", translatedText, "한국어 텍스트는 번역되어야 합니다.");
        verify(translate, times(1)).translate(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("비한국어 텍스트 번역하지 않음")
    void translateIfKorean_NonKoreanText() {
        // 비한국어 텍스트는 번역하지 않고 그대로 반환해야 합니다.
        String nonKoreanText = "Hello";
        String resultText = translateService.translateIfKorean(nonKoreanText);

        assertEquals("Hello", resultText, "비한국어 텍스트는 번역하지 않고 그대로 반환해야 합니다.");
        verify(translate, never()).translate(anyString(), anyString(), anyString());
    }
}