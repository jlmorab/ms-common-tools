package com.jlmorab.ms.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class LogicExceptionTest {

	private static final String  	TEXT_EXCEPTION = "Some text exception";
	private static final Exception  TEST_EXCEPTION = new IllegalArgumentException(TEXT_EXCEPTION);
	private static final HttpStatus TEST_HTTP_STATUS = HttpStatus.ACCEPTED;
	
	@Test
	void exception_withHttp() {
		LogicException actual = new LogicException( TEST_HTTP_STATUS );
		assertNotNull( actual );
		assertEquals( TEST_HTTP_STATUS, actual.getHttpStatus() );
	}//end exception_withHttp()
	
	@Test
	void exception_withMessage() {
		LogicException actual = new LogicException( TEXT_EXCEPTION );
		assertNotNull( actual );
		assertEquals( TEXT_EXCEPTION, actual.getMessage() );
	}//end exception_withMessage()
	
	@Test
	void exception_withMessageAndHttp() {
		LogicException actual = new LogicException( TEXT_EXCEPTION, TEST_HTTP_STATUS );
		assertNotNull( actual );
		assertEquals( TEST_HTTP_STATUS, actual.getHttpStatus() );
		assertEquals( TEXT_EXCEPTION, actual.getMessage() );
	}//end exception_withMessageAndHttp()
	
	@Test
	void exception_withReference() {
		LogicException actual = new LogicException( TEST_EXCEPTION );
		assertNotNull( actual );
		assertEquals( TEST_EXCEPTION, actual.getCause() );
	}//end exception_withReference()
	
	@Test
	void exception_withReferenceAndHttp() {
		LogicException actual = new LogicException( TEST_EXCEPTION, TEST_HTTP_STATUS );
		assertNotNull( actual );
		assertEquals( TEST_EXCEPTION, actual.getCause() );
		assertEquals( TEST_HTTP_STATUS, actual.getHttpStatus() );
	}//end exception_withReferenceAndHttp()
	
	@Test
	void exception_withMessageAndReference() {
		LogicException actual = new LogicException( TEXT_EXCEPTION, TEST_EXCEPTION );
		assertNotNull( actual );
		assertEquals( TEXT_EXCEPTION, actual.getMessage() );
		assertEquals( TEST_EXCEPTION, actual.getCause() );
	}//end exception_withMessageAndReference()
	
	@Test
	void exception_withAllArgs() {
		LogicException actual = new LogicException( TEXT_EXCEPTION, TEST_EXCEPTION, TEST_HTTP_STATUS );
		assertNotNull( actual );
		assertEquals( TEXT_EXCEPTION, actual.getMessage() );
		assertEquals( TEST_EXCEPTION, actual.getCause() );
		assertEquals( TEST_HTTP_STATUS, actual.getHttpStatus() );
	}//end exception_withAllArgs()

}
