package com.jlmorab.ms.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.jlmorab.ms.data.dto.MetaDTO;
import com.jlmorab.ms.data.dto.WebResponseDTO;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApplicationExceptionHandler {
	
	@ExceptionHandler( ResponseStatusException.class )
	public ResponseEntity<WebResponseDTO> handleResponseStatusException( ResponseStatusException exception ) {
		return executeFlow(
				HttpStatus.valueOf( exception.getStatusCode().value() ) ,
				List.of( exception.getReason() ) );
	}//end handleResponseStatusException()
	
	@ExceptionHandler( { MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class } )
	public ResponseEntity<WebResponseDTO> handleMethodArgumentNotValid( Exception exception ) {
		return customArgumentResponse( exception );
	}//end handleMethodArgumentNotValid()
	
	@ExceptionHandler( HttpRequestMethodNotSupportedException.class )
	public ResponseEntity<WebResponseDTO> handleResponseStatusException( HttpRequestMethodNotSupportedException exception ) {
		return executeFlow(
				HttpStatus.METHOD_NOT_ALLOWED ,
				List.of( exception.getMessage() ) );
	}//end handleResponseStatusException()
	
	@ExceptionHandler( HttpMessageNotReadableException.class )
	public ResponseEntity<WebResponseDTO> handleHttpMessageNotReadableException( HttpMessageNotReadableException exception ) {
		return executeFlow(
				HttpStatus.BAD_REQUEST ,
				List.of( exception.getMessage() ) );
	}//end handleHttpMessageNotReadableException()
	
	@ExceptionHandler( NoResourceFoundException.class )
	public ResponseEntity<WebResponseDTO> handleNoResourceFoundException( NoResourceFoundException exception ) {
		return executeFlow(
				HttpStatus.NOT_FOUND ,
				List.of( exception.getMessage() ) );
	}//end handleNoResourceFoundException()
	
	@ExceptionHandler( Exception.class )
	public ResponseEntity<WebResponseDTO> handleGenericException( Exception exception ) {
		log.error(exception.getMessage(), exception);
		return executeFlow(
				HttpStatus.INTERNAL_SERVER_ERROR,
				List.of( exception.getMessage() ) );
	}//end handleGenericException()
	

	private ResponseEntity<WebResponseDTO> customArgumentResponse( Exception exception ) {
		
		List<String> errors = switch( exception ) {
			case MethodArgumentNotValidException ex ->
				ex.getFieldErrors().stream()
			        .map(violation -> violation.getField() + ": " + violation.getDefaultMessage())
			        .toList();
			case ConstraintViolationException ex ->
				ex.getConstraintViolations().stream()
		            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
		            .toList();
			default -> List.of( exception.getMessage() );
		};//end switch
		
		return executeFlow(
				HttpStatus.BAD_REQUEST,
				errors );
		
	}//end customArgumentResponse()
	
	private WebResponseDTO buildResponse( HttpStatus httpStatus, List<String> messages ) {
		MetaDTO meta = new MetaDTO( httpStatus );
		meta.setMessages( messages );
		return WebResponseDTO.builder()
			.meta( meta )
			.build();
	}//end buildResponse()
	
	private ResponseEntity<WebResponseDTO> executeFlow( HttpStatus httpStatus, List<String> messages ) {
		WebResponseDTO response = buildResponse( httpStatus, messages );
		log.error("ApplicationExceptionHandler: {}", messages);
		return new ResponseEntity<>( response, httpStatus );
	}//end executeFlow()

}
