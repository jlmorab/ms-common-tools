package com.jlmorab.ms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.jlmorab.ms.data.dto.WebResponseDTO;
import com.jlmorab.ms.exception.LogicException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ServiceAbstractTest {

	private static final String ANY_TEXT = "any-text";
	
	static class TestClass extends ServiceAbstract {}
	
	private TestClass testClass;
	
	@Mock
	HttpServletResponse response;
	
	@BeforeEach
	void setUp() {
		testClass = new TestClass();
	}//end setUp()
	
	@Test
	void responseOK_minimumArgs_shouldReturnCorrectDTO() {
		WebResponseDTO actual = testClass.responseOK( response );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.OK.value(), actual.getMeta().getStatusCode() );
		verify( response ).setStatus( HttpStatus.OK.value() );
	}//end responseOK_minimumArgs_shouldReturnCorrectDTO()
	
	@Test
	void responseOk_withData_shouldReturnCorrectDTO() {
		WebResponseDTO actual = testClass.responseOK( response, ANY_TEXT);
		
		assertNotNull( actual );
		assertEquals( HttpStatus.OK.value(), actual.getMeta().getStatusCode() );
		assertEquals( ANY_TEXT, actual.getData() );
		verify( response ).setStatus( HttpStatus.OK.value() );
	}//end responseOk_withData_shouldReturnCorrectDTO()
	
	@Test
	void responseWithMessage_shouldReturnCorrectDTO() {
		WebResponseDTO actual = testClass.responseWithMessage( response, HttpStatus.BAD_REQUEST, ANY_TEXT);
		
		assertNotNull( actual );
		assertEquals( HttpStatus.BAD_REQUEST.value(), actual.getMeta().getStatusCode() );
		assertEquals( ANY_TEXT, actual.getMeta().getMessages().get(0) );
		verify( response ).setStatus( HttpStatus.BAD_REQUEST.value() );
	}//end responseWithMessage_shouldReturnCorrectDTO()
	
	@Test
	void executeFlow_withHappyPath_shouldReturnCorrectDTO() {
		WebResponseDTO actual = testClass.executeFlow( response, log, () -> ANY_TEXT );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.OK.value(), actual.getMeta().getStatusCode() );
		assertEquals( ANY_TEXT, actual.getData() );
		verify( response ).setStatus( HttpStatus.OK.value() );
	}//end executeFlow_shouldReturnCorrectDTO()
	
	@Test
	void executeFlow_withIllegalArgumentException_shouldReturnCorrectDTO() {
		WebResponseDTO actual = testClass.executeFlow( response, log, () -> { throw new IllegalArgumentException(ANY_TEXT); } );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.BAD_REQUEST.value(), actual.getMeta().getStatusCode() );
		assertEquals( ANY_TEXT, actual.getMeta().getMessages().get(0) );
		verify( response ).setStatus( HttpStatus.BAD_REQUEST.value() );
	}//end executeFlow_withIllegalArgumentException_shouldReturnCorrectDTO()
	
	@Test
	void executeFlow_withLogicException_shouldReturnCorrectDTO() {
		WebResponseDTO actual = testClass.executeFlow( response, log, () -> { throw new LogicException(ANY_TEXT); } );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.value(), actual.getMeta().getStatusCode() );
		assertEquals( ANY_TEXT, actual.getMeta().getMessages().get(0) );
		verify( response ).setStatus( HttpStatus.INTERNAL_SERVER_ERROR.value() );
	}//end executeFlow_withLogicException_shouldReturnCorrectDTO()
	
	@Test
	void executeFlow_withException_shouldReturnCorrectDTO() {
		WebResponseDTO actual = testClass.executeFlow( response, log, () -> { throw new RuntimeException(ANY_TEXT); } );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.INTERNAL_SERVER_ERROR.value(), actual.getMeta().getStatusCode() );
		assertEquals( ANY_TEXT, actual.getMeta().getMessages().get(0) );
		verify( response ).setStatus( HttpStatus.INTERNAL_SERVER_ERROR.value() );
	}//end executeFlow_withException_shouldReturnCorrectDTO()
	
	@Test
	void executeFlow_withNullData_shouldReturnCorrectDTO() {
		WebResponseDTO actual = testClass.executeFlow( response, log, () -> null );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.OK.value(), actual.getMeta().getStatusCode() );
		assertEquals( null, actual.getData() );
		verify( response ).setStatus( HttpStatus.OK.value() );
	}//end executeFlow_withNullData_shouldReturnCorrectDTO()

}
