package com.createver.server.global.util.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringListConverterTest {

    private StringListConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringListConverter();
    }

    @Test
    @DisplayName("리스트를 JSON 문자열로 정상 변환")
    void convertListToJsonString() {
        List<String> dataList = Arrays.asList("item1", "item2", "item3");
        String jsonResult = converter.convertToDatabaseColumn(dataList);

        // 예상되는 JSON 문자열
        String expectedJson = "[\"item1\",\"item2\",\"item3\"]";
        assertEquals(expectedJson, jsonResult);
    }

    @Test
    @DisplayName("JSON 문자열을 리스트로 정상 변환")
    void convertJsonStringToList() {
        String json = "[\"item1\",\"item2\",\"item3\"]";
        List<String> resultList = converter.convertToEntityAttribute(json);

        // 예상되는 리스트
        List<String> expectedList = Arrays.asList("item1", "item2", "item3");
        assertEquals(expectedList, resultList);
    }

    @Test
    @DisplayName("null 또는 빈 데이터 처리")
    void handleNullOrEmptyData() {
        // DB 데이터가 null일 때
        assertTrue(converter.convertToEntityAttribute(null).isEmpty());

        // DB 데이터가 빈 문자열일 때
        assertTrue(converter.convertToEntityAttribute("").isEmpty());

        // 리스트가 null일 때
        assertEquals(null, converter.convertToDatabaseColumn(null));

        // 리스트가 비어있을 때
        assertEquals(null, converter.convertToDatabaseColumn(Collections.emptyList()));
    }
}
