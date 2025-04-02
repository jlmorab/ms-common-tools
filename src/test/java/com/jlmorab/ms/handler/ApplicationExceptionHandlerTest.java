package com.jlmorab.ms.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.jlmorab.ms.data.dto.WebResponseDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

class ApplicationExceptionHandlerTest {

	private static final String TEST_REASON = "any-reason";
	
	private ApplicationExceptionHandler handler;
	
	@BeforeEach
	void setUp() {
		handler = new ApplicationExceptionHandler();
	}//end setUp()

	@Test
	void handleResponseStatusException_shouldReturnCorrectResponseEntity() {
		ResponseStatusException exception = new ResponseStatusException( 
				HttpStatusCode.valueOf( HttpStatus.NOT_FOUND.value() ), TEST_REASON );
		
		ResponseEntity<WebResponseDTO> actual = handler.handleResponseStatusException( exception );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.NOT_FOUND, actual.getStatusCode() );
		assertEquals( TEST_REASON, actual.getBody().getMeta().getMessages().get(0) );
	}//end handleResponseStatusException_shouldReturnCorrectResponseEntity()
	
	@ParameterizedTest
	@MethodSource("provideExceptionsHandleResponseStatusException")
	void handleResponseStatusException_shouldReturnCorrectResponseEntity( Exception exception, String messageExpected ) {
		ResponseEntity<WebResponseDTO> actual = handler.handleMethodArgumentNotValid( exception );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.BAD_REQUEST, actual.getStatusCode() );
		assertEquals( messageExpected, actual.getBody().getMeta().getMessages().get(0) );
	}//end handleResponseStatusException_shouldReturnCorrectResponseEntity()
	
	static Stream<Arguments> provideExceptionsHandleResponseStatusException() {
		MethodArgumentNotValidException methodArgException = mock( MethodArgumentNotValidException.class );
	    BindingResult bindingResult = mock( BindingResult.class );
	    FieldError fieldError = new FieldError("object", "field", "message");
	    
	    when( methodArgException.getFieldErrors() ).thenReturn( List.of( fieldError ) );
	    when( methodArgException.getBindingResult() ).thenReturn( bindingResult );
	    when( bindingResult.getFieldErrors() ).thenReturn( List.of( fieldError ) );
	    
	    ConstraintViolationException constraintException = mock( ConstraintViolationException.class );
	    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
	    Path path = mock(Path.class);
	    
	    when( violation.getPropertyPath() ).thenReturn( path );
	    when( path.toString() ).thenReturn("property");
	    when( violation.getMessage() ).thenReturn( TEST_REASON );
	    when( constraintException.getConstraintViolations() ).thenReturn( Set.of( violation ) );
	    
	    IllegalArgumentException illegalArgException = new IllegalArgumentException("Illegal argument");
	    IllegalStateException illegalStateException = new IllegalStateException("Illegal state");
	    NullPointerException nullException = new NullPointerException("Null value");
	    
	    return Stream.of(
	    		Arguments.of( methodArgException, "field: message" ),
	    		Arguments.of( constraintException, "property: " + TEST_REASON ),
	    		Arguments.of( illegalArgException, illegalArgException.getMessage() ),
	    		Arguments.of( illegalStateException, illegalStateException.getMessage() ),
	    		Arguments.of( nullException, nullException.getMessage() )
	    		);
	}//end provideExceptionsHandleResponseStatusException()
	
	@Test
	void handleResponseStatusException_withHttpException_shouldReturnCorrectResponseEntity() {
		HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException( TEST_REASON );
		
		ResponseEntity<WebResponseDTO> actual = handler.handleResponseStatusException( exception );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.METHOD_NOT_ALLOWED, actual.getStatusCode() );
		assertEquals( exception.getMessage(), actual.getBody().getMeta().getMessages().get(0) );
	}//end handleResponseStatusException_withHttpException_shouldReturnCorrectResponseEntity()
	
	@Test
	void handleHttpMessageNotReadableException_shouldReturnCorrectResponseEntity() {
		HttpMessageNotReadableException exception = mock( HttpMessageNotReadableException.class );
		when( exception.getMessage() ).thenReturn( TEST_REASON );
		
		ResponseEntity<WebResponseDTO> actual = handler.handleHttpMessageNotReadableException( exception );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.BAD_REQUEST, actual.getStatusCode() );
		assertEquals( exception.getMessage(), actual.getBody().getMeta().getMessages().get(0) );
	}//end handleHttpMessageNotReadableException_shouldReturnCorrectResponseEntity()
	
	@Test
	void handleNoResourceFoundException_shouldReturnCorrectResponseEntity() {
		NoResourceFoundException exception = mock( NoResourceFoundException.class );
		when( exception.getMessage() ).thenReturn( TEST_REASON );
		
		ResponseEntity<WebResponseDTO> actual = handler.handleNoResourceFoundException( exception );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.NOT_FOUND, actual.getStatusCode() );
		assertEquals( exception.getMessage(), actual.getBody().getMeta().getMessages().get(0) );
	}//end handleNoResourceFoundException_shouldReturnCorrectResponseEntity()
	
	@Test
	void handleGenericException_shouldReturnCorrectResponseEntity() {
		Exception exception = new RuntimeException( TEST_REASON );
		
		ResponseEntity<WebResponseDTO> actual = handler.handleGenericException( exception );
		
		assertNotNull( actual );
		assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, actual.getStatusCode() );
		assertEquals( exception.getMessage(), actual.getBody().getMeta().getMessages().get(0) );
	}//end handleGenericException_shouldReturnCorrectResponseEntity()
}
