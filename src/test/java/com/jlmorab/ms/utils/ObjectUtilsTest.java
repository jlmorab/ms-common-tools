package com.jlmorab.ms.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlmorab.ms.data.TestData;

class ObjectUtilsTest {

	@Test
	void getJsonString_withReturnData() {
		String expected = TestData.getRandomJsonObject();
		try( MockedConstruction<ObjectMapper> mockConstructor = Mockito.mockConstruction(ObjectMapper.class, (mock, context) -> {
			Mockito.when(mock.writeValueAsString(any())).thenReturn( expected );
		})) {
			String actual = ObjectUtils.getJsonString(new Object());
			
			assertEquals( expected, actual );
		}//end try
	}//end getJsonString_withReturnData()
	
	@Test
	void getJsonString_whenThrowException() {
		Exception exception = mock(JsonProcessingException.class);
		String expected = "{}";
		try( MockedConstruction<ObjectMapper> mockConstructor = Mockito.mockConstruction(ObjectMapper.class, (mock, context) -> {
			Mockito.when(mock.writeValueAsString(any())).thenThrow(exception);
		})) {
			String actual = ObjectUtils.getJsonString(new Object());
			
			assertEquals( expected, actual );
		}//end try
	}//end getJsonString_withReturnData()

}
