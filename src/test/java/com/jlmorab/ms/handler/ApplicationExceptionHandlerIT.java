package com.jlmorab.ms.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@WebMvcTest( ApplicationExceptionHandlerIT.TestController.class )
@Import( ApplicationExceptionHandler.class )
class ApplicationExceptionHandlerIT {
	
	private static final String TEST_REASON = "any-reason";

	@RestController
	@RequestMapping("/test")
	@RequiredArgsConstructor
	static class TestController {
		private final TestService service;
		
		@GetMapping
		public String getAnyEndpoint() {
			return service.process();
		}//end getAnyEndpoint()
		
		@PostMapping
		public String postAnyEndpoint(@RequestBody @Valid TestDataDTO request) {
			return service.process();
		}//end postAnyEndpoint()
	}//end TestController()
	
	interface TestService {
		String process();
	}//end TestService()

	@Data
	static class TestDataDTO {
		@NotBlank(message = "Field is required")
	    private String name;
	}//end TestDataDTO()
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private TestService service;
	
	@ParameterizedTest
	@MethodSource("provideExceptionsApplicationExceptionHandler")
	void shouldCatchException( Exception exception, HttpStatus httpStatus, String messageResponse ) throws Exception {
		when( service.process() ).thenThrow( exception );
		
		mockMvc.perform(get("/test"))
			.andExpect(status().is( httpStatus.value() ))
			.andExpect(jsonPath("$.meta.statusCode").value( httpStatus.value() ))
			.andExpect(jsonPath("$.meta.messages[0]").value( messageResponse ));
	}//end shouldCatchException()
	
	static Stream<Arguments> provideExceptionsApplicationExceptionHandler() {
		// HttpStatus.NOT_FOUND
		ResponseStatusException statusException = new ResponseStatusException( 
				HttpStatusCode.valueOf( HttpStatus.NOT_FOUND.value() ), TEST_REASON );
		
		// HttpStatus.BAD_REQUEST	    
	    ConstraintViolationException constraintException = mock( ConstraintViolationException.class );
	    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
	    Path path = mock(Path.class);
	    
	    when( violation.getPropertyPath() ).thenReturn( path );
	    when( path.toString() ).thenReturn("property");
	    when( violation.getMessage() ).thenReturn( TEST_REASON );
	    when( constraintException.getConstraintViolations() ).thenReturn( Set.of( violation ) );
	    
	    IllegalArgumentException illegalArgException = new IllegalArgumentException( TEST_REASON );
	    IllegalStateException illegalStateException = new IllegalStateException( TEST_REASON );
	    NullPointerException nullException = new NullPointerException( TEST_REASON );
	    
	    // HttpStatus.BAD_REQUEST
	    HttpMessageNotReadableException notReadableException = mock( HttpMessageNotReadableException.class );
		when( notReadableException.getMessage() ).thenReturn( TEST_REASON );
		
		// HttpStatus.INTERNAL_SERVER_ERROR
		RuntimeException runtimeException = new RuntimeException( TEST_REASON );
		
		return Stream.of(
				Arguments.of( statusException, HttpStatus.NOT_FOUND, TEST_REASON ),
				Arguments.of( constraintException, HttpStatus.BAD_REQUEST, "property: " + TEST_REASON ), 
				Arguments.of( illegalArgException, HttpStatus.BAD_REQUEST, TEST_REASON ), 
				Arguments.of( illegalStateException, HttpStatus.BAD_REQUEST, TEST_REASON ),
				Arguments.of( nullException, HttpStatus.BAD_REQUEST, TEST_REASON ),
				Arguments.of( notReadableException, HttpStatus.BAD_REQUEST, TEST_REASON ),
				Arguments.of( runtimeException, HttpStatus.INTERNAL_SERVER_ERROR, TEST_REASON )
				);
	}//end provideExceptionsApplicationExceptionHandler()
	
	@Test
	void shouldCatchMethodArgumentNotValidExceptionFromRequest() throws Exception {
	    String invalidJson = "{}";

	    mockMvc.perform(post("/test")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(invalidJson))
	            .andExpect(status().isBadRequest());
	}//end shouldCatchMethodArgumentNotValidExceptionFromRequest()

	@Test
	void shouldCatchHttpRequestMethodNotSupportedExceptionFromRequest() throws Exception {
	    mockMvc.perform(put("/test"))
	            .andExpect(status().isMethodNotAllowed());
	}//end shouldCatchHttpRequestMethodNotSupportedExceptionFromRequest()

	@Test
	void shouldCatchNoResourceFoundExceptionFromRequest() throws Exception {
	    mockMvc.perform(get("/test/non-existent-endpoint"))
	            .andExpect(status().isNotFound());
	}//end shouldCatchNoResourceFoundExceptionFromRequest()


}
